package com.mgrsoftwaredevelopers.ebankingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference accountInfoRef;

    String user_id;

    CircleImageView accPhoto;
    TextView accDetails, mastercardName;
    LinearLayout masterView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();

        loadAccountInfo();

    }

    private void initializeViews() {

        accPhoto = findViewById(R.id.account_photo);
        accDetails = findViewById(R.id.account_details);
        masterView = findViewById(R.id.master_card_services_view);
        mastercardName = findViewById(R.id.master_card_holder_name);

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

                    int accBalance = loadAccountInfo.getAccount_balance(),
                            accNum = loadAccountInfo.getAccount_number();

                    String accBal ,
                            firstName = loadAccountInfo.getFirst_name(),
                            surName = loadAccountInfo.getSur_name(),
                            midName = loadAccountInfo.getMiddle_name(),
                            imgUrl = loadAccountInfo.getProfile_image_url(),
                            accType = loadAccountInfo.getAccountType(),
                            acc_info = firstName + " " + surName + " " + midName + "\n" + accType + "\nAcc No: " + accNum + "\nBalance: ";

                    NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
                    accBal = format.format(accBalance);

                    Picasso.get().load(imgUrl).into(accPhoto);
                    accDetails.setText(String.format("%s%s", acc_info, accBal));
                    mastercardName.setText(String.format("%s %s %s \n%s", surName, firstName, midName, accNum));


                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String msg = "something's wrong: \n" + e.getMessage();
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                accDetails.setText(msg);
            }
        });

    }

//    todo step 1 begins

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null){

            sendUserToLoginActivity();

        }

        loadAccountInfo();

    }

    private void sendUserToLoginActivity() {

        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

//    todo step 1 ends


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mAuth.signOut();
        sendUserToLoginActivity();
    }

    public void dashBoardClicks(View view) {

        switch (view.getId()) {

            case R.id.make_payment_btn:

                openMakePayments();

                break;

            case R.id.transaction_btn:

                openTransactions();

                break;

            case R.id.card_btn:

                openCardServices();

                break;

            case R.id.profile_btn:

                openProfileSettings();

                break;

        }

    }

    private void openProfileSettings() {

        Intent intent = new Intent(MainActivity.this, PersonalizeAccountActivity.class);
        startActivity(intent);

    }

    private void openCardServices() {

        if (!(masterView.isShown())) {

            masterView.setVisibility(View.VISIBLE);

        } else {

            masterView.setVisibility(View.GONE);

        }

    }

    private void openHome() {
    }

    private void openTransactions() {

        Intent intent = new Intent(MainActivity.this, TransactionsActivity.class);
        startActivity(intent);

    }

    private void openMakePayments() {

        Intent intent = new Intent(MainActivity.this, MakePaymentsActivity.class);
        startActivity(intent);

    }

    public void bottomNavBar(View view) {

        switch (view.getId()) {

            case R.id.make_payment_bottom:

                openMakePayments();

                break;

            case R.id.transaction_bottom:

                openTransactions();

                break;

            case R.id.home_bottom:

                openHome();

                break;

            case R.id.card_bottom:

                openCardServices();

                break;

            case R.id.profile_bottom:

                openProfileSettings();

                break;

        }

    }

    public void cardServices(View view) {
//        request card, block / unblock card

        switch (view.getId()) {

            case R.id.request_card_btn:

                Toast.makeText(this, "your request for new debit/credit card is granted and processing",
                        Toast.LENGTH_LONG).show();

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Card Requisition");
                builder.setMessage("your request for new debit/credit card is granted and processing");
                builder.show();

                break;

            case R.id.block_card_btn:

                Toast.makeText(this, "your debit/credit card is blocked", Toast.LENGTH_LONG).show();

                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                builder1.setTitle("Block Card");
                builder1.setMessage("your debit/credit card is blocked");
                builder1.show();

                break;

        }

    }

}
