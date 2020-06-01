package com.google.android.gms.samples.vision.barcodereader;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

public class BottomNavigationBar extends FragmentActivity implements BottomNavigationBarInterface{

    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buttom_navigation_bar);
        bottomNavigation = findViewById(R.id.bottom_navigation);

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_account:
                        ProfessorAccountFragment myFr = new ProfessorAccountFragment();
                        myFr.setup("f", "f", BottomNavigationBar.this);

                        FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
                        transaction1.replace(R.id.fragmentContainer, myFr).commit();
                        break;
                    case R.id.action_qr:
                        SeeQuestionsFragment myFrr = new SeeQuestionsFragment();

                        FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
                        transaction2.replace(R.id.fragmentContainer, myFrr).commit();
                        break;
                    case R.id.action_camera:
                        Toast.makeText(BottomNavigationBar.this, "Nearby", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });
    }

    @Override

    // getactivity.opennewfragment
    public void openNewFragment(Fragment fr) {
        Toast.makeText(BottomNavigationBar.this, "open new fragment", Toast.LENGTH_SHORT).show();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fr).commit();
    }
}
