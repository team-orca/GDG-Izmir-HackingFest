package com.example.orca.howbeautyiam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Firebase.setAndroidContext(this);
        mSharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        user_name = mSharedPreferences.getString("username", "Empty");
        user_mail = mSharedPreferences.getString("email", "Empty");
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
                itemname = new String[username.size()];
                photos = new Bitmap[photoString.size()];
                points= new String[point.size()];

                for (int i = 0; i < photoString.size();i++ ){
                    photos[i]=StringToBitMap(photoString.get(i));
                    itemname[i]=username.get(i);
                    points[i]=point.get(i);

                    if(itemname[i].equals(user_name)){
                        img_user=Bitmap.createScaledBitmap(photos[i], 48, 48, true);
                        Toast.makeText(MainActivity.this, "Loading is done..", Toast.LENGTH_LONG).show();
                    }
                }

                adapter = new CustomListAdapter(MainActivity.this, itemname, photos, points);
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
                String Slecteditem= itemname[+position];
                Toast.makeText(getApplicationContext(), Slecteditem, Toast.LENGTH_SHORT).show();

            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        TextView username_view = (TextView)headerView.findViewById(R.id.username_nav);
        TextView usermail_view = (TextView)headerView.findViewById(R.id.email_nav);
        ImageView image_user= (ImageView)headerView.findViewById(R.id.image_user);
        usermail_view.setText(user_mail);
        username_view.setText(user_name);
        image_user.setImageBitmap(img_user);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_capture) {
            Intent intent = new Intent(this, CameraActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_shop) {
            Intent intent = new Intent(this, ShopActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_ranking) {
            Intent intent = new Intent(this, MyRankActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            Intent intent = new Intent(this, ScrollingActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
