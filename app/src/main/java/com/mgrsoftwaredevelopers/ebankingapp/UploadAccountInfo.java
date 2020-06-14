package com.mgrsoftwaredevelopers.ebankingapp;

public class UploadAccountInfo {

    private String accountType,
            first_name, sur_name, middle_name,
            email, date_ofBirth,
            phone_no, gender, profile_image_url;

    private int account_balance, account_number;

    public UploadAccountInfo() {
    }

    public UploadAccountInfo(int account_number, String account_type, String first_name,
                             String sur_name, String middle_name, String email,
                             String date_of_birth, String phone_no, String gender,
                             String profile_image_url, int account_balance) {

        if (middle_name.trim().equals("")) {
            middle_name = "";
        }

        this.account_number = account_number;
        this.accountType = account_type;
        this.first_name = first_name;
        this.sur_name = sur_name;
        this.middle_name = middle_name;
        this.email = email;
        this.date_ofBirth = date_of_birth;
        this.phone_no = phone_no;
        this.gender = gender;
        this.profile_image_url = profile_image_url;
        this.account_balance = account_balance;
    }

    public int getAccount_number() {
        return account_number;
    }

    public void setAccount_number(int account_number) {
        this.account_number = account_number;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getSur_name() {
        return sur_name;
    }

    public void setSur_name(String sur_name) {
        this.sur_name = sur_name;
    }

    public String getMiddle_name() {
        return middle_name;
    }

    public void setMiddle_name(String middle_name) {
        this.middle_name = middle_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDate_ofBirth() {
        return date_ofBirth;
    }

    public void setDate_ofBirth(String date_ofBirth) {
        this.date_ofBirth = date_ofBirth;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfile_image_url() {
        return profile_image_url;
    }

    public void setProfile_image_url(String profile_image_url) {
        this.profile_image_url = profile_image_url;
    }

    public int getAccount_balance() {
        return account_balance;
    }

    public void setAccount_balance(int account_balance) {
        this.account_balance = account_balance;
    }

}
