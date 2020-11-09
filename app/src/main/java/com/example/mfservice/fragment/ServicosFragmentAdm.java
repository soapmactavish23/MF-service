package com.example.mfservice.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.mfservice.R;
import com.example.mfservice.activity.ServicoOrcamentoActivity;
import com.example.mfservice.adapter.AdapterUsuarios;
import com.example.mfservice.config.ConfiguracaoFirebase;
import com.example.mfservice.helper.RecyclerItemClickListener;
import com.example.mfservice.model.ServicoOrcamento;
import com.example.mfservice.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ServicosFragmentAdm extends Fragment {

    private RecyclerView recyclerView;
    private List<ServicoOrcamento> orcamentos = new ArrayList<>();
    private DatabaseReference servicosRef;
    private ValueEventListener valueEventListener;
    private AdapterUsuarios adapterUsuarios;
    private TextView txtNoneServico;

    public ServicosFragmentAdm() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_servicos_adm, container, false);

        //Inicializar Componentes
        txtNoneServico = view.findViewById(R.id.txtNoneServico);
        recyclerView = view.findViewById(R.id.recyclerServicos);
        servicosRef = ConfiguracaoFirebase.getFirebaseDatabase().child("servicoOrcamento");

        //Configurar o Recycler
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        adapterUsuarios = new AdapterUsuarios(orcamentos, getActivity());
        recyclerView.setAdapter(adapterUsuarios);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(),
                recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Usuario cliente = orcamentos.get(position).getCliente();
                        String status = orcamentos.get(position).getStatus();
                        Intent intent = new Intent(getActivity(), ServicoOrcamentoActivity.class);
                        intent.putExtra("status", status);
                        intent.putExtra("cliente", cliente);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarOrcamentos();
    }

    @Override
    public void onStop() {
        super.onStop();
        orcamentos.clear();
        servicosRef.removeEventListener(valueEventListener);
    }

    private void recuperarOrcamentos(){
        orcamentos.clear();
        valueEventListener = servicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    orcamentos.add(ds.getValue(ServicoOrcamento.class));
                }
                if(orcamentos.size() > 0){
                    txtNoneServico.setVisibility(View.GONE);
                }
                adapterUsuarios.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
