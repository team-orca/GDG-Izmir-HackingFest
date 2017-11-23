package com.example.orca.howbeautyiam;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    EditText pass, nick;
    Button login,register;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        nick = (EditText) findViewById(R.id.edtNick);
        pass = (EditText)findViewById(R.id.edtPass);
        register = (Button)findViewById(R.id.button);
        mSharedPreferences = getSharedPreferences("user",MODE_PRIVATE);
        editor = mSharedPreferences.edit();
        editor.putInt("entrance", 1);
        Firebase.setAndroidContext(this);
        // Get a reference to our posts

        // Attach an listener to read the data at our posts reference
        login =(Button)findViewById(R.id.email_sign_in_button);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myInt = new Intent(getBaseContext(), SignUpActivity.class);
                startActivity(myInt);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Firebase ref = new Firebase("https://burning-heat-9804.firebaseio.com/users/");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        System.out.println("There are " + snapshot.getChildrenCount());
                        for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                            FetchUser post = postSnapshot.getValue(FetchUser.class);
                            if(post.getNickName().equals(nick.getText().toString()) && post.getPassword().equals(pass.getText().toString())) {
                                editor.putString("username", post.getNickName());
                                editor.putString("email", post.getMailAddres());
                                editor.putString("pass",post.getPassword());
                                editor.apply();
                                Intent myInt = new Intent(getBaseContext(), MainActivity.class);
                                Toast.makeText(getBaseContext(), "Photos are loading.. Please wait..", Toast.LENGTH_LONG).show();
                                startActivity(myInt);
                                finish();
                                break;
                            }
                        }

                    }
                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        System.out.println("The read failed: " + firebaseError.getMessage());
                    }
                });
            }
        });
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


        /*@Override
        public String toString() {
            return nickName + point + password + mailAddres;
        }*/
    }
}





