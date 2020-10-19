package com.example.mrservice.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mrservice.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrcamentoProdutoFragment extends Fragment {

    public OrcamentoProdutoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_orcamento_produto, container, false);
        return view;
    }
}
