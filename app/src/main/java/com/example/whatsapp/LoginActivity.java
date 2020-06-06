package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button LoginButton ,PhoneLoginButton;
    private EditText UserEmail,UserPassword;
    private TextView  NeedNewAccountLink,ForgetPasswordLink;
    private ProgressBar pb ;
    private ScrollView scrollView;
    private DatabaseReference UserRef ;
    private FirebaseUser currentuser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        InitializerFields();
        currentuser= mAuth.getCurrentUser();
        if(currentuser!= null)
        {
            SendUserToMainActivity();
        }



        pb.setVisibility(View.INVISIBLE);
        NeedNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToRegisterActivity();
            }
        });

        Toast.makeText(this, "Hii this is Whatsapp", Toast.LENGTH_SHORT).show();

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllowUserToLogin();
            }
        });

        PhoneLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendUserToPhoneLoginActivity();
            }
        });





    }

    private void SendUserToPhoneLoginActivity() {
        Intent phoneIntent = new Intent(LoginActivity.this,PhoneLoginActivity.class);
       // phoneIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(phoneIntent);
        //finish();
    }

    private void AllowUserToLogin() {

        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();
        if(TextUtils.isEmpty(email) && TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please enter email or password", Toast.LENGTH_SHORT).show();

        }
        else
        {   scrollView.setVisibility(View.INVISIBLE);
            pb.setVisibility(View.VISIBLE);


            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                final String currentUserId = mAuth.getCurrentUser().getUid();
                                final String[] deviceToken = new String[1];
                                FirebaseInstanceId.getInstance().getInstanceId()
                                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                                if (!task.isSuccessful()) {
                                                    Log.w("warn", "getInstanceId failed", task.getException());
                                                    return;
                                                }

                                                deviceToken[0] = task.getResult().getToken();
                                                UserRef.child(currentUserId).child("device_token")
                                                        .setValue(deviceToken[0])
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if(task.isSuccessful())
                                                                {
                                                                    SendUserToMainActivity();
                                                                    Toast.makeText(LoginActivity.this, "Logged in Successful..."+deviceToken[0], Toast.LENGTH_SHORT).show();
                                                                    pb.setVisibility(View.INVISIBLE);
                                                                    scrollView.setVisibility(View.VISIBLE);

                                                                }

                                                            }
                                                        });

                                            }
                                        });


                            }
                            else
                            {
                                String message = task.getException().toString() ;
                                Toast.makeText(LoginActivity.this, "Error"+message, Toast.LENGTH_SHORT).show();
                                pb.setVisibility(View.INVISIBLE);
                                scrollView.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        }


    }

    private void InitializerFields() {

        LoginButton = findViewById(R.id.login_button);
        PhoneLoginButton = findViewById(R.id.phone_login_button);
        UserEmail = findViewById(R.id.login_email);
        UserPassword = findViewById(R.id.login_password);
        ForgetPasswordLink = findViewById(R.id.forget_password_link);
        NeedNewAccountLink = findViewById(R.id.create_new_account_link);
        scrollView = findViewById(R.id.login_scroll);
        pb = findViewById(R.id.progressBar);
    }


    private void SendUserToMainActivity() {

        Intent main_Intent = new Intent(this,MainActivity.class);

        startActivity(main_Intent);

    }
    private void SendUserToRegisterActivity() {

        Intent registerIntent = new Intent(this,RegisterActivity.class);
        startActivity(registerIntent);

    }
}
