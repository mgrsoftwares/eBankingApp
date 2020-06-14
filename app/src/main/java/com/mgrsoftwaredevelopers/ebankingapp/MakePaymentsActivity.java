package com.mgrsoftwaredevelopers.ebankingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MakePaymentsActivity extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference accountsCollection = db.collection("accounts");
    DocumentReference accountInfoRef, secretPinRef, receiverAccountInfoRef, receiverTransactionRef, senderTransactionRef;

    String user_id, secret_pin_number = "", transactionDescription, senderAccountName, receiverAccountName, receiverUserId,
            transactionDateTime;
    int accBalance, amount_to_send, senderAccountNumber, receiverAccountBalance, receiverAccountNumber;

    TextView accountName, accountType, accountNumber, accountBalance, receiverAccount, receiverAccountView;
    EditText searchAccountBar, amountToSend, paymentDescription, secretPin, newSecretPin, confirmNewSecretPin;
    Button verifyPaymentBtn;
    CircleImageView receiverProfileImage;

    ScrollView makePaymentView;
    RelativeLayout accountListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_payments);

        initializeViews();

        loadAccountInfo();

        checkSecretPin();

        datePicker();

    }

    private void initializeViews() {

        accountName = findViewById(R.id.from_account);
        accountType = findViewById(R.id.from_account_type);
        accountNumber = findViewById(R.id.from_account_number);
        accountBalance = findViewById(R.id.from_account_balance);
        amountToSend = findViewById(R.id.make_payment_amount); /* make_payment_amount */
        paymentDescription = findViewById(R.id.make_payment_description);
        secretPin = findViewById(R.id.make_payment_pin);
        newSecretPin = findViewById(R.id.make_payment_new_pin);
        confirmNewSecretPin = findViewById(R.id.make_payment_confirm_pin);
        searchAccountBar = findViewById(R.id.search_account_number);
        receiverAccount = findViewById(R.id.receiver_account);
        receiverAccountView  = findViewById(R.id.receiver_account_view);
        receiverProfileImage =  findViewById(R.id.receiver_account_photo);

        makePaymentView = findViewById(R.id.make_payment_view);

        accountListView = findViewById(R.id.account_list_View);
        verifyPaymentBtn = findViewById(R.id.verify_payment);

    }

    private void loadAccountInfo() {

        user_id = mAuth.getUid();
        accountInfoRef = db.document("accounts/" + user_id);

        accountInfoRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.exists()) {

                    LoadAccountInfo loadAccountInfo = documentSnapshot.toObject(LoadAccountInfo.class);

                    assert loadAccountInfo != null;

                    accBalance = loadAccountInfo.getAccount_balance();
                    senderAccountNumber = loadAccountInfo.getAccount_number();

                    String bal ,
                            firstName = loadAccountInfo.getFirst_name(),
                            surName = loadAccountInfo.getSur_name(),
                            midName = loadAccountInfo.getMiddle_name(),
                            accType = loadAccountInfo.getAccountType(),
                            acNo = "Account Number: " + senderAccountNumber;
                            senderAccountName = surName + " " + firstName + " " + midName ;

                    NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
                    bal = format.format(accBalance);


                    accountName.setText(senderAccountName);
                    accountBalance.setText(bal);
                    accountNumber.setText(acNo);
                    accountType.setText(accType);

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String msg = "something's wrong: \n" + e.getMessage();
                Toast.makeText(MakePaymentsActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });

    }

