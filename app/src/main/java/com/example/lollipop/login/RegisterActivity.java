package com.example.lollipop.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.lollipop.R;
import com.example.lollipop.users.User;
import com.example.lollipop.Utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener
{
    private TextView lollipop;
    private FirebaseAuth mAuth;
    private EditText name, surname, age, mail, password;
    private ProgressBar progressBar;
    private Button registerUser;
    private ImageView show_hide_password;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.holo_blue_light)));

        mAuth = FirebaseAuth.getInstance();

        //Initialize each variable
        lollipop = findViewById(R.id.lollipop_back);
        lollipop.setOnClickListener(this);

        registerUser = findViewById(R.id.button_register);
        registerUser.setOnClickListener(this);

        name = findViewById(R.id.userName);
        surname = findViewById(R.id.userSurname);
        age = findViewById(R.id.age);
        mail = findViewById(R.id.email_register);
        password = findViewById(R.id.password_register);

        progressBar = findViewById(R.id.progressBar_register);

        show_hide_password = findViewById(R.id.show_hide_password);
        show_hide_password.setOnClickListener(this);

    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();

        if(id == R.id.lollipop_back)
        {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        else if(id == R.id.show_hide_password)
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
           registerUser();
        }
    }

  private void registerUser()
    {
        String nameR = name.getText().toString().trim();
        String surnameR = surname.getText().toString().trim();
        String ageR = age.getText().toString().trim();
        String mailR = mail.getText().toString().trim();
        String passwordR = password.getText().toString().trim();

        if(nameR.isEmpty())
        {
            name.setError(getString(R.string.name_error));
            name.requestFocus();
            return;
        }

        if(surnameR.isEmpty())
        {
            surname.setError(getString(R.string.surname_error));
            surname.requestFocus();
            return;
        }

        if(ageR.isEmpty())
        {
            age.setError(getString(R.string.age_required));
            age.requestFocus();
            return;
        }

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

        if(!passwordR.matches("^(?=.*[0-9])"
            + "(?=.*[a-z])(?=.*[A-Z])"
            + "(?=.*[@#$%^&+=])"
            + "(?=\\S+$).{8,20}$"))
        {
            password.setError(getString(R.string.error_password), null);
            password.requestFocus();
            return;
        }
        ArrayList<String> help = new ArrayList<>();
        help.add("false");

        User user = new User("", nameR, surnameR, ageR, mailR, passwordR, help, help, help);

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(mailR, passwordR).addOnCompleteListener(this, task ->
        {
            if(task.isSuccessful())
            {
                //If server kill use in instance ("https://lolli-94219-default-rtdb.europe-west1.firebasedatabase.app")
                FirebaseDatabase.getInstance().getReference("Users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            Intent goToLogin = new Intent(RegisterActivity.this, LoginActivity.class);
                            goToLogin.putExtra(Constants.MAIL_FROM_REGISTRATION, mailR);
                            goToLogin.putExtra(Constants.PASSWORD_FROM_REGISTRATION, passwordR);

                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(RegisterActivity.this, R.string.registration_complete, Toast.LENGTH_LONG).show();
                            startActivity(goToLogin);
                            finish();
                        }
                        else
                        {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(RegisterActivity.this, R.string.registration_error, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
            else
            {
                Toast.makeText(RegisterActivity.this, R.string.registration_error, Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        }).addOnFailureListener(e -> Log.d("Error: ", e.toString()));
    }
}