package com.example.mrservice.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.example.mrservice.R;
import com.example.mrservice.adapter.AdapterOrcamentoProduto;
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
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class ProdutoOrcamentoActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<ProdutoOrcamento> listaOrcamento = new ArrayList<>();
    private AdapterOrcamentoProduto adapterOrcamentoProduto;
    private DatabaseReference produtoOrcamentoRef;
    private ProdutoOrcamento produtoOrcamentoSelecionado;
    private AlertDialog dialog;
    private Usuario usuario;
    private MaterialSearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produto_orcamento);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        //Configuracoes Iniciais
        produtoOrcamentoRef = ConfiguracaoFirebase.getFirebaseDatabase().child("produtoOrcamento");
        recyclerView = findViewById(R.id.recyclerOrcamentoProduto);
        Bundle bundle = getIntent().getExtras();
        usuario = (Usuario) bundle.getSerializable("DadosUsuario");
        searchView = findViewById(R.id.materialSearchProdutos);

        //Configurar o RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapterOrcamentoProduto = new AdapterOrcamentoProduto(this, listaOrcamento, usuario.getTipo_usuario());
        recyclerView.setAdapter(adapterOrcamentoProduto);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(ProdutoOrcamentoActivity.this, DetalhesOrcamentoProdutoActivity.class);
                        intent.putExtra("produtoOrcamentoSelecionado", listaOrcamento.get(position));
                        intent.putExtra("tipoUsuario", usuario.getTipo_usuario());
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

    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperar();
    }

    private void recuperar(){
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Recuperando Produtos")
                .setCancelable(false)
                .build();
        dialog.show();
        if(usuario.getTipo_usuario().equals("ADM")){
            Query query = produtoOrcamentoRef.orderByChild("status");
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    listaOrcamento.clear();
                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                        for(DataSnapshot res: ds.getChildren()){
                            listaOrcamento.add(res.getValue(ProdutoOrcamento.class));
                        }
                    }
                    Collections.reverse(listaOrcamento);
                    adapterOrcamentoProduto.notifyDataSetChanged();
                    dialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            produtoOrcamentoRef
                    .child(usuario.getId())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            listaOrcamento.clear();
                            for(DataSnapshot ds: dataSnapshot.getChildren()){
                                listaOrcamento.add(ds.getValue(ProdutoOrcamento.class));
                                //ProdutoOrcamento produtoOrcamento = ds.getValue(ProdutoOrcamento.class);
                                //System.out.println(produtoOrcamento.getStatus());

                            }
                            adapterOrcamentoProduto.notifyDataSetChanged();
                            dialog.dismiss();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
