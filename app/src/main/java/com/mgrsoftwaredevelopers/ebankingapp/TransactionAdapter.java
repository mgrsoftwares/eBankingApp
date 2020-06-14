package com.mgrsoftwaredevelopers.ebankingapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.text.NumberFormat;
import java.util.Locale;

public class TransactionAdapter extends FirestoreRecyclerAdapter <TransactionsInfo, TransactionAdapter.TransactionHolder> {

    public TransactionAdapter(@NonNull FirestoreRecyclerOptions<TransactionsInfo> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull TransactionHolder holder, int position, @NonNull TransactionsInfo model) {
        String receiver_account_name = model.getReceiver_account_name(),
                sender_account_name = model.getSender_account_name(),
                transaction_date = model.getTransaction_date(),
                transaction_description = model.getTransaction_description(),
                transaction_type = model.getTransaction_type();

        int transaction_amount = model.getTransaction_amount(),
                receiver_account_number = model.getReceiver_account_number(),
                sender_account_number = model.getSender_account_number();

        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
        String currency = format.format(transaction_amount);

        String details = transaction_date + "\n" + "Transaction: " + transaction_type + "\n" + "Amount: " + currency + "\n"
                + "Description: " + transaction_description,
                sender_details = "Account Name: " + sender_account_name + "\n" + "Account No: " + sender_account_number,
                receiver_details = "Account Name: " + receiver_account_name + "\n" + "Account No: " + receiver_account_number;

        holder.transactionDetails.setText(details);
        holder.senderDetails.setText(sender_details);
        holder.receiverDetails.setText(receiver_details);

    }

    @NonNull
    @Override
    public TransactionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_view_layout, parent, false);
        return new TransactionHolder(view);
    }

    class TransactionHolder extends RecyclerView.ViewHolder {

        TextView transactionDetails, receiverDetails, senderDetails;

        public TransactionHolder(@NonNull View itemView) {
            super(itemView);

            transactionDetails = itemView.findViewById(R.id.transaction_details);
            receiverDetails = itemView.findViewById(R.id.receiver_details);
            senderDetails = itemView.findViewById(R.id.sender_details);

        }
    }

}
