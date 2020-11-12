package com.example.mfservice.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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
    private List<Usuario> clientes = new ArrayList<>();
    private DatabaseReference firebaseRef, servicosRef, usuarioRef;
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
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        servicosRef = firebaseRef.child("servicoOrcamento");
        usuarioRef = firebaseRef.child("usuarios");

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
                        Usuario cliente = clientes.get(position);
                        ServicoOrcamento servicoOrcamentoSelecionado = orcamentos.get(position);
                        Intent intent = new Intent(getActivity(), ServicoOrcamentoActivity.class);
                        intent.putExtra("servicoOrcamentoSelecionado", servicoOrcamentoSelecionado);
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
                    ServicoOrcamento servicoOrcamento = ds.getValue(ServicoOrcamento.class);
                    recuperarUsuarios(servicoOrcamento.getIdCliente());
                    orcamentos.add(servicoOrcamento);

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

    private void recuperarUsuarios(String idUsuario){
        usuarioRef.child(idUsuario).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                clientes.add(snapshot.getValue(Usuario.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
