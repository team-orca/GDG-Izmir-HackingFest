package com.example.orca.howbeautyiam;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.sql.SQLException;


public class CameraActivity extends AppCompatActivity {
    private static final String CLOUD_VISION_API_KEY = "AIzaSyCeIyDQvsfi1q9RlMHaIbBcuBVowB299cE";
    public static final String FILE_NAME = "temp.jpg";

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;

    private  String Datetime;
    private SharedPreferences mSharedPreferences;
    private String user_name;
    private String stringConverted;
    private TextView mImageDetails;
    private ImageView mMainImage;
    private ListView listView;
    private ArrayList<String> listViewItems= new ArrayList<>();
    private String currentDateTimeString;


    private double score=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        Button upload_button =(Button)findViewById(R.id.finish_button);
        upload_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                score = 0;
                AlertDialog.Builder builder = new AlertDialog.Builder(CameraActivity.this);
                builder
                        .setMessage(R.string.dialog_select_prompt)
                        .setPositiveButton(R.string.dialog_select_gallery, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startGalleryChooser();
                            }
                        })
                        .setNegativeButton(R.string.dialog_select_camera, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startCamera();
                            }
                        });
                builder.create().show();
            }

        });
        mImageDetails = (TextView) findViewById(R.id.image_details);
        mMainImage = (ImageView) findViewById(R.id.main_image);
        mSharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        user_name = mSharedPreferences.getString("username", "Empty");
    }
    public void onClick(View view){
        currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        //new Description().execute();
        finish();

    }

    public void startGalleryChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a photo"),
                GALLERY_IMAGE_REQUEST);
    }

    public void startCamera() {
        if (PermissionUtils.requestPermission(this, CAMERA_PERMISSIONS_REQUEST, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getCameraFile()));
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
        }
    }

    public File getCameraFile() {
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
                uploadImage(data.getData());
            } else if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
                uploadImage(Uri.fromFile(getCameraFile()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults)) {
            startCamera();
        }
    }

    public void uploadImage(Uri uri) throws SQLException {
        if (uri != null) {
            try {
                // scale the image to 800px to save on bandwidth
                Bitmap bitmap = scaleBitmapDown(MediaStore.Images.Media.getBitmap(getContentResolver(), uri), 1200);
                //Bitmap to string
                ByteArrayOutputStream baos=new  ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
                byte [] b=baos.toByteArray();
                stringConverted=Base64.encodeToString(b, Base64.DEFAULT);
                //Get current date
                Calendar c = Calendar.getInstance();
                SimpleDateFormat dateformat = new SimpleDateFormat("dd-MMM-yyyy");
                Datetime = dateformat.format(c.getTime());

                callCloudVision(bitmap);
                mMainImage.setImageBitmap(bitmap);

            } catch (IOException e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
                Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "Image picker gave us a null image.");
            Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
        }
    }

    private void callCloudVision(final Bitmap bitmap) throws IOException, SQLException {
        // Switch text to loading
        mImageDetails.setText(R.string.loading_message);

        // Do the real work in an async task, because we need to use the network anyway
        new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... params) {
                try {
                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(new
                            VisionRequestInitializer(CLOUD_VISION_API_KEY));
                    Vision vision = builder.build();

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                            new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
                        AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

                        // Add the image
                        Image base64EncodedImage = new Image();
                        // Convert the bitmap to a JPEG
                        // Just in case it's a format that Android understands but Cloud Vision
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);

                        byte[] imageBytes = byteArrayOutputStream.toByteArray();

                        // Base64 encode the JPEG
                        base64EncodedImage.encodeContent(imageBytes);
                        annotateImageRequest.setImage(base64EncodedImage);

                        // add the features we want
                        annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                            Feature labelDetection = new Feature();
                            labelDetection.setType("LABEL_DETECTION");
                            labelDetection.setMaxResults(30);
                            add(labelDetection);
                        }});

                        // Add the list of one thing to the request
                        add(annotateImageRequest);
                    }});

                    Vision.Images.Annotate annotateRequest =
                            vision.images().annotate(batchAnnotateImagesRequest);
                    // Due to a bug: requests to Vision API containing large images fail when GZipped.
                    annotateRequest.setDisableGZipContent(true);
                    Log.d(TAG, "created request object, sending request");

                    BatchAnnotateImagesResponse response = annotateRequest.execute();

                    return convertResponseToString(response);



                } catch (GoogleJsonResponseException e) {
                    Log.d(TAG, "failed to make request because " + e.getContent());
                } catch (IOException e) {
                    Log.d(TAG, "failed to make request because of other IOException " +
                            e.getMessage());
                }
                catch(SQLException e){
                    e.printStackTrace();
                    return "SqlException";
                }
                return "Request failed. Check logs for details.";
            }

            protected void onPostExecute(String result) {
                mImageDetails.setText(result);
                //populateListView();
            }
        }.execute();
    }
    public Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    private String convertResponseToString(BatchAnnotateImagesResponse response)throws SQLException{
        String message = "Found these:\n\n";

        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();


        ArrayList<String> visionList = new ArrayList<String>();


        if (labels != null) {

            for (EntityAnnotation label : labels) {
                visionList.add(label.getDescription());
            }
            //} else {
            //   message = "Fotoğraf algılanamadı, tekrar deneyin..";
            //  check = false;
            //}
        }

        if (labels != null) {
            for (EntityAnnotation label : labels) {
                message += String.format("%.3f: %s", label.getScore(), label.getDescription());
                message += "\n";
                if(label.getDescription().equals("model"))
                    score+=20*label.getScore();
                else if(label.getDescription().equals("beauty"))
                    score+=20*label.getScore();
                else if(label.getDescription().equals("lip"))
                    score+=20*label.getScore();
                else if(label.getDescription().equals("eyeleash"))
                    score+=20*label.getScore();
                else if(label.getDescription().equals("lady"))
                    score+=15*label.getScore();
                else if(label.getDescription().equals("woman"))
                    score+=15*label.getScore();
                else if(label.getDescription().equals("girl"))
                    score+=15*label.getScore();
                else if(label.getDescription().equals("dress"))
                    score+=15*label.getScore();
                else if(label.getDescription().equals("fashion"))
                    score+=15*label.getScore();
                else if(label.getDescription().equals("cosmetics"))
                    score+=15*label.getScore();
                else if(label.getDescription().equals("eyeleash"))
                    score+=15*label.getScore();
                else if(label.getDescription().equals("blond"))
                    score+=15*label.getScore();
                else if(label.getDescription().equals("smile"))
                    score+=10*label.getScore();
                else if(label.getDescription().equals("hairstyle"))
                    score+=10*label.getScore();
                else if(label.getDescription().equals("long hair"))
                    score+=10*label.getScore();
                else if(label.getDescription().equals("brown hair"))
                    score+=10*label.getScore();
                else if(label.getDescription().equals("black hair"))
                    score+=10*label.getScore();
                else if(label.getDescription().equals("chest"))
                    score+=10*label.getScore();
                else if(label.getDescription().equals("hair"))
                    score+=10*label.getScore();
                else if(label.getDescription().equals("eye"))
                    score+=10*label.getScore();
                else if(label.getDescription().equals("cheek"))
                    score+=10*label.getScore();

            }
        } else {
            message += "nothing";
        }

        listViewItems.clear();
        if(score<20 && score>10)
            score+=40;
        else if(score<30 && score>20)
            score+=30;
        else if(score<40 && score>30)
            score+=20;
        else if(score<50 && score>40)
            score+=10;
        message+="Your score is => "+ (int)score;
        sendToDB();
        return message;
    }
    public void sendToDB(){
        //Set string to DB
        Firebase ref = new Firebase("https://burning-heat-9804.firebaseio.com/");
        Firebase userRef = ref.child("Photo").child(user_name+  " : " + Datetime);
        Photos ph = new Photos(stringConverted,Double.toString(score),user_name,Datetime);
        userRef.setValue(ph);
    }
}







