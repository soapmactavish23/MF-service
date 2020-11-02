package com.example.mrservice.fragment;

import android.app.AlertDialog;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mrservice.R;
import com.example.mrservice.activity.CadastrarProdutoActivity;
import com.example.mrservice.activity.DetalhesOrcamentoProdutoActivity;
import com.example.mrservice.activity.DetalhesProdutoActivity;
import com.example.mrservice.activity.ListProdutosActivity;
import com.example.mrservice.activity.ProdutoOrcamentoActivity;
import com.example.mrservice.adapter.AdapterItem;
import com.example.mrservice.adapter.AdapterProdutos;
import com.example.mrservice.config.ConfiguracaoFirebase;
import com.example.mrservice.config.UsuarioFirebase;
import com.example.mrservice.helper.RecyclerItemClickListener;
import com.example.mrservice.model.Produto;
import com.example.mrservice.model.ProdutoOrcamento;
import com.example.mrservice.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProdutosFragment extends Fragment {

    private RecyclerView recyclerView;
    private AdapterItem adapterItem;
    private List<ProdutoOrcamento> listProdutoOrcamentos = new ArrayList<>();
    private DatabaseReference produtosOrcamentosRef;
    private ValueEventListener valueEventListener;
    private CircleImageView foto;
    private TextView txtNome, txtNoneProduto, txtPrecoTotal, txtTotal;

    public ProdutosFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_produtos, container, false);

        //Inicializar Componentes
        foto = view.findViewById(R.id.imgFotoCliente);
        txtNome = view.findViewById(R.id.txtNomeCliente);
        txtPrecoTotal = view.findViewById(R.id.txtPrecoTotal);
        txtNoneProduto = view.findViewById(R.id.txtNoneProduto);
        txtTotal = view.findViewById(R.id.txtTotal);
        recyclerView = view.findViewById(R.id.recyclerProdutosOrcamentos);

        produtosOrcamentosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("produtoOrcamento")
                .child(UsuarioFirebase.getIdentificadorUsuario());

        Glide.with(getActivity()).load(UsuarioFirebase.getUsuarioLogado().getFoto()).into(foto);
        txtNome.setText(UsuarioFirebase.getUsuarioLogado().getNome());

        //Configurar O RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        adapterItem = new AdapterItem(getActivity(), listProdutoOrcamentos);
        recyclerView.setAdapter(adapterItem);

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
        produtosOrcamentosRef.removeEventListener(valueEventListener);
        listProdutoOrcamentos.clear();
    }

    public void recuperarOrcamentos(){
        valueEventListener = produtosOrcamentosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listProdutoOrcamentos.clear();
                int total = 0;
                String totalStr = "000";
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    ProdutoOrcamento produtoOrcamento = ds.getValue(ProdutoOrcamento.class);

                    String valor1 = produtoOrcamento.getProduto().getPrecoVenda().replaceAll("[^0-9]", "");
                    int valor = Integer.parseInt(valor1) * Integer.parseInt(produtoOrcamento.getQtd());

                    total += valor;

                    listProdutoOrcamentos.add(produtoOrcamento);

                    if(produtoOrcamento != null){
                        txtPrecoTotal.setVisibility(View.VISIBLE);
                        txtTotal.setVisibility(View.VISIBLE);
                        txtNoneProduto.setVisibility(View.GONE);
                        totalStr = Integer.toString(total);
                    }
                }

                StringBuilder stringBuilder = new StringBuilder(Integer.toString(total));
                stringBuilder.insert(totalStr.length() - 2, ",");
                if(listProdutoOrcamentos.get(0).getStatus().equals("PENDENTE")){
                    txtPrecoTotal.setText("R$ 0,00");
                }else{
                    txtPrecoTotal.setText("R$ " + stringBuilder);
                }
                adapterItem.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
