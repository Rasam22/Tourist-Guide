package com.example.harpreet.visitorguide.Account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.harpreet.visitorguide.MLCamera;
import com.example.harpreet.visitorguide.MainActivity;
import com.example.harpreet.visitorguide.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button login;
    private Button signup;
    FirebaseAuth mauth;
    private ProgressBar dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dialog = findViewById(R.id.progressBar);
        dialog.setVisibility(View.INVISIBLE);

        mauth=FirebaseAuth.getInstance();
        email=findViewById(R.id.signupemail);
        password=findViewById(R.id.signuppassword);
        login=findViewById(R.id.createbtn);
        signup=findViewById(R.id.signupbutton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginFuction();
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login.this,register.class));
            }
        });
    }

    private void loginFuction() {

        String Email=email.getText().toString()+"@goa.com";
        String Password=password.getText().toString();

        if(!TextUtils.isEmpty(Password))
        {

            dialog.setVisibility(View.VISIBLE);
            mauth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        startActivity(new Intent(login.this,MainActivity.class));
                        finish();
                        dialog.setVisibility(View.INVISIBLE);
                    }
                    else
                    {
                        String error=task.getException().getMessage();
                        Toast.makeText(login.this,error,Toast.LENGTH_LONG).show();
                        dialog.setVisibility(View.INVISIBLE);
                    }

                }
            });


        }
        else
        {
            Toast.makeText(login.this,"Please Enter Both UserName & Password",Toast.LENGTH_SHORT).show();
        }


    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null)
        {
            //go to sign in page
            startActivity(new Intent(login.this,MainActivity.class));
            finish();
        }
    }
}
