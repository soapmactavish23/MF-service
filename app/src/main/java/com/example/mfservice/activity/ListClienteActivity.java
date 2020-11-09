package com.example.mfservice.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
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
import android.widget.GridView;

import com.example.mfservice.R;
import com.example.mfservice.adapter.AdapterClientes;
import com.example.mfservice.adapter.AdapterGrid;
import com.example.mfservice.adapter.AdapterProdutos;
import com.example.mfservice.config.ConfiguracaoFirebase;
import com.example.mfservice.helper.RecyclerItemClickListener;
import com.example.mfservice.model.Cliente;
import com.example.mfservice.model.Produto;
import com.example.mfservice.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class ListClienteActivity extends AppCompatActivity {

    private Usuario usuario;
    private String categoria;
    private List<Cliente> listaClientes = new ArrayList<>();
    private DatabaseReference clientesCategoriaRef;
    private Cliente clienteSelecionado;
    private AdapterGrid adapterGrid;
    private AdapterClientes adapterClientes;
    private AlertDialog dialog;
    //private GridView gridViewClientes;
    private MaterialSearchView searchView;
    private RecyclerView recyclerViewClientes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_cliente);

        //Recuperando bundle
        Bundle bundle = getIntent().getExtras();
        usuario = (Usuario) bundle.getSerializable("DadosUsuario");
        categoria = bundle.getString("categoria");

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        toolbar.setTitle(categoria);

        //Configuracoes Iniciais
        clientesCategoriaRef = ConfiguracaoFirebase.getFirebaseDatabase().child("clientes").child(categoria);
        recyclerViewClientes = findViewById(R.id.recyclerClientes);
        searchView = findViewById(R.id.materialSearchClientes);
        inicializarImageLoader();

        //Configurando o recyclerView
        recyclerViewClientes.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewClientes.setHasFixedSize(true);
        adapterClientes = new AdapterClientes(listaClientes, this);
        recyclerViewClientes.setAdapter(adapterClientes);

        //Configurar o toque
        recyclerViewClientes.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                recyclerViewClientes,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Cliente cliente = adapterClientes.getListaClientes().get(position);
                        Intent intent = new Intent(ListClienteActivity.this, DetalhesClienteActivity.class);
                        intent.putExtra("cliente", cliente);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        if(usuario.getTipo_usuario().equals("ADM")){
                            Cliente cliente = adapterClientes.getListaClientes().get(position);
                            Intent intent = new Intent(ListClienteActivity.this, CadastrarClientesActivity.class);
                            intent.putExtra("cliente", cliente);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }));

                //Configurar o SearchView
                searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        if (newText != null && !newText.isEmpty()) {
                            pesquisarClientes(newText.toLowerCase());
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
                recarregarClientes();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarClientes();
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

    private void pesquisarClientes(String text){
        List<Cliente> listClienteBusca = new ArrayList<>();
        for(Cliente cliente : listaClientes){
            String titulo = cliente.getNome().toLowerCase();
            if(titulo.contains(text)){
                listClienteBusca.add(cliente);
            }
        }
        adapterClientes = new AdapterClientes(listClienteBusca, getApplicationContext());
        recyclerViewClientes.setAdapter(adapterClientes);
        adapterClientes.notifyDataSetChanged();
    }

    private void recarregarClientes(){
        adapterClientes = new AdapterClientes(listaClientes, getApplicationContext());
        recyclerViewClientes.setAdapter(adapterClientes);
        adapterClientes.notifyDataSetChanged();
    }

    private void inicializarImageLoader(){
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(this)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .build();
        ImageLoader.getInstance().init(config);
    }

    public void recuperarClientes(){
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Recuperando Clientes")
                .setCancelable(false)
                .build();
        dialog.show();
        Query query = clientesCategoriaRef.orderByChild("nome");
        //clientesCategoriaRef = ConfiguracaoFirebase.getFirebaseDatabase().child("clientes").child("clientes");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try{
                    //Configurando o Grid
                    listaClientes.clear();
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        //Cliente cliente = ds.getValue(Cliente.class);
                        //cliente.salvar();
                        listaClientes.add(ds.getValue(Cliente.class));

                    }
                    adapterClientes.notifyDataSetChanged();
                    dialog.dismiss();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
