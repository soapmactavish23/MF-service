package com.example.mfservice.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.mfservice.R;
import com.example.mfservice.activity.DetalhesTrabalhosFeitosActivity;
import com.example.mfservice.activity.GaleryActivity;
import com.example.mfservice.model.TrabalhosFeitos;

/**
 * A simple {@link Fragment} subclass.
 */
public class DepoisFragment extends Fragment {

    private ImageView imgDepois;
    private TrabalhosFeitos trabalhosFeitos;

    public DepoisFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_depois, container, false);
        imgDepois = view.findViewById(R.id.imgDepois);
        Bundle bundle = getActivity().getIntent().getExtras();
        trabalhosFeitos = (TrabalhosFeitos) bundle.getSerializable("trabalhoFeitoSelecionado");

        Glide.with(getActivity()).load(trabalhosFeitos.getFotoDepois()).into(imgDepois);

        imgDepois.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GaleryActivity.class);
                intent.putExtra("foto", trabalhosFeitos.getFotoDepois());
                intent.putExtra("titulo", "Depois");
                startActivity(intent);
            }
        });

        return view;
    }
}
