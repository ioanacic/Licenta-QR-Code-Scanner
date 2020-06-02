package com.google.android.gms.samples.vision.barcodereader;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

public class StudentMenuActivity extends FragmentActivity {

    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_navigation_bar);
        bottomNavigation = findViewById(R.id.bottom_navigation);

        StudentHomeFragment studentHomeFragment = new StudentHomeFragment();

        FragmentTransaction transactionHome = getSupportFragmentManager().beginTransaction();
        transactionHome.add(R.id.fragmentContainerStudent, studentHomeFragment).commit();

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        StudentHomeFragment studentHomeFragment = new StudentHomeFragment();

                        FragmentTransaction transactionHome = getSupportFragmentManager().beginTransaction();
                        transactionHome.replace(R.id.fragmentContainerStudent, studentHomeFragment).commit();
                        break;
                    case R.id.action_scan_qr:
                        Intent intent = new Intent(StudentMenuActivity.this, BarcodeCaptureActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.action_history:
                        HistoryFragment historyFragment = new HistoryFragment();

                        FragmentTransaction transactionHistory = getSupportFragmentManager().beginTransaction();
                        transactionHistory.replace(R.id.fragmentContainerStudent, historyFragment).commit();

                        break;
                }
                return true;
            }
        });
    }

    public void openNewFragment(Fragment newFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainerStudent, newFragment).commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
