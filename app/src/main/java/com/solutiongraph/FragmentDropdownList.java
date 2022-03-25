package com.solutiongraph;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentDropdownList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentDropdownList extends Fragment {

    private static final String INDEX = "index";
    private String index;

    public FragmentDropdownList() {
        super(R.layout.fragment_dropdown_list);
    }

    public static FragmentDropdownList newInstance(String param1) {
        FragmentDropdownList fragment = new FragmentDropdownList();
        Bundle args = new Bundle();
        args.putString(INDEX, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            index = getArguments().getString(INDEX);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dropdown_list, container, false);
    }
}