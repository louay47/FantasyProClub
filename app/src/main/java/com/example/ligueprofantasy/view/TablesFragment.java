package com.example.ligueprofantasy.view;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ligueprofantasy.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TablesFragment extends Fragment {


    public TablesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tables2, container, false);
    }

}
