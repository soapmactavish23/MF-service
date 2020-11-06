package com.example.mfservice.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.bumptech.glide.Glide;
import com.example.mfservice.R;
import com.example.mfservice.adapter.AdapterItem;
import com.example.mfservice.adapter.AdapterOrcamentoProduto;
import com.example.mfservice.config.ConfiguracaoFirebase;
import com.example.mfservice.config.UsuarioFirebase;
import com.example.mfservice.helper.RecyclerItemClickListener;
import com.example.mfservice.model.Produto;
import com.example.mfservice.model.ProdutoOrcamento;
import com.example.mfservice.model.Usuario;
import com.google.android.material.textfield.TextInputEditText;
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

    private RecyclerView recyclerView;
    private AdapterItem adapterItem;
    private List<ProdutoOrcamento> listProdutoOrcamentos = new ArrayList<>();
    private DatabaseReference produtosOrcamentosRef;
    private DatabaseReference usuarioRef;
    private Usuario cliente;
    private ValueEventListener valueEventListener;
    private CircleImageView foto;
    private TextView txtNome, txtNoneProduto, txtTotal, txtEndereco, txtEmail, txtTelefone;
    private CurrencyEditText txtPrecoTotal;
    private ProdutoOrcamento produtoOrcamentoSelecionado;

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
        txtPrecoTotal = findViewById(R.id.txtPrecoTotal);
        txtNoneProduto = findViewById(R.id.txtNoneProduto);
        txtEndereco = findViewById(R.id.txtEndereco);
        txtEmail = findViewById(R.id.txtEmail);
        txtTelefone = findViewById(R.id.txtTelefone);
        txtTotal = findViewById(R.id.txtTotal);
        recyclerView = findViewById(R.id.recyclerProdutosOrcamentos);

        //Recuperar o usuario
        Bundle bundle = getIntent().getExtras();
        cliente = (Usuario) bundle.getSerializable("clienteSelecionado");

        produtosOrcamentosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("produtoOrcamento")
                .child(cliente.getId());

        Glide.with(this).load(cliente.getFoto()).into(foto);
        txtNome.setText(cliente.getNome());
        txtEmail.setText("E-mail: " + cliente.getEmail());
        txtEndereco.setText("Endereço: " + cliente.getEndereco());
        txtTelefone.setText("Contato: " + cliente.getContato());

        //Configurar O RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapterItem = new AdapterItem(this, listProdutoOrcamentos);
        recyclerView.setAdapter(adapterItem);

        //Toque no Recycler
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        produtoOrcamentoSelecionado = listProdutoOrcamentos.get(position);
                        editarValorProduto();
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        produtoOrcamentoSelecionado = listProdutoOrcamentos.get(position);
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
        recuperarOrcamentos();
    }

    @Override
    protected void onStop() {
        super.onStop();
        listProdutoOrcamentos.clear();
        produtosOrcamentosRef.removeEventListener(valueEventListener);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    public void recuperarOrcamentos(){
        valueEventListener = produtosOrcamentosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listProdutoOrcamentos.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    listProdutoOrcamentos.add(ds.getValue(ProdutoOrcamento.class));
                }
                adapterItem.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void editarValorProduto(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Editar o valor do produto");
        alertDialog.setMessage(
                "Valor Atual: " + produtoOrcamentoSelecionado.getProduto().getPrecoVenda()
                + "\nQuantidade: " + produtoOrcamentoSelecionado.getQtd()
                + "\nDigite o Valor unitário do produto"
        );
        alertDialog.setCancelable(true);

        View viewQtd = getLayoutInflater().inflate(R.layout.dialog_preco, null);
        final CurrencyEditText editPreco = viewQtd.findViewById(R.id.editPreco);
        editPreco.setText(produtoOrcamentoSelecionado.getProduto().getPrecoVenda());

        alertDialog.setView(viewQtd);

        alertDialog.setPositiveButton("Finalizar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, final int i) {
                String preco = editPreco.getText().toString();
                produtoOrcamentoSelecionado.getProduto().setPrecoVenda(preco);
                produtoOrcamentoSelecionado.setStatus("FINALIZADO");
                produtoOrcamentoSelecionado.salvar();
                adapterItem.notifyDataSetChanged();
            }
        });
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alert = alertDialog.create();
        alert.show();
    }

}
