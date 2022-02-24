package com.example.lollipop.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.lollipop.R;
import com.example.lollipop.Utils.Constants;
import com.example.lollipop.Utils.InternetConnection;
import com.example.lollipop.home.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener
{
    private TextView register, resetPassword;
    private Button login;
    private ProgressBar progressBar;
    private EditText mail, password;
    private FirebaseAuth mAuth;
    private ImageView show_hide_password;
    private CheckBox remember;
    private SharedPreferences encrypt;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initialize EditText
        mail = findViewById(R.id.email);
        password = findViewById(R.id.password);

        if(getIntent().hasExtra(Constants.MAIL_FROM_REGISTRATION) && getIntent().hasExtra(Constants.PASSWORD_FROM_REGISTRATION))
        {
            deleteSavedCredentials();
            mail.setText(getIntent().getStringExtra(Constants.MAIL_FROM_REGISTRATION));
            password.setText(getIntent().getStringExtra(Constants.PASSWORD_FROM_REGISTRATION));
        }

        //Initialize Shared
        encrypt = null;

        //Initialize Checkbox
        remember = findViewById(R.id.remember_me);

        try
        {
            readCredentialsSaved(doesSavedUserExist());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.holo_blue_light)));

        //Initialize register
        register = findViewById(R.id.register_text_view);
        register.setOnClickListener(this);

        //Initialize resetPassword
        resetPassword = findViewById(R.id.reset_password);
        resetPassword.setOnClickListener(this);

        //Initialized button login
        login = findViewById(R.id.button_login);
        login.setOnClickListener(this);

        //Initialize ProgressBar
        progressBar = findViewById(R.id.progressBar_load);

        //Initialize ProgressBar
        mAuth = FirebaseAuth.getInstance();

        //Initialize show_hide
        show_hide_password = findViewById(R.id.show_hide_password_login);
        show_hide_password.setOnClickListener(this);


    }

    @Override
    public void onClick(View v)
    {
       int id = v.getId();

       if(id == R.id.reset_password)
       {
           if(InternetConnection.isConnected(this))
           {
               Intent resetPassword = new Intent(this, ResetPasswordActivity.class);
               resetPassword.putExtra(Constants.MAIL_FROM_LOGIN, mail.getText().toString());
               startActivity(resetPassword);
               finish();
           }
           else
           {
               Toast.makeText(LoginActivity.this, R.string.no_internet, Toast.LENGTH_LONG).show();
           }
       }
       else if(id == R.id.register_text_view)
       {
           if(InternetConnection.isConnected(this))
           {
               startActivity(new Intent(this, RegisterActivity.class));
               finish();
           }
           else
           {
               Toast.makeText(LoginActivity.this, R.string.no_internet, Toast.LENGTH_LONG).show();
           }
       }
       else if(id == R.id.show_hide_password_login)
       {
           if(password.getTransformationMethod().equals(PasswordTransformationMethod.getInstance()))
           {
               //Show Password
               show_hide_password.setImageResource(R.drawable.im_hide_pass);
               password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
           }
           else
           {
               //Hide Password
               show_hide_password.setImageResource(R.drawable.im_show_pass);
               password.setTransformationMethod(PasswordTransformationMethod.getInstance());
           }
       }
       else
       {
         if(InternetConnection.isConnected(this))
         {
             userLogin();
         }
         else
         {
             Toast.makeText(LoginActivity.this, R.string.no_internet, Toast.LENGTH_LONG).show();
         }
       }
    }

    private void userLogin()
    {
        String mailR = mail.getText().toString().trim();
        String passwordR = password.getText().toString().trim();

        if(!Patterns.EMAIL_ADDRESS.matcher(mailR).matches())
        {
            mail.setError(getString(R.string.mail_error));
            mail.requestFocus();
            return;
        }

        if(passwordR.isEmpty())
        {
            password.setError(getString(R.string.void_password), null);
            password.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(mailR, passwordR).addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful())
                {
                    if(remember.isChecked())
                    {
                        try
                        {
                            saveCredentials(mailR, passwordR);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        deleteSavedCredentials();
                    }
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, R.string.login_complete, Toast.LENGTH_LONG).show();
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                }
                else
                {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, R.string.error_login, Toast.LENGTH_LONG).show();
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

    private void saveCredentials(String mailR, String passwordR) throws GeneralSecurityException, IOException
    {
        deleteSavedCredentials();
        encrypt = encryptShared();
        SharedPreferences.Editor userEditor = encrypt.edit();
        userEditor.putString(Constants.MAIL_SAVED_LOGIN, mailR);
        userEditor.putString(Constants.PASSWORD_SAVED_LOGIN, passwordR);
        userEditor.apply();
    }

    private void readCredentialsSaved(boolean exist) throws GeneralSecurityException, IOException
    {
        if(exist)
        {
            encrypt = encryptShared();
            mail.setText(encrypt.getString(Constants.MAIL_SAVED_LOGIN, null));
            password.setText(encrypt.getString(Constants.PASSWORD_SAVED_LOGIN, null));
            remember.setChecked(true);
        }
    }

    private void deleteSavedCredentials()
    {
        if(doesSavedUserExist())
        {
            File f = new File(
                    "/data/data/com.example.lollipop/shared_prefs/" + Constants.SHARED_PREF_NAME+".xml");
            f.delete();
        }
    }

    private boolean doesSavedUserExist()
    {
        File f = new File(
                "/data/data/com.example.lollipop/shared_prefs/" + Constants.SHARED_PREF_NAME +".xml");
        if (f.exists())
        {
            return true;
        }
        return false;
    }

    SharedPreferences encryptShared() throws GeneralSecurityException, IOException
    {
            MasterKey masterKey = new MasterKey.Builder(this, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            SharedPreferences toEncrypt = EncryptedSharedPreferences.create(
                    LoginActivity.this,
                    Constants.SHARED_PREF_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);

            return toEncrypt;
    }
}