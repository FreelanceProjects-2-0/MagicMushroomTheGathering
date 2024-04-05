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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    TextView textView_registerNow;
    EditText editText_Email;
    EditText editText_Password;
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
        setContentView(R.layout.activity_login);
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

    private void submit() {
        progressBar.setVisibility(View.VISIBLE);
        String email, password, password2;
        email = String.valueOf(editText_Email.getText());
        password = String.valueOf(editText_Password.getText());

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Email is empty", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Password is empty", Toast.LENGTH_LONG).show();
        } else {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Login successful.",
                                        Toast.LENGTH_SHORT).show();
                                redirectAfterAuth();
                            } else {
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
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
        submitButton = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progress_bar);
        textView_registerNow = findViewById(R.id.registerNow);
        mAuth = FirebaseAuth.getInstance();
    }

    private void initClickListeners() {
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });
        textView_registerNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}