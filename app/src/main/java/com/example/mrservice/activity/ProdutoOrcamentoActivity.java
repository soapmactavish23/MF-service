package com.example.mrservice.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mrservice.R;
import com.example.mrservice.adapter.AdapterItem;
import com.example.mrservice.adapter.AdapterOrcamentoProduto;
import com.example.mrservice.config.ConfiguracaoFirebase;
import com.example.mrservice.config.UsuarioFirebase;
import com.example.mrservice.helper.RecyclerItemClickListener;
import com.example.mrservice.model.ProdutoOrcamento;
import com.example.mrservice.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class ProdutoOrcamentoActivity extends AppCompatActivity {

    private Usuario clienteSelecionado;
    private RecyclerView recyclerView;
    private AdapterItem adapterItem;
    private List<ProdutoOrcamento> listProdutoOrcamentos = new ArrayList<>();
    private DatabaseReference produtosOrcamentosRef;
    private ValueEventListener valueEventListener;
    private CircleImageView foto;
    private TextView txtNome, txtPrecoTotal, txtTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produto_orcamento);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        //Inicializar Componentes
        foto = findViewById(R.id.imgFotoCliente);
        txtNome = findViewById(R.id.txtNomeCliente);
        txtTotal = findViewById(R.id.txtPrecoTotal);
        txtPrecoTotal = findViewById(R.id.txtPrecoTotal);
        recyclerView = findViewById(R.id.recyclerProdutosOrcamentos);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            clienteSelecionado = (Usuario) bundle.getSerializable("clienteSelecionado");
            Glide.with(getApplicationContext()).load(clienteSelecionado.getFoto()).into(foto);
            txtNome.setText(clienteSelecionado.getNome());

            produtosOrcamentosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                    .child("produtoOrcamento")
                    .child(clienteSelecionado.getId());
        }

        //Configurar O RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapterItem = new AdapterItem(this, listProdutoOrcamentos);
        recyclerView.setAdapter(adapterItem);

    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarOrcamentos();
    }

    @Override
    protected void onStop() {
        super.onStop();
        produtosOrcamentosRef.removeEventListener(valueEventListener);
        listProdutoOrcamentos.clear();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
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
                        totalStr = Integer.toString(total);
                    }
                }

                StringBuilder stringBuilder = new StringBuilder(Integer.toString(total));
                stringBuilder.insert(totalStr.length() - 2, ",");
                txtPrecoTotal.setText("R$ " + stringBuilder);
                adapterItem.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
