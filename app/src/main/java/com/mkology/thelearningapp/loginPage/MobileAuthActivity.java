package com.mkology.thelearningapp.loginPage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.mkology.thelearningapp.helper.ApplicationConstants;
import com.mkology.thelearningapp.helper.ErrorHelper;
import com.mkology.thelearningapp.helper.ProgressbarLoading;
import com.mkology.thelearningapp.HomeActivity;
import com.mkology.thelearningapp.R;
import com.mkology.thelearningapp.apiCalls.APIClient;
import com.mkology.thelearningapp.apiCalls.JsonPlaceHolderApi;
import com.mkology.thelearningapp.apiCalls.UserProfile;
import com.mkology.thelearningapp.saveUserData.SaveSharedPreference;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MobileAuthActivity extends AppCompatActivity {

    private String name, email;
    private EditText mobile;
    private Button sendOtp;
    private LinearLayout setUserDetail;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String mVerificationId;
   // private EditText otp;
    private FirebaseAuth mAuth;
    private TextView responseErrorMsg, signUpErrorMsg;
    private LinearLayout mobileVerification, otpVerificationLayout;
    private Button signUp, verify_otp;
    private EditText password, confirmPassword, locality;
    private String phone, phoneWithCountryCode, countryCode;
    private CountryCodePicker countryCodePicker;
    EditText otp_textbox_one, otp_textbox_two, otp_textbox_three,
            otp_textbox_four, otp_textbox_five, otp_textbox_six;
    private ProgressbarLoading progressbarLoading;
    private ErrorHelper errorHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mobile_authentication);

        email = getIntent().getExtras().getString("email");
        name = getIntent().getExtras().getString("name");

        getSupportActionBar().hide();
        progressbarLoading = new ProgressbarLoading(MobileAuthActivity.this);
        errorHelper = new ErrorHelper(MobileAuthActivity.this);

        mobile = findViewById(R.id.mobileNumber);
        sendOtp = findViewById(R.id.sendOtp);
        mAuth = FirebaseAuth.getInstance();
        responseErrorMsg = findViewById(R.id.response_error_msg);
        mobileVerification = findViewById(R.id.mobile_Verification);
        setUserDetail = findViewById(R.id.setDetails);
        verify_otp = findViewById(R.id.verify_otp);
        countryCodePicker = findViewById(R.id.cpp);
        otpVerificationLayout = findViewById(R.id.otp_verification_layout);
        signUpErrorMsg = findViewById(R.id.error_msg);

        otp_textbox_one = findViewById(R.id.otp_edit_box1);
        otp_textbox_two = findViewById(R.id.otp_edit_box2);
        otp_textbox_three = findViewById(R.id.otp_edit_box3);
        otp_textbox_four = findViewById(R.id.otp_edit_box4);
        otp_textbox_five = findViewById(R.id.otp_edit_box5);
        otp_textbox_six = findViewById(R.id.otp_edit_box6);

        EditText[] edit = {otp_textbox_one, otp_textbox_two, otp_textbox_three,
                otp_textbox_four, otp_textbox_five, otp_textbox_six};

        otp_textbox_one.addTextChangedListener(new GenericTextWatcher(otp_textbox_one, edit));
        otp_textbox_two.addTextChangedListener(new GenericTextWatcher(otp_textbox_two, edit));
        otp_textbox_three.addTextChangedListener(new GenericTextWatcher(otp_textbox_three, edit));
        otp_textbox_four.addTextChangedListener(new GenericTextWatcher(otp_textbox_four, edit));
        otp_textbox_five.addTextChangedListener(new GenericTextWatcher(otp_textbox_five, edit));
        otp_textbox_six.addTextChangedListener(new GenericTextWatcher(otp_textbox_six, edit));



        sendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                responseErrorMsg.setVisibility(View.GONE);
                countryCode = countryCodePicker.getSelectedCountryCodeWithPlus();
                phone = mobile.getText().toString();
                phoneWithCountryCode = countryCode + mobile.getText().toString();
                if (!phone.isEmpty()) {
                    checkMobileAndGetData(phone, phoneWithCountryCode);
                }
            }

        });

        verify_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otpValue = otp_textbox_one.getText().toString()
                        + otp_textbox_two.getText().toString()
                        + otp_textbox_three.getText().toString()
                        + otp_textbox_four.getText().toString()
                        + otp_textbox_five.getText().toString()
                        + otp_textbox_six.getText().toString();

                verifyVerificationCode(otpValue);
            }
        });

    }

    private void sendVerificationCode(String no) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                no,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                otp_textbox_one.setText(Integer.toString(code.charAt(0)-48));
                otp_textbox_two.setText(Integer.toString(code.charAt(1)-48));
                otp_textbox_three.setText(Integer.toString(code.charAt(2)-48));
                otp_textbox_four.setText(Integer.toString(code.charAt(3)-48));
                otp_textbox_five.setText(Integer.toString(code.charAt(4)-48));
                otp_textbox_six.setText(Integer.toString(code.charAt(5)-48));

                //verifying the code
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            progressbarLoading.hideProgressBar();
            Toast.makeText(MobileAuthActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            sendOtp.setText("OTP sent");
            sendOtp.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_green));
            verify_otp.setEnabled(true);
            otpVerificationLayout.setVisibility(View.VISIBLE);
            otp_textbox_one.requestFocus();
            //storing the verification id that is sent to the user
            mVerificationId = s;
        }
    };

    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(MobileAuthActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressbarLoading.hideProgressBar();
                            verify_otp.setText("Verified");
                            verify_otp.setBackground(getResources().getDrawable(R.drawable.bg_rectangle_green));
                            //verification successful we will start the profile activity
                            onMobileAuthSuccess();
                        } else {

                            //verification unsuccessful.. display an error message
                            responseErrorMsg.setVisibility(View.VISIBLE);
                            responseErrorMsg.setText(getString(R.string.phoneAuthError));

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                responseErrorMsg.setText(getString(R.string.phoneAuthErrorFirebase));
                            }
                        }
                    }
                });
    }

    private void onMobileAuthSuccess() {
        mobileVerification.setVisibility(View.GONE);
        setUserDetail.setVisibility(View.VISIBLE);
        signUp = findViewById(R.id.signup);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirm_password);
        locality = findViewById(R.id.locality);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressbarLoading.showProgressBar();
                String pass, confirmPass, place;
                pass = password.getText().toString();
                confirmPass = confirmPassword.getText().toString();
                place = locality.getText().toString();

                SaveSharedPreference.setEmail(getApplicationContext(), email);
                SaveSharedPreference.setName(getApplicationContext(), name);
                SaveSharedPreference.setPlace(getApplicationContext(), place);
                SaveSharedPreference.setMobile(getApplicationContext(), phone);

                if (pass.equals(ApplicationConstants.EMPTY)
                        || confirmPass.equals(ApplicationConstants.EMPTY)
                        || place.equals(ApplicationConstants.EMPTY)) {
                    signUpErrorMsg.setVisibility(View.VISIBLE);
                    signUpErrorMsg.setText(getString(R.string.empty_field_error));
                    progressbarLoading.hideProgressBar();
                } else if (!pass.equals(confirmPass)) {
                    signUpErrorMsg.setVisibility(View.VISIBLE);
                    signUpErrorMsg.setText(getString(R.string.password_error));
                    progressbarLoading.hideProgressBar();
                } else if (pass.length() < 6) {
                    signUpErrorMsg.setVisibility(View.VISIBLE);
                    signUpErrorMsg.setText(getString(R.string.password_length_error));
                    progressbarLoading.hideProgressBar();
                } else {
                    createUserProfile(pass);
                }
            }
        });
    }

    private void createUserProfile(final String password) {

        JsonPlaceHolderApi jsonPlaceHolderApi = APIClient.getClient().create(JsonPlaceHolderApi.class);

        Call<String> call = jsonPlaceHolderApi.insertUser(
                SaveSharedPreference.getEmail(getApplicationContext()),
                password,
                SaveSharedPreference.getName(getApplicationContext()),
                SaveSharedPreference.getMobile(getApplicationContext()),
                SaveSharedPreference.getPlace(getApplicationContext()));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                progressbarLoading.hideProgressBar();
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_LONG).show();
                    SaveSharedPreference.setLoggedIn(getApplicationContext(), true);
                    Intent intent = new Intent(MobileAuthActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Registration failed! Please try again later", Toast.LENGTH_LONG).show();
                    errorHelper.errorMessage();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressbarLoading.hideProgressBar();
                errorHelper.errorMessage();
                Toast.makeText(getApplicationContext(), "Registration failed! ", Toast.LENGTH_LONG).show();
                            signUpErrorMsg.setVisibility(View.VISIBLE);
            }
        });
    }

    private void checkMobileAndGetData (String mobile, String mobileWithCountryCode) {
        JsonPlaceHolderApi jsonPlaceHolderApi = APIClient.getClient().create(JsonPlaceHolderApi.class);
        Map<String, String> params = new HashMap<String, String>();
        params.put("mobile", mobile);
        progressbarLoading.showProgressBar();
        jsonPlaceHolderApi.getDataForgotPass(params).enqueue(new Callback<List<UserProfile>>() {
            @Override
            public void onResponse(Call<List<UserProfile>> call, Response<List<UserProfile>> response) {
                List<UserProfile> userProfiles = response.body();
                UserProfile userProfile = userProfiles.get(0);
                progressbarLoading.hideProgressBar();
                if (userProfile.getError() == null) {
                    responseErrorMsg.setVisibility(View.VISIBLE);
                    responseErrorMsg.setText(getString(R.string.mobile_already_registered));
                } else {
                    SaveSharedPreference.setUserData(getApplicationContext(), userProfile);
                    sendVerificationCode(mobileWithCountryCode);
                }
            }

            @Override
            public void onFailure(Call<List<UserProfile>> call, Throwable t) {
                progressbarLoading.hideProgressBar();
                errorHelper.errorMessage();
            }
        });
    }
}

