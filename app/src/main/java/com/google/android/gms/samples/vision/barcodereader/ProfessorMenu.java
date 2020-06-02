package com.google.android.gms.samples.vision.barcodereader;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MenuItem;

import java.lang.reflect.Field;

public class ProfessorMenu extends FragmentActivity {

    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buttom_navigation_bar);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        disableShiftMode(bottomNavigation);

        ProfessorHomeFragment professorHomeFragment = new ProfessorHomeFragment();

        FragmentTransaction transactionHome = getSupportFragmentManager().beginTransaction();
        transactionHome.add(R.id.fragmentContainer, professorHomeFragment).commit();

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        ProfessorHomeFragment professorHomeFragment = new ProfessorHomeFragment();

                        FragmentTransaction transactionHome = getSupportFragmentManager().beginTransaction();
                        transactionHome.replace(R.id.fragmentContainer, professorHomeFragment).commit();
                        break;
                    case R.id.action_see_questions:
                        SeeQuestionsFragment seeQuestionsFragment = new SeeQuestionsFragment();

                        FragmentTransaction transactionQuestions = getSupportFragmentManager().beginTransaction();
                        transactionQuestions.replace(R.id.fragmentContainer, seeQuestionsFragment).commit();
                        break;
                    case R.id.action_see_tests:
                        SeeTestsFragment seeTestsFragment = new SeeTestsFragment();

                        FragmentTransaction transactionTests = getSupportFragmentManager().beginTransaction();
                        transactionTests.replace(R.id.fragmentContainer, seeTestsFragment).commit();
                        break;
                    case R.id.action_see_students:
                        SeeStudentsFragment seeStudentsFragment = new SeeStudentsFragment();

                        FragmentTransaction transactionStudents = getSupportFragmentManager().beginTransaction();
                        transactionStudents.replace(R.id.fragmentContainer, seeStudentsFragment).commit();
                        break;
                }
                return true;
            }
        });
    }

    public void openNewFragment(Fragment newFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, newFragment).commit();
    }

    @SuppressLint("RestrictedApi")
    private void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                //noinspection RestrictedApi
                item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                //noinspection RestrictedApi
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
        } catch (IllegalAccessException e) {
        }
    }
}
