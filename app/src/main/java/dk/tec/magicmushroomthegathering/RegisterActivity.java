package dk.tec.magicmushroomthegathering;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    TextView textView_loginNow;
    EditText editText_Email;
    EditText editText_Password;
    EditText editText_VerifyPassword;
    Button submitButton;
    ProgressBar progressBar;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = null;
        // Check if user is signed in (non-null) and update UI accordingly.
        if (mAuth != null)
        {
            currentUser = mAuth.getCurrentUser();
        }
        if (currentUser != null) {
            redirectAfterAuth();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getLayoutFields();
        initClickListeners();
    }


    private void redirectAfterAuth() {
        Intent intent = new Intent(getApplicationContext(), LoggedInUser.class);
        startActivity(intent);
        finish();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void submit() {
        progressBar.setVisibility(View.VISIBLE);
        String email, password, password2;
        email = String.valueOf(editText_Email.getText());
        password = String.valueOf(editText_Password.getText());
        password2 = String.valueOf(editText_VerifyPassword.getText());

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Email is empty", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Password is empty", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(password2) || !password.equals(password2)) {
            Toast.makeText(this, "Confirm password has to be the same as set password", Toast.LENGTH_LONG).show();
        } else {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "New account created",
                                        Toast.LENGTH_SHORT).show();
                                redirectToLogin();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        progressBar.setVisibility(View.GONE);
    }

    private void getLayoutFields() {
        editText_Email = findViewById(R.id.textInput_Email);
        editText_Password = findViewById(R.id.textInput_Password);
        editText_VerifyPassword = findViewById(R.id.textInput_Password2);
        submitButton = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progress_bar);
        textView_loginNow = findViewById(R.id.loginNow);
        mAuth = FirebaseAuth.getInstance();
    }

    private void initClickListeners() {
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });
        textView_loginNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}