package com.studentplanner.studentplanner;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.google.android.material.navigation.NavigationView;
import com.studentplanner.studentplanner.databinding.ActivityMainBinding;
import com.studentplanner.studentplanner.databinding.NavHeaderBinding;
import com.studentplanner.studentplanner.fragments.HomeFragment;
import com.studentplanner.studentplanner.fragments.LoginFragment;
import com.studentplanner.studentplanner.fragments.ReminderFragment;
import com.studentplanner.studentplanner.utils.AccountPreferences;
import com.studentplanner.studentplanner.utils.FragmentHandler;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private DatabaseHelper db;
    private int studentID;
    private ActivityMainBinding binding;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getSupportFragmentManager().popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = DatabaseHelper.getInstance(this);
        studentID = AccountPreferences.getStudentID(this);
        setupNavDrawer(savedInstanceState);
        showStudentDetails();
    }


    private void openFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(binding.fragmentContainer.getId(), fragment).commit();

    }

    private void showStudentDetails() {
        NavHeaderBinding navHeaderBinding = NavHeaderBinding.bind(binding.navView.getHeaderView(0));
        navHeaderBinding.navNameLabel.setText(db.getUserFirstAndLastName(studentID).getName());
        navHeaderBinding.navEmailLabel.setText(db.getStudentEmail(studentID));
    }

    private void setupNavDrawer(Bundle savedInstanceState) {
        Toolbar toolbar = binding.toolbar;

        setSupportActionBar(toolbar);

        drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {

            if (AccountPreferences.getStudentID(this) == 0) {
                openFragment(new LoginFragment());
                return;
            }
            var selectedFragment = new HomeFragment();
            openFragment(selectedFragment);
            navigationView.setCheckedItem(FragmentHandler.activeLink(selectedFragment));
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_logout) {
            AccountPreferences.logout(this);
            openFragment(new LoginFragment());
        } else {
            openFragment(FragmentHandler.getSelectedFragment(item));
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}