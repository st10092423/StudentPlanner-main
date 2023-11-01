package com.studentplanner.studentplanner.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;
import com.studentplanner.studentplanner.DatabaseHelper;
import com.studentplanner.studentplanner.MainActivity;
import com.studentplanner.studentplanner.R;
import com.studentplanner.studentplanner.activities.RegisterActivity;
import com.studentplanner.studentplanner.databinding.FragmentLoginBinding;
import com.studentplanner.studentplanner.models.Student;
import com.studentplanner.studentplanner.utils.AccountPreferences;
import com.studentplanner.studentplanner.utils.Encryption;
import com.studentplanner.studentplanner.utils.Helper;
import com.studentplanner.studentplanner.utils.Validation;


public class LoginFragment extends Fragment {
    private Context context;
    private FragmentLoginBinding binding;
    private DatabaseHelper db;

    private TextInputLayout txtEmail;
    private TextInputLayout txtPassword;
    private TextView lblLoginError;
    private Validation form;
    private Activity activity;

    public LoginFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void initFragment() {
        context = getContext();
        activity = getActivity();
        activity.setTitle(context.getString(R.string.login));
        ((AppCompatActivity) activity).getSupportActionBar().hide();

    }

    private void initFields() {
        txtEmail = binding.txtEmail;
        txtPassword = binding.txtPassword;
        lblLoginError = binding.lblLoginError;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);

        initFragment();
        initFields();

        db = DatabaseHelper.getInstance(context);
        form = new Validation(context);

        binding.btnRegisterLink.setOnClickListener(v -> Helper.goToActivity(activity, RegisterActivity.class));
        lblLoginError.setVisibility(View.INVISIBLE);
        binding.btnLogin.setOnClickListener(v -> handleLogin());


        return binding.getRoot();

    }

    private void handleLogin() {
        String email = Helper.trimStr(txtEmail);
        String password = Helper.trimStr(txtPassword, false);
        Student student = new Student(txtEmail, txtPassword);

        if (db.isAuthorised(email, Encryption.encode(password))) {
            lblLoginError.setVisibility(View.INVISIBLE);
            configUserDetails(email);
            return;
        }
        lblLoginError.setVisibility(View.VISIBLE);
    }

    private void configUserDetails(String email) {
        AccountPreferences.setLoginShredPref(context, db.getStudentID(email));
        Helper.goToActivity(activity, MainActivity.class);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}