//    Button Clicks Listeners

    public void makePaymentsClicks(View view) {

        switch (view.getId()) {

            case R.id.receiver_account:

                getSenderAccountDetails();

                break;

            case R.id.verify_payment:

                verifyPaymentView();

                break;

        }

    }

    public void backToMainActivity(View view) {

        if (accountListView.isShown()){
            accountListView.setVisibility(View.GONE);
            if (!(makePaymentView.isShown())){
                makePaymentView.setVisibility(View.VISIBLE);
            }
        } else {
            super.onBackPressed();
        }

    }

    public void searchAccount(View view) {

        receiverAccountNumber = Integer.parseInt(searchAccountBar.getText().toString().trim());

        accountsCollection.whereEqualTo("account_number", receiverAccountNumber).
                get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                if (!(queryDocumentSnapshots.isEmpty())){

                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                        receiverProfileImage.setVisibility(View.VISIBLE);

                        LoadAccountInfo loadAccountInfo = documentSnapshot.toObject(LoadAccountInfo.class);
                        loadAccountInfo.setDocumentId(documentSnapshot.getId());
                        receiverUserId = loadAccountInfo.getDocumentId();
                        receiverAccountBalance = loadAccountInfo.getAccount_balance();

                        receiverAccountNumber = loadAccountInfo.getAccount_number();
                        String firstName = loadAccountInfo.getFirst_name(),
                                surName = loadAccountInfo.getSur_name(),
                                midName = loadAccountInfo.getMiddle_name(),
                                imgUrl = loadAccountInfo.getProfile_image_url(),
                                receiverAccInfo = surName + firstName + midName + "\n" + receiverAccountNumber;

                        receiverAccountName = surName + " " + firstName + " " + midName;

                        receiverAccount.setText(receiverAccInfo);
                        receiverAccountView.setText(receiverAccInfo);
                        Picasso.get().load(imgUrl).into(receiverProfileImage);

                    }

                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(MakePaymentsActivity.this);
                    builder.setTitle("Invalid Account Number");
                    builder.setMessage("Please select a valid account number");
                    builder.show();

                }

            }
        });

    }

