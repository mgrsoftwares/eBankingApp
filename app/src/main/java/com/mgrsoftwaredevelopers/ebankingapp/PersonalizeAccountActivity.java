package com.mgrsoftwaredevelopers.ebankingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonalizeAccountActivity extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference accountInfoRef;
    StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("account_profile_pictures");

    String user_id;

    Uri uri;

    EditText first_Name, sur_Name, middle_Name, dob, phone_Num;
    TextView emailAdd;
    CircleImageView profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalize_account);

        initializeViews();

        loadAccountInfo();

        loadAccountInfo();

    }

    private void initializeViews() {

        profilePic = findViewById(R.id.personalize_account_photo);
        first_Name = findViewById(R.id.personalize_first_name);
        sur_Name = findViewById(R.id.personalize_sur_name);
        middle_Name = findViewById(R.id.personalize_middle_name);
        dob = findViewById(R.id.personalize_date_of_birth);
        phone_Num = findViewById(R.id.personalize_phone_number);
        emailAdd = findViewById(R.id.personalize_email);


    }

    private void loadAccountInfo() {

        user_id = mAuth.getUid();
        accountInfoRef = db.document("accounts/" + user_id);

        accountInfoRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.exists()) {

                    LoadAccountInfo loadAccountInfo = documentSnapshot.toObject(LoadAccountInfo.class);

                    assert loadAccountInfo != null;
                    String firstName = loadAccountInfo.getFirst_name(),
                            surName = loadAccountInfo.getSur_name(),
                            midName = loadAccountInfo.getMiddle_name(),
                            imgUrl = loadAccountInfo.getProfile_image_url(),
                            dateOfBirth = loadAccountInfo.getDate_ofBirth(),
                            phone = loadAccountInfo.getPhone_no(),
                            email = loadAccountInfo.getEmail();

                    Picasso.get().load(imgUrl).into(profilePic);
                    first_Name.setText(firstName);
                    sur_Name.setText(surName );
                    middle_Name.setText(midName);
                    dob.setText(dateOfBirth);
                    phone_Num.setText(phone);
                    emailAdd.setText(email);

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String msg = "something's wrong: \n" + e.getMessage();
                Toast.makeText(PersonalizeAccountActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void saveEditedUserInfo(View view) {

        String firstName = first_Name.getText().toString().trim(),
                surName = sur_Name.getText().toString().trim(),
                midName = middle_Name.getText().toString().trim(),
                dateOfBirth = dob.getText().toString().trim(),
                phone = phone_Num.getText().toString().trim(),
                email = emailAdd.getText().toString().trim();

        if (TextUtils.isEmpty(firstName)){
            first_Name.setError("first name required");
        } else if (TextUtils.isEmpty(surName)) {
            sur_Name.setError("surname required");
        } else if (TextUtils.isEmpty(dateOfBirth)){
            dob.setError("date of birth required");
        } else if (TextUtils.isEmpty(phone)){
            phone_Num.setError("phone number required");
        } else if (TextUtils.isEmpty(email)){
            emailAdd.setError("email address required");
        } else {

            final ProgressDialog loadingBar;
            loadingBar = new ProgressDialog(this);
            loadingBar.setTitle("Saving new Account Info");
            loadingBar.setMessage("please wait while your new Account Info is being saved ... ");
            loadingBar.show();

            accountInfoRef.update("first_name", firstName, "sur_name", surName, "middle_name", midName,
                    "date_of_birth", dateOfBirth, "email", email, "phone_no", phone).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {
                        Toast.makeText(PersonalizeAccountActivity.this, "New Account Info saved successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PersonalizeAccountActivity.this, "error occurred !!! \n" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                    loadingBar.dismiss();

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
                Picasso.get().load(uri).into(profilePic);

                upLoadProfilePicture();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                assert result != null;
                Exception error = result.getError();
                Toast.makeText(this, "please try again \n" + error, Toast.LENGTH_SHORT).show();
            }

        }

    }

    private void upLoadProfilePicture() {

        if (uri != null) {

            final ProgressDialog loadingBar;
            loadingBar = new ProgressDialog(this);
            loadingBar.setTitle("Uploading new picture");
            loadingBar.setMessage("please wait while your picture is uploading ... ");
            loadingBar.show();

            final StorageReference imageRef = mStorageRef.child(user_id + ".jpg");

            Task<Uri> urLTask = imageRef.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return imageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    String profile_image_url = task.getResult().toString();

                    accountInfoRef.update("profile_image_url", profile_image_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                Toast.makeText(PersonalizeAccountActivity.this, "Picture uploaded successfully", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            } else {
                                Toast.makeText(PersonalizeAccountActivity.this, "error occurred !!! \n" + task.getException(), Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }

                        }
                    });

                }
            });

        } else {
            Toast.makeText(this, "please select image to continue with your registration", Toast.LENGTH_SHORT).show();
        }

    }

    public void backToMainActivity(View view) {
        super.onBackPressed();
    }

}