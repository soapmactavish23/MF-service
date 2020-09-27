package com.example.mrservice.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Path;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mrservice.R;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class ListProdutosActivity extends AppCompatActivity {

    private RecyclerView recyclerViewProdutos;
    private List<Produto> listaProdutos = new ArrayList<>();
    private AdapterProdutos adapterProdutos;
    private DatabaseReference produtosRef;
    private DatabaseReference produtosCategoriaRef;
    private Produto produtoSelecionado;
    private AlertDialog dialog;
    private String filtroCategoria;
    private String filtroTipoProduto;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_produtos);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        //Configuracoes iniciais
        produtosRef = ConfiguracaoFirebase.getFirebaseDatabase().child("produtos");
        recyclerViewProdutos = findViewById(R.id.recyclerViewProdutos);
        Bundle bundle = getIntent().getExtras();
        usuario = (Usuario) bundle.getSerializable("DadosUsuario");
        filtroCategoria = bundle.getString("categoria");
        filtroTipoProduto = bundle.getString("tipo_produto");

        //Checar se o usuario e adm
        if(usuario.getTipo_usuario().equals("ADM")){
            swipe();
        }

        toolbar.setTitle(filtroTipoProduto);

        //Configurar o RecyclerView
        recyclerViewProdutos.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProdutos.setHasFixedSize(true);
        adapterProdutos = new AdapterProdutos(listaProdutos, this);
        recyclerViewProdutos.setAdapter(adapterProdutos);

        //Configurar o toque
        recyclerViewProdutos.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                recyclerViewProdutos,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        produtoSelecionado = listaProdutos.get(position);
                        Intent intent = new Intent(ListProdutosActivity.this, DetalhesProdutoActivity.class);
                        intent.putExtra("produtoSelecionado", produtoSelecionado);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, final int position) {
                        /*produtoSelecionado = listaProdutos.get(position);
                        Intent intent = new Intent(ProdutosActivity.this, CadastrarProdutoActivity.class);
                        intent.putExtra("produtoSelecionado", produtoSelecionado);
                        startActivity(intent);*/
                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));

    }

    private void exibirMensagem(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarProdutos();
    }

    public void recuperarProdutos(){
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Recuperando Produtos")
                .setCancelable(false)
                .build();
        dialog.show();
        //Configura nó por categoria
        produtosCategoriaRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("produtos").child(filtroCategoria).child(filtroTipoProduto);

        Query produtoPesquisa = produtosCategoriaRef.orderByChild("titulo");

        produtoPesquisa.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaProdutos.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    listaProdutos.add(ds.getValue(Produto.class));
                }
                adapterProdutos.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Exclusao
    public void swipe(){
        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                excluirProduto(viewHolder);
            }
        };

        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerViewProdutos);

    }

    public void excluirProduto(final RecyclerView.ViewHolder viewHolder){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ListProdutosActivity.this);
        alertDialog.setTitle("Excluir");
        alertDialog.setMessage("Tem certeza que deseja excluir esse produto?");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int position = viewHolder.getAdapterPosition();
                produtoSelecionado = listaProdutos.get(position);
                produtoSelecionado.deletar();
                adapterProdutos.notifyItemRemoved(position);
                listaProdutos.clear();
                adapterProdutos.notifyDataSetChanged();
            }
        });
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                adapterProdutos.notifyDataSetChanged();
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
