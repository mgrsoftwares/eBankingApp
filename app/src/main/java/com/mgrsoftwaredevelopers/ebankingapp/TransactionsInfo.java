package com.mgrsoftwaredevelopers.ebankingapp;

import com.google.firebase.firestore.Exclude;

public class TransactionsInfo {

    private String receiver_account_name,
             sender_account_name,
            transaction_date, transaction_description,
            transaction_type;

    private int receiver_account_number, sender_account_number, transaction_amount;

    public TransactionsInfo() {
    }

    public TransactionsInfo(String receiver_account_name, String sender_account_name,
                            String transaction_date, String transaction_description,
                            String transaction_type, int receiver_account_number,
                            int sender_account_number, int transaction_amount) {


        this.receiver_account_name = receiver_account_name;
        this.sender_account_name = sender_account_name;

        this.transaction_date = transaction_date;
        this.transaction_description = transaction_description;
        this.transaction_type = transaction_type;
        this.transaction_amount = transaction_amount;

        this.receiver_account_number = receiver_account_number;
        this.sender_account_number = sender_account_number;

    }

    public String getReceiver_account_name() {
        return receiver_account_name;
    }

    public void setReceiver_account_name(String receiver_account_name) {
        this.receiver_account_name = receiver_account_name;
    }

    public String getSender_account_name() {
        return sender_account_name;
    }

    public void setSender_account_name(String sender_account_name) {
        this.sender_account_name = sender_account_name;
    }

    public String getTransaction_date() {
        return transaction_date;
    }

    public void setTransaction_date(String transaction_date) {
        this.transaction_date = transaction_date;
    }

    public String getTransaction_description() {
        return transaction_description;
    }

    public void setTransaction_description(String transaction_description) {
        this.transaction_description = transaction_description;
    }

    public String getTransaction_type() {
        return transaction_type;
    }

    public void setTransaction_type(String transaction_type) {
        this.transaction_type = transaction_type;
    }

    public int getReceiver_account_number() {
        return receiver_account_number;
    }

    public void setReceiver_account_number(int receiver_account_number) {
        this.receiver_account_number = receiver_account_number;
    }

    public int getSender_account_number() {
        return sender_account_number;
    }

    public void setSender_account_number(int sender_account_number) {
        this.sender_account_number = sender_account_number;
    }

    public int getTransaction_amount() {
        return transaction_amount;
    }

    public void setTransaction_amount(int transaction_amount) {
        this.transaction_amount = transaction_amount;
    }

}