//    Button Clicks Listeners


    private void getSenderAccountDetails() {

        makePaymentView.setVisibility(View.GONE);
        accountListView.setVisibility(View.VISIBLE);

    }

    private void verifyPaymentView() {

        if (verifyPaymentBtn.getText().toString().trim().equals("Create New Pin")) {

            handleSecretPinCreation();

        } else if (verifyPaymentBtn.getText().toString().trim().equals("Verify Payment")) {

            validatePaymentFields();

        }

    }

    private void checkSecretPin() {

        secretPinRef = db.document("Secret_Pin/" + user_id);

        secretPinRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.exists()){

                    SecretPin secretPin = documentSnapshot.toObject(SecretPin.class);

                    assert secretPin != null;
                    secret_pin_number = secretPin.getSecret_pin();

                    handlePaymentTransaction();

                } else {

                    String btName = "Create New Pin";

                    verifyPaymentBtn.setText(btName);

                    amountToSend.setVisibility(View.GONE);
                    paymentDescription.setVisibility(View.GONE);
                    secretPin.setVisibility(View.GONE);
                    receiverAccount.setVisibility(View.GONE);

                    newSecretPin.setVisibility(View.VISIBLE);
                    confirmNewSecretPin.setVisibility(View.VISIBLE);

                }

            }
        });

    }

    private void handleSecretPinCreation() {

        final String newPin = newSecretPin.getText().toString(),
                confirmPin = confirmNewSecretPin.getText().toString();

        if (TextUtils.isEmpty(newPin)) {

            newSecretPin.setError("Enter Secret Pin");

        } else if (!newPin.equals(confirmPin)) {

            confirmNewSecretPin.setError("wrong Pin! \n Enter correct Pin");

        } else {

            final SecretPin secPin = new SecretPin(newPin);
            secretPinRef.set(secPin).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {

                        handlePaymentTransaction();

                        secret_pin_number = newPin;
                        secretPin.setText(newPin);

                        Toast.makeText(MakePaymentsActivity.this, "Secret PIN Created Successfully", Toast.LENGTH_SHORT).show();

                    } else {

                        checkSecretPin();

                        Toast.makeText(MakePaymentsActivity.this, "Error!!! \n" + task.getException(), Toast.LENGTH_SHORT).show();

                    }

                }
            });

        }

    }

    private void handlePaymentTransaction() {

        String btName = "Verify Payment";

        verifyPaymentBtn.setText(btName);

        newSecretPin.setVisibility(View.GONE);
        confirmNewSecretPin.setVisibility(View.GONE);

        amountToSend.setVisibility(View.VISIBLE);
        paymentDescription.setVisibility(View.VISIBLE);
        secretPin.setVisibility(View.VISIBLE);
        receiverAccount.setVisibility(View.VISIBLE);

    }

    private void validatePaymentFields() {

        int errColor = R.color.colorRed;

        transactionDescription = paymentDescription.getText().toString().trim();
        String amount = amountToSend.getText().toString().trim(),
                pin = secretPin.getText().toString().trim(),
                accountToPay = receiverAccount.getText().toString().trim();

        if (accountToPay.equals("")) {

          String error = "no account selected";
          receiverAccount.setText(error);
          receiverAccount.setError(error);

        } else if (TextUtils.isEmpty(amount)) {

            amountToSend.setError("amount needed");
            amountToSend.setText("0");
            amountToSend.setTextColor(getResources().getColor(errColor));

        } else if (TextUtils.isEmpty(pin)) {

            secretPin.setError("secret pin needed");
            secretPin.setText("0");
            secretPin.setTextColor(getResources().getColor(errColor));

        } else if (!(pin.equals(secret_pin_number))) {

            secretPin.setError("wrong secret pin");
            secretPin.setText("0");
            secretPin.setTextColor(getResources().getColor(errColor));

        } else {

            amount_to_send = Integer.parseInt(amount);

            secretPin.setTextColor(0);
            amountToSend.setTextColor(0);

            if (accBalance == 0) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MakePaymentsActivity.this);
                builder.setTitle("Zero Account");
                builder.setMessage("Sorry your account balance is zero (0.00) ");
                builder.show();

                verifyPaymentBtn.setEnabled(false);

            } else if (accBalance < amount_to_send) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MakePaymentsActivity.this);
                builder.setTitle("Insufficient Funds");
                builder.setMessage("Sorry you do not have enough account balance to complete this transaction");
                builder.show();

            } else if (amount_to_send == 0) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MakePaymentsActivity.this);
                builder.setTitle("Invalid Amount");
                builder.setMessage("Sorry you cannot send 0.00 amount");
                builder.show();

            } else {

                NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
                String money = format.format(amount_to_send);

                AlertDialog.Builder builder = new AlertDialog.Builder(MakePaymentsActivity.this);
                builder.setTitle("Confirm Payment");
                builder.setMessage("Are you sure you want to pay " + receiverAccountName +  " the sum of " + money);
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        datePicker();
                        completeTransaction();

                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        onBackPressed();

                    }
                });
                builder.show();

            }

        }

    }

    private void completeTransaction() {

        receiverAccountInfoRef = db.document("accounts/" + receiverUserId);

        receiverAccountInfoRef.update("account_balance", amount_to_send + receiverAccountBalance)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    createReceiverTransactionRecord();

                    accBalance = accBalance - amount_to_send;
                    accountInfoRef.update("account_balance", accBalance)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                createSenderTransactionRecord();

                            } else {

                                Toast.makeText(MakePaymentsActivity.this, "Error!!! \n" + task.getException(), Toast.LENGTH_SHORT).show();

                            }

                        }
                    });

                } else {

                    Toast.makeText(MakePaymentsActivity.this, "Error!!! \n" + task.getException(), Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    private void createReceiverTransactionRecord() {

        TransactionsInfo transactionsInfo = new TransactionsInfo(receiverAccountName, senderAccountName,
                transactionDateTime, transactionDescription,
                "Credit", receiverAccountNumber,
                senderAccountNumber, amount_to_send);

        receiverTransactionRef.set(transactionsInfo);

    }

    private void createSenderTransactionRecord() {

        TransactionsInfo transactionsInfo = new TransactionsInfo(receiverAccountName, senderAccountName,
                transactionDateTime, transactionDescription,
                "Debit", receiverAccountNumber,
                senderAccountNumber, amount_to_send);

        senderTransactionRef.set(transactionsInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    onBackPressed();
                }

            }
        });

    }

    @Override
    public void onBackPressed() {

        if (accountListView.isShown()){
            accountListView.setVisibility(View.GONE);
            if (!(makePaymentView.isShown())){
                makePaymentView.setVisibility(View.VISIBLE);
            }
        } else {
            super.onBackPressed();
        }

    }

    public void SelectReceiverAccount(View view) {

        makePaymentView.setVisibility(View.VISIBLE);
        accountListView.setVisibility(View.GONE);

    }

    private void datePicker() {

        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()),
                time = new SimpleDateFormat("HH:mm:ss Z", Locale.getDefault()).format(new Date());
        transactionDateTime = "Date: " + date + " Time: " + time;

        receiverTransactionRef = db.document("transactions/" + receiverUserId + "/transaction_lists/" + date + time);
        senderTransactionRef = db.document("transactions/" + user_id + "/transaction_lists/" + date + time);

    }

}
