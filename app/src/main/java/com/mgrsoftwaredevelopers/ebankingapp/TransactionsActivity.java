package com.mgrsoftwaredevelopers.ebankingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class TransactionsActivity extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String user_id;
    CollectionReference transactionCollection;
//            db.collection("transactions/" + user_id + "/transaction_lists");

    TransactionAdapter transactionAdapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        user_id = mAuth.getUid();
        transactionCollection = db.collection("transactions/" + user_id + "/transaction_lists");

        initializeViews();

        loadTransactions();

    }

    private void initializeViews() {

        recyclerView = findViewById(R.id.transactions_recycler);

    }

    private void loadTransactions() {

        Query query = transactionCollection.orderBy("transaction_date", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<TransactionsInfo> options = new FirestoreRecyclerOptions.Builder<TransactionsInfo>()
                .setQuery(query, TransactionsInfo.class).build();

        transactionAdapter = new TransactionAdapter(options);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(transactionAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        transactionAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        transactionAdapter.stopListening();
    }

    public void backToMainActivity(View view) {
        super.onBackPressed();
    }

}