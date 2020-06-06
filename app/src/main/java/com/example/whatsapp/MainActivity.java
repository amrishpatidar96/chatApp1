package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    private Toolbar mToolbar ;
    private ViewPager myViewPager ;
    private TabLayout myTabLayout;
    private TabsAccessorAdapter myTabsAccessorAdapter;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth ;
    private DatabaseReference RootRef ;
    private ScrollView scrollView ;
    private static int i = 0;
    UserOnlineStatus userOnlineStatus = new UserOnlineStatus();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        RootRef = FirebaseDatabase.getInstance().getReference();

        Log.d("MainOnCreate","MainOnCreate");

          userOnlineStatus.updateUserStatus("online");
            Toast.makeText(this, "going towards user verification", Toast.LENGTH_SHORT).show();
            VerifyUserExistence();


        mToolbar = findViewById(R.id.main_page_toolbar);
        myTabLayout = findViewById(R.id.main_tabs);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("ChatApp");
        myViewPager =  findViewById(R.id.main_tabs_pager);
        myTabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsAccessorAdapter);
        myTabLayout.setupWithViewPager(myViewPager);





    }


    @Override
    protected void onStop() {
        super.onStop();


        Log.d("MainOnstop","MainOnstop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


        Log.d("MainOnDestroy","MainOnDestroy");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.options_menu,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);


        switch(item.getItemId())
        {
            case R.id.main_logout_option:
                userOnlineStatus.updateUserStatus("offline");
                mAuth.signOut();
                SendUserToLoginActivity();
                break;
            case R.id.main_settings_option:
                SendUserToSettingsActivity();
                break;
            case R.id.main_create_group_option:
                RequestNewGroup();
                break;
            case R.id.main_find_friends_option:
                SendUserToFindFriendsActivity();
                break;
        }

        return true;

    }

    private void SendUserToFindFriendsActivity() {

        Intent findFriendsIntent = new Intent(this,FindFriendsActivity.class);
        startActivity(findFriendsIntent);

    }

    private void RequestNewGroup()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog);
        builder.setTitle("Enter Group Name :");

        final EditText groupNameField = new EditText(MainActivity.this);
        groupNameField.setHint("e.g. Coding Cafe");
        builder.setView(groupNameField);
        builder.setPositiveButton("Create" , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupName = groupNameField.getText().toString();

                if(TextUtils.isEmpty(groupName))
                {
                    Toast.makeText(MainActivity.this, "Please write Group Name...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    CreateNewGroup(groupName);
                }

            }
        });

        builder.setNegativeButton("Cancel" , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        builder.show();
    }

    private void CreateNewGroup(final String groupName) {

        RootRef.child("Groups").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(MainActivity.this, groupName+" group is created successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }


    private void VerifyUserExistence() {

        String currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
       // Toast.makeText(this, currentUserID, Toast.LENGTH_SHORT).show();
        RootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if( dataSnapshot.exists()  &&  !dataSnapshot.child("name").getValue().toString().equals("username") )
                {
                    Toast.makeText(MainActivity.this, "welcome", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "please fill the required details first", Toast.LENGTH_SHORT).show();
                    SendUserToSettingsActivity();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(MainActivity.this, "Error"+databaseError, Toast.LENGTH_SHORT).show();
                SendUserToLoginActivity();
            }
        });
    }


    private void SendUserToSettingsActivity() {

        Intent settingIntent= new Intent(this,SettingsActivity.class);
        Log.d("maintosetting","mainActivity to settings");
        settingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingIntent);
        finish();
    }

    private void SendUserToLoginActivity() {

        Intent loginIntent= new Intent(this,LoginActivity.class);
        Log.d("Logintosetting","mainActivity to Login");
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }


}


