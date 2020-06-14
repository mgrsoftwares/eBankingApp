package com.mgrsoftwaredevelopers.ebankingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SnapshotMetadata;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String user_id, accountType,
            first_name, sur_name, middle_name,
            email, confirm_email, date_ofBirth,
            phone_no, password, confirm_password,
            gender, profile_image_url;

    int account_number;

    Spinner spinner;
    EditText firstName, surName, middleName, dateOfBirth, emailAddress, confirmEmailAddress, passwordField, confirmPasswordField, phoneNumber;
    RadioButton maleSelected, femaleSelected;
    CircleImageView pickProfilePicture;

    LinearLayout regFormView, pictureSelectedView;
    ProgressBar imageUploadProgress;

    Uri uri;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference accountsRef = db.collection("accounts");
    DocumentReference accountInfoRef,
    accountNumberRef = db.document("account_number/account_number");
    StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("account_profile_pictures");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeViews();

        setUpSpinner();

    }

    private void initializeViews() {

        spinner = findViewById(R.id.account_type);
        firstName = findViewById(R.id.first_name_txt);
        surName = findViewById(R.id.sur_name_txt);
        middleName = findViewById(R.id.middle_name_txt);
        dateOfBirth = findViewById(R.id.date_of_birth_txt);
        phoneNumber = findViewById(R.id.reg_phone);
        emailAddress = findViewById(R.id.reg_email_txt);
        confirmEmailAddress = findViewById(R.id.confirm_reg_email_txt);
        passwordField = findViewById(R.id.reg_password_txt);
        confirmPasswordField = findViewById(R.id.confirm_reg_password_txt);
        regFormView = findViewById(R.id.account_form_creation);
        pictureSelectedView =findViewById(R.id.profile_picture_view);
        maleSelected = findViewById(R.id.male_gender);
        femaleSelected = findViewById(R.id.female_gender);
        pickProfilePicture = findViewById(R.id.profile_pic);
        imageUploadProgress =findViewById(R.id.progressBar);

        radioButtonsCheck();

    }

    private void radioButtonsCheck() {

        gender = "";

        maleSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                gender = "Male";
            }
        });

        femaleSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                gender = "Female";

            }
        });

    }

    //    todo spinner setUp n clicks begins
    private void setUpSpinner() {

        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(this, R.array.account_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        accountType = parent.getItemAtPosition(position).toString().trim();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
//    todo spinner setUp n clicks ends

    public void createUser(View view) {

        first_name = firstName.getText().toString().trim();
        sur_name = surName.getText().toString().trim();
        middle_name = middleName.getText().toString().trim();
        date_ofBirth = dateOfBirth.getText().toString().trim();
        email = emailAddress.getText().toString().trim();
        confirm_email = confirmEmailAddress.getText().toString().trim();
        phone_no = phoneNumber.getText().toString().trim();
        password = passwordField.getText().toString().trim();
        confirm_password = confirmPasswordField.getText().toString().trim();

        if (TextUtils.isEmpty(first_name)){

            firstName.setError("first name");
            Toast.makeText(this, "please type-in \n your first name ", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(sur_name)){

            surName.setError("sur name");
            Toast.makeText(this, "please type-in \n your sur name ", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(date_ofBirth)){

            dateOfBirth.setError("sur name");
            Toast.makeText(this, "please type-in \n your date of birth ", Toast.LENGTH_SHORT).show();

        } else if (accountType.equals("...")) {

            spinner.setBackgroundResource(R.drawable.error_background);
            Toast.makeText(this, "please select account type", Toast.LENGTH_SHORT).show();


        } else if (TextUtils.isEmpty(phone_no)){

            phoneNumber.setError("phone number");
            Toast.makeText(this, "please type-in \n your phone number ", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(email)){

            emailAddress.setError("email address");
            Toast.makeText(this, "please type-in \n your email address ", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(confirm_email)){

            confirmEmailAddress.setError("confirm email address");
            Toast.makeText(this, "please type-in \n your email address ", Toast.LENGTH_SHORT).show();

        } else if (!(email.equals(confirm_email))){

            confirmEmailAddress.setError("wrong email address");
            Toast.makeText(this, "incorrect email \n please type-in \n the same email above ", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(password)){

            passwordField.setError("password");
            Toast.makeText(this, "please type-in \n your phone number ", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(confirm_password)){

            confirmPasswordField.setError("confirm password");
            Toast.makeText(this, "please type-in \n your email address ", Toast.LENGTH_SHORT).show();

        } else if (!(password.equals(confirm_password))){

            confirmPasswordField.setError("wrong password");
            Toast.makeText(this, "incorrect email \n please type-in \n the same email above ", Toast.LENGTH_SHORT).show();

        }else if (gender.equals("")) {

            maleSelected.setError("please select gender");
            femaleSelected.setError("please select gender");
            Toast.makeText(this, "please select gender", Toast.LENGTH_SHORT).show();

        }
            else {

            final ProgressDialog loadingBar;
            loadingBar = new ProgressDialog(this);
            loadingBar.setTitle("Creating Your Account");
            loadingBar.setMessage("please wait while your Account is creating ... ");
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()){

                        signIn();

                    } else {

                        loadingBar.dismiss();
                        String error_msg = Objects.requireNonNull(task.getException()).toString();
                        Toast.makeText(RegisterActivity.this, "Error: "+ error_msg, Toast.LENGTH_LONG).show();

                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                        builder.setTitle("Error !!!");
                        builder.setMessage(error_msg);
                        builder.show();

                    }

                }

                private void signIn() {

                    loadingBar.setTitle("Creating Your Account");
                    loadingBar.setMessage("please wait while your Account is creating ... ");
                    loadingBar.show();

                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()){

                                loadingBar.dismiss();
                                pictureSelectedView.setVisibility(View.VISIBLE);
                                regFormView.setVisibility(View.INVISIBLE);

                                user_id = mAuth.getUid();
                                accountInfoRef = db.document("accounts/" + user_id);

                                validateAccountNumber();

                            } else {

                                signIn();

                            }

                        }
                    });

                }

            });

        }

    }

    public void selectProfilePicture(View view) {

        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK && result != null) {
                uri = result.getUri();
                Picasso.get().load(uri).into(pickProfilePicture);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                assert result != null;
                Exception error = result.getError();
                Toast.makeText(this, "please try again \n" + error, Toast.LENGTH_SHORT).show();
            }

        }

    }

    public void upLoadProfilePicture(View view) {

        if (uri != null) {

            final ProgressDialog loadingBar;
            loadingBar = new ProgressDialog(this);
            loadingBar.setTitle("Creating Your Account");
            loadingBar.setMessage("please wait while your Account is creating ... ");
            loadingBar.show();

            imageUploadProgress.setVisibility(View.VISIBLE);

            final StorageReference imageRef = mStorageRef.child(user_id + ".jpg");

            Task<Uri> urLTask = imageRef.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    double progress = (100.0 * task.getResult().getBytesTransferred() / task.getResult().getTotalByteCount() );
                    imageUploadProgress.setProgress((int) progress);

                    return imageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    profile_image_url = task.getResult().toString();

                    uploadAccountInfo();

                }
            });

        } else {
            Toast.makeText(this, "please select image to continue with your registration", Toast.LENGTH_SHORT).show();
        }

    }

    private void uploadAccountInfo () {

        int account_balance = 0;

        UploadAccountInfo uploadAccountInfo = new UploadAccountInfo(account_number, accountType,
                first_name, sur_name, middle_name,
                email, date_ofBirth,
                phone_no, gender, profile_image_url, account_balance);

        accountInfoRef.set(uploadAccountInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    Toast.makeText(RegisterActivity.this, "congratulations your has been created successfully", Toast.LENGTH_SHORT).show();
                    sendUserToMainActivity();

                }

            }
        });

    }

    private void validateAccountNumber() {

        accountNumberRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.exists()) {

                    AccountNumber accountNumber = documentSnapshot.toObject(AccountNumber.class);

                    int acc = accountNumber.getAccount_number();

                    account_number = 10000000 + acc + 1;

                    acc = acc + 1;

                    accountNumberRef.update("account_number", acc);

                } else {

        Map<String, Object> note = new HashMap<>();
        note.put("account_number", 1);

                    accountNumberRef.set(note);

                    account_number = 10000000 + 1;

                }

            }
        });

    }

    private void sendUserToMainActivity() {

        Intent mainAct = new Intent(this, MainActivity.class);
        mainAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainAct);
        finish();

    }

}
