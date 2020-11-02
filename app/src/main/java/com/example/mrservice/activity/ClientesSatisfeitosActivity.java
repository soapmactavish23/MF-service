package com.example.mrservice.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.mrservice.adapter.AdapterUsuarios;
import com.example.mrservice.config.ConfiguracaoFirebase;
import com.example.mrservice.helper.RecyclerItemClickListener;
import com.example.mrservice.model.ClientesSatisfeitos;
import com.example.mrservice.model.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.AdapterView;

import com.example.mrservice.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class ClientesSatisfeitosActivity extends AppCompatActivity {

    private RecyclerView recyclerViewClientes;
    private List<ClientesSatisfeitos>  clientesSatisfeitosList = new ArrayList<>();
    private DatabaseReference clientesSatisfeitosRef;
    private ClientesSatisfeitos clientesSatisfeitos;
    private AdapterUsuarios adapterUsuarios;
    private AlertDialog dialog;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientes_satisfeitos);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Configuracoes Iniciais
        clientesSatisfeitosRef = ConfiguracaoFirebase.getFirebaseDatabase().child("clientes satisfeitos");

        //Configurar o RecyclerView
        recyclerViewClientes = findViewById(R.id.recyclerViewClientesSatisfeitos);
        recyclerViewClientes.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewClientes.setHasFixedSize(true);
        //adapterUsuarios = new AdapterUsuarios(clientesSatisfeitosList, this);
        recyclerViewClientes.setAdapter(adapterUsuarios);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ClientesSatisfeitosActivity.this, CadastrarClientesSatisfeitosActivity.class));
            }
        });

        //Checar se o usuario e adm
        Bundle bundle = getIntent().getExtras();
        usuario = (Usuario) bundle.getSerializable("DadosUsuario");
        if(usuario.getTipo_usuario().equals("ADM")){
            swipe();
        }else{
            fab.setVisibility(View.GONE);
        }

        recyclerViewClientes.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                recyclerViewClientes,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        clientesSatisfeitos = clientesSatisfeitosList.get(position);
                        Intent intent = new Intent(ClientesSatisfeitosActivity.this, DetalhesClienteActivity.class);
                        intent.putExtra("clienteSatisfeito", clientesSatisfeitos);
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
        recuperarClientesSatisfeitos();
    }

    public void recuperarClientesSatisfeitos(){
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Recuperando Clientes")
                .setCancelable(false)
                .build();
        dialog.show();

        clientesSatisfeitosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                clientesSatisfeitosList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    clientesSatisfeitosList.add(ds.getValue(ClientesSatisfeitos.class));
                }
                Collections.reverse(clientesSatisfeitosList);
                adapterUsuarios.notifyDataSetChanged();
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

        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerViewClientes);

    }

    public void excluirProduto(final RecyclerView.ViewHolder viewHolder){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ClientesSatisfeitosActivity.this);
        alertDialog.setTitle("Excluir");
        alertDialog.setMessage("Tem certeza que deseja excluir esse produto?");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int position = viewHolder.getAdapterPosition();
                clientesSatisfeitos = clientesSatisfeitosList.get(position);
                clientesSatisfeitos.deletar();
                adapterUsuarios.notifyItemRemoved(position);
                clientesSatisfeitosList.clear();
                adapterUsuarios.notifyDataSetChanged();
            }
        });
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                adapterUsuarios.notifyDataSetChanged();
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.show();
    }

}
