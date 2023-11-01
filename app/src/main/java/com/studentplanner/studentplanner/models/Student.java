package com.studentplanner.studentplanner.models;

import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

public final class Student extends User {

    private EditText txtUserPhone;

    private TextInputLayout txtPassword;
    private TextInputLayout txtPhone;


    private String phone;
    private String password;


    public EditText getTxtUserPhone() {
        return txtUserPhone;
    }

    public void setTxtUserPhone(EditText txtUserPhone) {
        this.txtUserPhone = txtUserPhone;
    }

    public TextInputLayout getTxtPassword() {
        return txtPassword;
    }

    public String getPhone() {
        return phone;
    }


    public void setTxtPhone(TextInputLayout txtPhone) {
        this.txtPhone = txtPhone;
    }

    // register form
    public Student(TextInputLayout txtFirstName, TextInputLayout txLastName, TextInputLayout txtEmail, TextInputLayout txtPassword) {
        super(txtFirstName, txLastName, txtEmail);
        this.txtPassword = txtPassword;
    }

    public Student() {

    }

    public Student(TextInputLayout txtEmail, TextInputLayout txtPassword) {
        super(txtEmail, txtPassword);
    }

    public Student(String firstname, String lastname, String email, String phone, String password) {
        super(firstname, lastname, email);
        this.phone = phone;
        this.password = password;
    }

    public Student(String firstname, String lastname, String email, String password) {
        super(firstname, lastname, email);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public Student setPassword(String password) {
        this.password = password;
        return this;
    }


}

