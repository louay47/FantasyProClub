package com.fl.ligueprofantasy.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.fl.ligueprofantasy.R;

public abstract class SingleFragmentActivity extends AppCompatActivity {

    protected abstract Fragment createFragment();

    protected abstract Bundle getBundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.flContent);

        if (fragment == null) {
            fragment = createFragment();
            Bundle args = getBundle();
            fragment.setArguments(args);
            fm.beginTransaction().add(R.id.flContent,fragment).commit();
        }
    }
}
