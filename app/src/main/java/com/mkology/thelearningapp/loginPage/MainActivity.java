package com.mkology.thelearningapp.loginPage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import com.mkology.thelearningapp.R;
import com.mkology.thelearningapp.helper.ErrorHelper;
import com.mkology.thelearningapp.helper.ProgressbarLoading;
import com.mkology.thelearningapp.saveUserData.SaveSharedPreference;

public class MainActivity extends AppCompatActivity implements MainContract.MainView {
    private int RC_SIGN_IN = 1;
    private EditText username, password;
    private String EMPTY = "";
    private String mail, pwd;
    private TextView errorText;
    private Button loginButton;
    private TextView forgotPassword;
    private MainContract.MainPresenter presenter;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private ProgressbarLoading progressbarLoading;
    private ErrorHelper errorHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        progressbarLoading = new ProgressbarLoading(MainActivity.this);
        errorHelper = new ErrorHelper(MainActivity.this);
        new MainPresenter(this, MainActivity.this);
        mAuth = FirebaseAuth.getInstance();
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        ((TextView) signInButton.getChildAt(0)).setText(R.string.sign_up_with_google);

        loginButton = findViewById(R.id.button_login);
        errorText = findViewById(R.id.response_error_msg);
        username = findViewById(R.id.mail_login);
        password = findViewById(R.id.login_password);
        forgotPassword = findViewById(R.id.forgot_password);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getApplicationContext(), gso);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mail = username.getText().toString();
                pwd = password.getText().toString();
                if (mail.equals(EMPTY) ||
                        !android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                    errorText.setVisibility(View.VISIBLE);
                    errorText.setText("Enter valid email");
                } else if (pwd.equals(EMPTY)) {
                    errorText.setVisibility(View.VISIBLE);
                    errorText.setText("Please enter Password");
                } else {
                    presenter.checkUserAuthentication(mail, pwd, errorText);
                }
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()) {
                    case R.id.sign_in_button:
                        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                        startActivityForResult(signInIntent, RC_SIGN_IN);
                        break;
                }
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    public void setPresenter(MainContract.MainPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            SaveSharedPreference.setEmail(getApplicationContext(), account.getEmail());
            SaveSharedPreference.setName(getApplicationContext(), account.getDisplayName());
            presenter.checkUserAuthentication(account.getEmail(), EMPTY, errorText);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d("Tag", "signInResult:failed code=" + e.getStatusCode());
            errorHelper.errorMessage();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

}
