package com.example.mrservice.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.mrservice.adapter.AdapterProdutos;
import com.example.mrservice.config.ConfiguracaoFirebase;
import com.example.mrservice.helper.RecyclerItemClickListener;
import com.example.mrservice.model.Produto;
import com.example.mrservice.model.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mrservice.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class ProdutosActivity extends AppCompatActivity {

    private RecyclerView recyclerViewProdutos;
    private List<Produto> listaProdutos = new ArrayList<>();
    private AdapterProdutos adapterProdutos;
    private DatabaseReference produtosRef;
    private DatabaseReference produtosCategoriaRef;
    private Produto produtoSelecionado;
    private AlertDialog dialog;
    private String filtroCategoria;
    private String filtroTipoProduto;
    private Button btnTipoProduto;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produtos);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Configuracoes iniciais
        produtosRef = ConfiguracaoFirebase.getFirebaseDatabase().child("produtos");
        recyclerViewProdutos = findViewById(R.id.recyclerViewProdutos);
        btnTipoProduto = findViewById(R.id.btnTipoProduto);

        //Configurar o RecyclerView
        recyclerViewProdutos.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProdutos.setHasFixedSize(true);
        adapterProdutos = new AdapterProdutos(listaProdutos, this);
        recyclerViewProdutos.setAdapter(adapterProdutos);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProdutosActivity.this, CadastrarProdutoActivity.class));
            }
        });

        //Configurar o toque
        recyclerViewProdutos.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                recyclerViewProdutos,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        produtoSelecionado = listaProdutos.get(position);
                        Intent intent = new Intent(ProdutosActivity.this, DetalhesProdutoActivity.class);
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

        //Checar se o usuario e adm
        Bundle bundle = getIntent().getExtras();
        usuario = (Usuario) bundle.getSerializable("DadosUsuario");
        if(usuario.getTipo_usuario().equals("ADM")){
            swipe();
        }else{
            fab.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_search_i, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuInteratividade:
                startActivity(new Intent(this, InfoProdutosActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
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
        //dialog.show();

        produtosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaProdutos.clear();
                for(DataSnapshot categorias: dataSnapshot.getChildren()){
                    for(DataSnapshot tipos_produtos : categorias.getChildren()){
                        for(DataSnapshot ds : tipos_produtos.getChildren()){
                            listaProdutos.add(ds.getValue(Produto.class));
                        }
                        Collections.reverse(listaProdutos);
                        adapterProdutos.notifyDataSetChanged();
                        //dialog.dismiss();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dialog.dismiss();
            }
        });
    }

    public void recuperarPorCategoria(View view){
        AlertDialog.Builder dialogCategoria = new AlertDialog.Builder(this);
        dialogCategoria.setTitle("Selecione a categoria desejada");

        //Configurar spinner
        View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);
        final Spinner spinner = viewSpinner.findViewById(R.id.spinnerFiltro);
        //Carregar Spinner de categoria
        String[] categoria = getResources().getStringArray(R.array.categorias);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, categoria
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        dialogCategoria.setView(viewSpinner);

        dialogCategoria.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                filtroCategoria = spinner.getSelectedItem().toString();
                recuperarPorCategoriaFirebase();
            }
        });
        dialogCategoria.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = dialogCategoria.create();
        dialog.show();
    }

    private void recuperarPorCategoriaFirebase(){
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Recuperando Produtos")
                .setCancelable(false)
                .build();
        dialog.show();
        //Configura nó por categoria
        produtosCategoriaRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("produtos").child(filtroCategoria);
        produtosCategoriaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaProdutos.clear();
                for(DataSnapshot tipo_produto : dataSnapshot.getChildren()){
                    for(DataSnapshot ds : tipo_produto.getChildren()){
                        listaProdutos.add(ds.getValue(Produto.class));
                    }
                }
                Collections.reverse(listaProdutos);
                adapterProdutos.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void recuperarPorTipoProduto(View view){
        try {
            if(!filtroCategoria.isEmpty()) {
                AlertDialog.Builder dialogCategoria = new AlertDialog.Builder(this);
                dialogCategoria.setTitle("Selecione o tipo de produto desejado");

                //Configurar spinner
                View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);
                final Spinner spinner = viewSpinner.findViewById(R.id.spinnerFiltro);
                //Carregar Spinner de categoria
                String[] tipoProduto = getResources().getStringArray(R.array.equipamentos);
                switch (filtroCategoria) {
                    case "EQUIPAMENTOS":
                        tipoProduto = getResources().getStringArray(R.array.equipamentos);
                        break;
                    case "CÁRDIOS":
                        tipoProduto = getResources().getStringArray(R.array.cardio);
                        break;
                    case "ACESSÓRIOS":
                        tipoProduto = getResources().getStringArray(R.array.acessorios);
                        break;
                    case "PISOS":
                        tipoProduto = getResources().getStringArray(R.array.piso);
                        break;
                    case "REVESTIMENTOS":
                        tipoProduto = getResources().getStringArray(R.array.revestimento);
                        break;
                    case "BRINQUEDOS":
                        tipoProduto = getResources().getStringArray(R.array.brinquedo);
                        break;
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        this, android.R.layout.simple_spinner_item, tipoProduto
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

                dialogCategoria.setView(viewSpinner);

                dialogCategoria.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        filtroTipoProduto = spinner.getSelectedItem().toString();
                        recuperarPorTipoProdutoFirebase();
                    }
                });
                dialogCategoria.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog dialog = dialogCategoria.create();
                dialog.show();
            }
        }catch (Exception e){
            exibirMensagem("Selecione uma Categoria");
        }
    }

    public void recuperarPorTipoProdutoFirebase(){
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Recuperando Produtos")
                .setCancelable(false)
                .build();
        dialog.show();
        //Configura nó por categoria
        produtosCategoriaRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("produtos").child(filtroCategoria).child(filtroTipoProduto);
        produtosCategoriaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaProdutos.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    listaProdutos.add(ds.getValue(Produto.class));
                }
                Collections.reverse(listaProdutos);
                adapterProdutos.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProdutosActivity.this);
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


}
