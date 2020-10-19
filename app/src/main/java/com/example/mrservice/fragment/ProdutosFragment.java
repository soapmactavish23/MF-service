package com.example.mrservice.fragment;

import android.app.AlertDialog;
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

import com.example.mrservice.R;
import com.example.mrservice.activity.CadastrarProdutoActivity;
import com.example.mrservice.activity.DetalhesOrcamentoProdutoActivity;
import com.example.mrservice.activity.DetalhesProdutoActivity;
import com.example.mrservice.activity.ListProdutosActivity;
import com.example.mrservice.activity.ProdutoOrcamentoActivity;
import com.example.mrservice.adapter.AdapterProdutos;
import com.example.mrservice.config.ConfiguracaoFirebase;
import com.example.mrservice.helper.RecyclerItemClickListener;
import com.example.mrservice.model.Produto;
import com.example.mrservice.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProdutosFragment extends Fragment {

    private RecyclerView recyclerViewProdutos;
    private List<Produto> listaProdutos = new ArrayList<>();
    private AdapterProdutos adapterProdutos;
    private DatabaseReference produtosRef;
    private Produto produtoSelecionado;
    private MaterialSearchView searchView;
    private AlertDialog dialog;
    private Usuario usuario;
    private ValueEventListener valueEventListenerProdutos;

    public ProdutosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_produtos, container, false);

        Bundle bundle = getActivity().getIntent().getExtras();
        usuario = (Usuario) bundle.getSerializable("DadosUsuario");

        produtosRef = ConfiguracaoFirebase.getFirebaseDatabase().child("produtos");
        recyclerViewProdutos = view.findViewById(R.id.recyclerViewProdutos);

        //Configurar o RecyclerView
        recyclerViewProdutos.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewProdutos.setHasFixedSize(true);
        adapterProdutos = new AdapterProdutos(listaProdutos, getActivity());
        recyclerViewProdutos.setAdapter(adapterProdutos);

        recyclerViewProdutos.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(),
                recyclerViewProdutos,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        produtoSelecionado = listaProdutos.get(position);
                        Intent intent = new Intent(getActivity(), DetalhesProdutoActivity.class);
                        intent.putExtra("produtoSelecionado", produtoSelecionado);
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
        recuperarProdutos();
    }

    @Override
    public void onStop() {
        super.onStop();
        produtosRef.removeEventListener(valueEventListenerProdutos);
        listaProdutos.clear();
    }

    public void pesquisarProduto(String text){
        List<Produto> listaProdutoPesquisa = new ArrayList<>();
        for(Produto produto : listaProdutos){
            String titulo = produto.getTitulo().toLowerCase();
            String categoria = produto.getCategoria().toLowerCase();
            String tipo_produto = produto.getProduto().toLowerCase();
            if(titulo.contains(text)){
                listaProdutoPesquisa.add(produto);
            }
        }
        adapterProdutos = new AdapterProdutos(listaProdutoPesquisa, getActivity());
        recyclerViewProdutos.setAdapter(adapterProdutos);
        adapterProdutos.notifyDataSetChanged();
    }

    public void recarregarProdutos(){
        adapterProdutos = new AdapterProdutos(listaProdutos, getActivity());
        recyclerViewProdutos.setAdapter(adapterProdutos);
        adapterProdutos.notifyDataSetChanged();
    }

    public void recuperarProdutos(){
        dialog = new SpotsDialog.Builder()
                .setContext(getActivity())
                .setMessage("Recuperando Produtos")
                .setCancelable(false)
                .build();
        dialog.show();

        Query produtoPesquisa = produtosRef.orderByChild("titulo");

        valueEventListenerProdutos = produtoPesquisa.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaProdutos.clear();
                for(DataSnapshot categoria : dataSnapshot.getChildren()){
                    for (DataSnapshot tipoProduto : categoria.getChildren()){
                        for(DataSnapshot ds : tipoProduto.getChildren()){
                            listaProdutos.add(ds.getValue(Produto.class));
                            System.out.println(ds.getValue(Produto.class).getTitulo());
                        }
                    }
                }
                adapterProdutos.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
