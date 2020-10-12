package com.codepath.apps.restclienttemplate;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentComposeTweet#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentComposeTweet extends Fragment {

    private EditText mEditeText;

    public FragmentComposeTweet() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FragmentComposeTweet newInstance() {
        FragmentComposeTweet fragment = new FragmentComposeTweet();
        Bundle args = new Bundle();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose_tweet, container, false);
    }

    @Override
}