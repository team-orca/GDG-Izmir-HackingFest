package com.example.orca.howbeautyiam;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

public class SignUpActivity extends AppCompatActivity {
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mNicknameView;
    private Button btnSign;

    private String password;
    private String mUserName;
    private String emailaddress;

    private ArrayList<String> username = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Firebase.setAndroidContext(this);

        mEmailView = (EditText) findViewById(R.id.edtMail);
        mPasswordView = (EditText)findViewById(R.id.edtPass);
        mNicknameView = (EditText)findViewById(R.id.edtnick);
        btnSign = (Button)findViewById(R.id.email_sign_in_button);

        btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserName = mNicknameView.getText().toString();
                password = mPasswordView.getText().toString();
                emailaddress = mEmailView.getText().toString();
                final Firebase ref = new Firebase("https://burning-heat-9804.firebaseio.com/users/");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                            FetchUser fetch = postSnapshot.getValue(FetchUser.class);
                            username.add(fetch.getNickName().toString());
                        }
                    }
                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        Toast.makeText(getBaseContext(), "Canceled", Toast.LENGTH_LONG).show();
                    }
                });

                boolean isFound = false;

                for (int i = 0; i < username.size(); i++){
                    if(username.get(i).equals(mUserName))
                        isFound = true;
                }

                if(!isFound) {
                    Firebase mRef = new Firebase("https://burning-heat-9804.firebaseio.com/");
                    User user = new User(mUserName, emailaddress, password);
                    Firebase userRef = mRef.child("users").child(mUserName + "Info");
                    userRef.setValue(user);
                } else {
                    Toast.makeText(getBaseContext(), "The nickname already exists", Toast.LENGTH_LONG).show();
                }
            finish();
            }
        });



    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class FetchUser {
        private String nickName;
        private String mailAddres;
        private String password;
        private String point;

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

    }
}
