package com.example.mrservice.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.mrservice.R;
import com.example.mrservice.adapter.AdapterGrid;
import com.example.mrservice.config.ConfiguracaoFirebase;
import com.example.mrservice.model.Cliente;
import com.example.mrservice.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
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
    private AlertDialog dialog;
    private GridView gridViewClientes;

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
        gridViewClientes = findViewById(R.id.gridViewClientes);
        inicializarImageLoader();

        if(usuario.getTipo_usuario().equals("ADM")){
            gridViewClientes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    excluirProduto(i);
                    return false;
                }
            });
        }

        gridViewClientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cliente cliente = listaClientes.get(i);
                Intent intent = new Intent(ListClienteActivity.this, DetalhesClienteActivity.class);
                intent.putExtra("cliente", cliente);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarClientes();
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

        clientesCategoriaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Configurando o Grid
                int tamanhoGrid = getResources().getDisplayMetrics().widthPixels;
                int tamanhoImagem = tamanhoGrid/3;
                gridViewClientes.setColumnWidth(tamanhoImagem);

                listaClientes.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    listaClientes.add(ds.getValue(Cliente.class));
                }
                //Adapter Grid
                adapterGrid = new AdapterGrid(getApplicationContext(), R.layout.grid_foto_titulo, listaClientes);
                gridViewClientes.setAdapter(adapterGrid);

                dialog.dismiss();

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

    public void excluirProduto(final int position){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ListClienteActivity.this);
        alertDialog.setTitle("Excluir");
        alertDialog.setMessage("Tem certeza que deseja excluir esse cliente?");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                clienteSelecionado = listaClientes.get(position);
                clienteSelecionado.deletar();
                listaClientes.clear();
                gridViewClientes.deferNotifyDataSetChanged();
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
