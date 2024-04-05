package dk.tec.magicmushroomthegathering;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoggedInUser extends AppCompatActivity {
    FirebaseAuth mauth;
    Button btn_logout;
    Button btn_openMap;
    TextView tv_userDetails;

    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_logged_in_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getLayoutFields();
        initClickListeners();
    }

    private void logout(){
        FirebaseAuth.getInstance().signOut();
        redirectToLogin();
    }

    private void getLayoutFields() {
        btn_logout = findViewById(R.id.logout);
        btn_openMap = findViewById(R.id.btn_openMap);
        tv_userDetails = findViewById(R.id.user_details);
        mauth = FirebaseAuth.getInstance();
        user = mauth.getCurrentUser();

        if (user == null){
            redirectToLogin();
        }
        else {
            tv_userDetails.setText(user.getEmail());
        }
    }

    private void redirectToLogin(){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void redirectToMap(){
        Intent intent = new Intent(getApplicationContext(), OsmActivity.class);
        startActivity(intent);
        finish();
    }

    private void initClickListeners() {
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
        btn_openMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectToMap();
            }
        });
    }
}