package com.example.lollipop.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.lollipop.R;
import com.example.lollipop.Utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText mail;
    private ProgressBar loading;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.holo_blue_light)));

        //Initialize edittext
        mail = findViewById(R.id.email_for_reset);

        //Fare in Login passaggio intent
        if(getIntent().hasExtra(Constants.MAIL_FROM_LOGIN))
        {
            mail.setText(getIntent().getStringExtra(Constants.MAIL_FROM_LOGIN));
        }

        //Initialize button
        Button resetPasswordButton = findViewById(R.id.button_password_reset);
        resetPasswordButton.setOnClickListener(this);

        //Initialize progressbar
        loading = findViewById(R.id.progressBar_load_password);

        //Initialize Textview
        TextView backHome = findViewById(R.id.textView_back_home);
        backHome.setOnClickListener(this);

        //Initialize firebase
        mAuth = FirebaseAuth.getInstance();
    }



    @Override
    public void onClick(View v)
    {
        int id = v.getId();

        if(id == R.id.textView_back_home)
        {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        else
        {
            resetPassword();
        }
    }

    private void resetPassword()
    {
        String mailR = mail.getText().toString().trim();

        if(mailR.isEmpty())
        {
            mail.setError(getString(R.string.mail_error));
            mail.requestFocus();
            return;
        }

        loading.setVisibility(View.VISIBLE);
        mAuth.sendPasswordResetEmail(mailR).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    Intent resetDone = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                    loading.setVisibility(View.GONE);
                    Toast.makeText(ResetPasswordActivity.this, R.string.email_reset_sent, Toast.LENGTH_LONG).show();
                    startActivity(resetDone);
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Log.d("Error: ", e.toString());
            }
        });
    }
}