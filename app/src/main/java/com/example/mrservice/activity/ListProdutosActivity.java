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
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
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
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class ListProdutosActivity extends AppCompatActivity {
    private Button btnSelectLinha;
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
    private MaterialSearchView searchView;

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
        searchView = findViewById(R.id.materialSearchProdutos);
        btnSelectLinha = findViewById(R.id.btnSelectLinha);

        //Configurar o SearchView
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText != null && !newText.isEmpty()){
                    pesquisarProdutos(newText.toLowerCase());
                }
                return true;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                recarregarProdutos();
            }
        });

        //Checar se o usuario e adm
        if(usuario.getTipo_usuario().equals("ADM")){
            swipe();
        }

        toolbar.setTitle(filtroTipoProduto);

        //Configurar o RecyclerView
        recyclerViewProdutos.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProdutos.setHasFixedSize(true);
        adapterProdutos = new AdapterProdutos(listaProdutos, usuario.getTipo_usuario(),this);
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
                        intent.putExtra("cliente", usuario);
                        intent.putExtra("produtoSelecionado", produtoSelecionado);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, final int position) {
                        if(usuario.getTipo_usuario().equals("ADM")){
                            produtoSelecionado = listaProdutos.get(position);
                            Intent intent = new Intent(ListProdutosActivity.this, CadastrarProdutoActivity.class);
                            intent.putExtra("produtoSelecionado", produtoSelecionado);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search_i, menu);

        //Configurar botao pesquisa
        MenuItem item = menu.findItem(R.id.menu_pesquisa);
        searchView.setMenuItem(item);
        return super.onCreateOptionsMenu(menu);
    }

    private void exibirMensagem(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarProdutos();
    }

    private void recuperarPorLinha(String l){
        List<Produto> listProdutosLinha = new ArrayList<>();
        for(Produto produto: listaProdutos){
            String linha = produto.getLinha();
            if(linha.contains(l)){
                listProdutosLinha.add(produto);
            }
        }
        adapterProdutos = new AdapterProdutos(listProdutosLinha, usuario.getTipo_usuario(), getApplicationContext());
        recyclerViewProdutos.setAdapter(adapterProdutos);
        adapterProdutos.notifyDataSetChanged();
        swipe();
    }

    private void pesquisarProdutos(String text){
        List<Produto> listProdutosBusca = new ArrayList<>();
        for(Produto produto : listaProdutos){
            String titulo = produto.getTitulo().toLowerCase();
            if(titulo.contains(text)){
                listProdutosBusca.add(produto);
            }
        }
        adapterProdutos = new AdapterProdutos(listProdutosBusca, usuario.getTipo_usuario(),getApplicationContext());
        recyclerViewProdutos.setAdapter(adapterProdutos);
        adapterProdutos.notifyDataSetChanged();
        swipe();
    }

    private void recarregarProdutos(){
        adapterProdutos = new AdapterProdutos(listaProdutos, usuario.getTipo_usuario(),getApplicationContext());
        recyclerViewProdutos.setAdapter(adapterProdutos);
        adapterProdutos.notifyDataSetChanged();
        swipe();
    }

    public void selectLinha(View view){
        AlertDialog.Builder dialogCategoria = new AlertDialog.Builder(this);
        dialogCategoria.setTitle("Escolha sua linha:");

        //Configurar spinner
        View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);
        final Spinner spinner = viewSpinner.findViewById(R.id.spinnerFiltro);

        final String[] linha = getResources().getStringArray(R.array.linhas);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getApplicationContext(), android.R.layout.simple_spinner_item, linha
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        dialogCategoria.setView(viewSpinner);

        dialogCategoria.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String l = spinner.getSelectedItem().toString();
                if(l.equals("NENHUM")){
                    recuperarPorLinha("");
                }else{
                    recuperarPorLinha(l);
                }
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
