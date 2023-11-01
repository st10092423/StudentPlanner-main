package com.studentplanner.studentplanner.models;

import com.google.android.material.textfield.TextInputLayout;

import org.apache.commons.text.WordUtils;

public abstract class User {
    private int userID;
    private String firstname;
    private String lastname;
    private String email;
    private TextInputLayout txtFirstName;
    private TextInputLayout txtLastName;
    private TextInputLayout txtEmail;

    public void setEmail(String email) {
        this.email = email;
    }

    private TextInputLayout txtPassword;

    public TextInputLayout getTxtPassword() {
        return txtPassword;
    }

    public void setTxtPassword(TextInputLayout txtPassword) {
        this.txtPassword = txtPassword;
    }

    public User() {
    }

    public void setTxtEmail(TextInputLayout txtEmail) {
        this.txtEmail = txtEmail;
    }

    public User(TextInputLayout txtFirstName, TextInputLayout txLastName, TextInputLayout txtEmail) {
        this.txtFirstName = txtFirstName;
        this.txtLastName = txLastName;
        this.txtEmail = txtEmail;
    }

    public User(TextInputLayout txtEmail, TextInputLayout txtPassword) {
        this.txtEmail = txtEmail;
        this.txtPassword = txtPassword;
    }

    public User(String firstname, String lastname, String email) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
    }

    public User(int userID, String firstname, String lastname, String email) {
        this.userID = userID;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
    }

    public TextInputLayout getTxtFirstName() {
        return txtFirstName;
    }

    public TextInputLayout getTxtLastName() {
        return txtLastName;
    }

    public TextInputLayout getTxtEmail() {
        return txtEmail;
    }

    public int getUserID() {
        return userID;
    }


    public User setFirstname(String firstname) {
        this.firstname = firstname;
        return this;
    }

    public String getFirstname() {
        return firstname;
    }

    public User setLastname(String lastname) {
        this.lastname = lastname;
        return this;
    }

    public String getLastname() {
        return lastname;
    }


    public String getEmail() {
        return email;
    }

    public String getName() {
        return WordUtils.capitalizeFully(String.format("%s %s", firstname, lastname));
    }
}

