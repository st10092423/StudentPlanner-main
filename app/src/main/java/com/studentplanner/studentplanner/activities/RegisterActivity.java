package com.studentplanner.studentplanner.activities;

import static com.studentplanner.studentplanner.utils.Helper.setEditTextMaxLength;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;
import com.studentplanner.studentplanner.DatabaseHelper;
import com.studentplanner.studentplanner.R;
import com.studentplanner.studentplanner.databinding.ActivityRegisterBinding;
import com.studentplanner.studentplanner.models.Student;
import com.studentplanner.studentplanner.utils.AlertDialogFragment;
import com.studentplanner.studentplanner.utils.Helper;
import com.studentplanner.studentplanner.utils.PasswordValidator;
import com.studentplanner.studentplanner.utils.Validation;

import java.text.MessageFormat;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout txtFirstName;
    private TextInputLayout txtLastName;
    private TextInputLayout txtEmail;
    private EditText txtPhone;
    private TextInputLayout txtPassword;
    private DatabaseHelper db;
    private Validation form;
    private ProgressBar progressBar;
    private MaterialCheckBox terms;
    private AlertDialogFragment alertDialogFragment;
    private ActivityRegisterBinding binding;
    private CountryCodePicker countryCodePicker;
    private PasswordValidator passwordValidator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initFields();
        passwordValidator = new PasswordValidator(this, binding.progressBar, binding.lblPasswordStrength);
        db = DatabaseHelper.getInstance(this);
        form = new Validation(this, db);
        alertDialogFragment = new AlertDialogFragment(this);


        binding.btnRegister.setOnClickListener(v -> handleRegister());
    }


    private void handleRegister() {
        Student student = new Student(txtFirstName, txtLastName, txtEmail, txtPassword);
        student.setTxtUserPhone(txtPhone);

        if (!form.validateRegisterForm(student)) return;
        if (!terms.isChecked()) {
            alertDialogFragment.showTermsPolicyError();
            return;
        }
        if (db.registerStudent(getStudentDetails())) {
            Helper.longToastMessage(this, getString(R.string.account_created));
            finish();
            return;
        }

        Helper.longToastMessage(this, getString(R.string.create_account_error));

    }

    private void initFields() {
        txtFirstName = binding.txtFirstname;
        txtLastName = binding.txtLastname;
        txtEmail = binding.txtEmail;
        txtPhone = binding.txtPhone;
        txtPassword = binding.txtPassword;
        countryCodePicker = binding.ccp;
        countryCodePicker.setAutoDetectedCountry(true);
        terms = binding.checkboxTermsConditions;
        binding.txtPasswordText.setOnKeyListener((v, keyCode, event) -> {
            updatePasswordStrengthView(txtPassword.getEditText().getText().toString());
            return false;
        });

        txtPhone.setOnKeyListener((v, keyCode, event) -> {
            String phone = txtPhone.getText().toString();
            final int phoneLength = phone.startsWith("0") ? 11 : 10;
            setEditTextMaxLength(txtPhone, phoneLength);
            return false;
        });

    }


    private Student getStudentDetails() {
        String phone = txtPhone.getText().toString();
        if (!phone.isBlank()) {
            return new Student(
                    Helper.trimStr(txtFirstName),
                    Helper.trimStr(txtLastName),
                    Helper.trimStr(txtEmail),
                    MessageFormat.format("{0}{1}", countryCodePicker.getSelectedCountryCodeWithPlus(), txtPhone.getText().toString()),
                    Helper.trimStr(txtPassword, false)
            );

        }

        return new Student(
                Helper.trimStr(txtFirstName),
                Helper.trimStr(txtLastName),
                Helper.trimStr(txtEmail),
                Helper.trimStr(txtPassword, false)
        );


    }

    private void updatePasswordStrengthView(String password) {
        int strength = 0;
        if (PasswordValidator.is8Chars(password)) strength++;
        if (PasswordValidator.containsSpecialChar(password)) strength++;
        if (PasswordValidator.containsUpperCase(password)) strength++;
        if (PasswordValidator.containsLowerCase(password)) strength++;
        if (PasswordValidator.containsNumber(password)) strength++;
        passwordValidator.getProgressBarStatus(strength);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return true;
    }
}