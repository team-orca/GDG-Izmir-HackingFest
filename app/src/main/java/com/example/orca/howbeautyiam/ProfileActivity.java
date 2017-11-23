package com.example.orca.howbeautyiam;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private SharedPreferences mSharedPreferences;
    private Handler mHandler = new Handler();
    private String user_name="";
    private String user_mail="";
    private Bitmap img_user;
    CustomListAdapter adapter;
    public ArrayList<String> photoString = new ArrayList<>();
    public ArrayList<String> username = new ArrayList<>();
    public ArrayList<String> point = new ArrayList<>();
    public ArrayList<String> date = new ArrayList<>();

    ListView list;
    String[] itemname;
    Bitmap [] photos;
    String[] points;
    double totalpoint=0.0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mSharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        user_name = mSharedPreferences.getString("username", "Empty");
        user_mail = mSharedPreferences.getString("email", "Empty");
        final ImageView imageView = (ImageView) findViewById(R.id.profile);
        TextView textView1 = (TextView) findViewById(R.id.username);
        TextView textView2 = (TextView) findViewById(R.id.email);
        final TextView textView3 = (TextView) findViewById(R.id.points);
        imageView.setImageResource(R.drawable.pic8);
        textView1.setText(user_name);
        textView2.setText(user_mail);

        list=(ListView)findViewById(R.id.list);

        final Firebase ref = new Firebase("https://burning-heat-9804.firebaseio.com/Photo/");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                point.clear();
                photoString.clear();
                username.clear();
                date.clear();
                System.out.println("There are " + snapshot.getChildrenCount());
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    FetchPhoto post = postSnapshot.getValue(FetchPhoto.class);
                    photoString.add(post.getPhotoUrl());
                    point.add(post.getPhotoPoint());
                    username.add(post.getUsername());
                    date.add(post.getDate());
                }
                int counter = 0;
                for (int i = 0; i < username.size(); i++) {
                    if (username.get(i).equals(user_name)) {
                        counter++;
                    }
                }
                itemname = new String[counter];
                photos = new Bitmap[counter];
                points = new String[counter];
                int k = 0;
                for (int i = 0; i < username.size(); i++) {
                    if (username.get(i).equals(user_name)) {
                        photos[k] = StringToBitMap(photoString.get(i));
                        itemname[k] = username.get(i);
                        points[k] = point.get(i);
                        totalpoint += Double.parseDouble(point.get(i));
                        imageView.setImageBitmap(photos[k]);
                        k++;
                        if (k >= counter)
                            break;
                    }
                }
                textView3.setText("" + totalpoint);
                adapter = new CustomListAdapter(ProfileActivity.this, itemname, photos, points);
                list.setAdapter(adapter);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                String Slecteditem = itemname[+position];
                Toast.makeText(getApplicationContext(), Slecteditem, Toast.LENGTH_SHORT).show();

            }
        });
        Firebase mRef = new Firebase("https://burning-heat-9804.firebaseio.com/");
        User user = new User(user_name, user_mail, Double.toString(totalpoint), "a");
        Firebase userRef = mRef.child("users").child(user_name + "Info");
        userRef.setValue(user);


    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class FetchUser {
        private String nickName;
        private String mailAddres;
        private String password;
        private String point;
        private String shopPoint;

        public String getNickName() {
            return nickName;
        }

        public String getMailAddres() {
            return mailAddres;
        }

        public String getPassword() {
            return  password;
        }

        public String getPoint() {
            return point;
        }
        public String getShopPoint() {
            return shopPoint;
        }
    }
    private static class FetchPhoto {
        private String photoPoint;
        private String photoUrl;
        private String username;
        private String date;
        public String getPhotoUrl()
        {
            return photoUrl;
        }
        public String getPhotoPoint(){return photoPoint;}

        public String getUsername() {
            return username;
        }

        public String getDate() {
            return date;
        }

    } public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }
}
