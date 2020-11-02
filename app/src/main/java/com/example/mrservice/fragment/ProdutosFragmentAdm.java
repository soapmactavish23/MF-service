package com.example.mrservice.fragment;

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

import com.example.mrservice.R;
import com.example.mrservice.activity.ProdutoOrcamentoActivity;
import com.example.mrservice.adapter.AdapterClientes;
import com.example.mrservice.adapter.AdapterOrcamentoProduto;
import com.example.mrservice.adapter.AdapterUsuarios;
import com.example.mrservice.config.ConfiguracaoFirebase;
import com.example.mrservice.helper.RecyclerItemClickListener;
import com.example.mrservice.model.Produto;
import com.example.mrservice.model.ProdutoOrcamento;
import com.example.mrservice.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProdutosFragmentAdm extends Fragment {

    private RecyclerView recyclerView;
    private List<ProdutoOrcamento> orcamentos = new ArrayList<>();
    private List<String> ids = new ArrayList<>();
    private List<Usuario> clientes = new ArrayList<>();
    private DatabaseReference produtosOrcamentosRef;
    private DatabaseReference usuariosRef;
    private ValueEventListener valueEventListener;
    private AdapterOrcamentoProduto adapterOrcamentoProduto;
    private AdapterUsuarios adapterUsuarios;
    private TextView txtNone;

    public ProdutosFragmentAdm() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_produtos_adm, container, false);

        //Inicializar Componentes
        txtNone = view.findViewById(R.id.txtNoneProduto);
        recyclerView = view.findViewById(R.id.recyclerProdutosOrcamentos);
        produtosOrcamentosRef = ConfiguracaoFirebase.getFirebaseDatabase().child("produtoOrcamento");
        usuariosRef = ConfiguracaoFirebase.getFirebaseDatabase().child("usuarios");

        //Configurar o RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        adapterOrcamentoProduto = new AdapterOrcamentoProduto(getActivity(), orcamentos);
        adapterUsuarios = new AdapterUsuarios(clientes, orcamentos, getActivity());
        recyclerView.setAdapter(adapterUsuarios);

        produtosOrcamentosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    recuperarOrcamentos(ds.getKey());
                    for(DataSnapshot dataSnapshot1 : ds.getChildren()){
                        ProdutoOrcamento produtoOrcamento = dataSnapshot1.getValue(ProdutoOrcamento.class);
                        orcamentos.add(produtoOrcamento);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Toque do Recycler
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(),
                recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        ProdutoOrcamento produtoOrcamento = orcamentos.get(position);
                        Usuario cliente = clientes.get(position);
                        Intent intent = new Intent(getActivity(), ProdutoOrcamentoActivity.class);
                        intent.putExtra("orcamentoSelecionado", produtoOrcamento);
                        intent.putExtra("clienteSelecionado", cliente);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, final int position) {

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
    }

    @Override
    public void onStop() {
        super.onStop();
        clientes.clear();
        usuariosRef.removeEventListener(valueEventListener);
    }

    private void recuperarOrcamentos(String idUsuario){
        valueEventListener = usuariosRef.child(idUsuario).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(Usuario.class) != null){
                    txtNone.setVisibility(View.GONE);
                    clientes.add(dataSnapshot.getValue(Usuario.class));
                    adapterUsuarios.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}