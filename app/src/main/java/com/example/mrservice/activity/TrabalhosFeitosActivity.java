package com.example.mrservice.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.mrservice.adapter.AdapterProdutos;
import com.example.mrservice.adapter.AdapterTrabalhosFeitos;
import com.example.mrservice.config.ConfiguracaoFirebase;
import com.example.mrservice.helper.RecyclerItemClickListener;
import com.example.mrservice.model.TrabalhosFeitos;
import com.example.mrservice.model.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.AdapterView;
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

public class TrabalhosFeitosActivity extends AppCompatActivity {

    private RecyclerView recyclerViewTrabalhosFeitos;
    private List<TrabalhosFeitos> listaTrabalhosFeitos = new ArrayList<>();
    private AdapterTrabalhosFeitos adapterTrabalhosFeitos;
    private DatabaseReference trabalhosFeitosRef;
    private TrabalhosFeitos trabalhosFeitosSelecionado;
    private AlertDialog dialog;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trabalhos_feitos);
        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Configuracoes Iniciais
        trabalhosFeitosRef = ConfiguracaoFirebase.getFirebaseDatabase().child("trabalhos feitos");
        recyclerViewTrabalhosFeitos = findViewById(R.id.recyclerViewTrabalhosFeitos);

        //Configurar o RecyclerView
        recyclerViewTrabalhosFeitos.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTrabalhosFeitos.setHasFixedSize(true);
        adapterTrabalhosFeitos = new AdapterTrabalhosFeitos(listaTrabalhosFeitos, this);
        recyclerViewTrabalhosFeitos.setAdapter(adapterTrabalhosFeitos);

        //Configurando o toque no recyclerView
        recyclerViewTrabalhosFeitos.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerViewTrabalhosFeitos,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                trabalhosFeitosSelecionado = listaTrabalhosFeitos.get(position);
                                Intent intent = new Intent(TrabalhosFeitosActivity.this, DetalhesTrabalhosFeitosActivity.class);
                                intent.putExtra("trabalhoFeitoSelecionado", trabalhosFeitosSelecionado);
                                startActivity(intent);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            }
                        }
                )
        );

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TrabalhosFeitosActivity.this, CadastrarTrabalhosFeitosActivity.class));
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

    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarTrabalhosFeitos();
    }

    private void exibirMensagem(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void recuperarTrabalhosFeitos(){
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Recuperando Produtos")
                .setCancelable(false)
                .build();
        dialog.show();

        trabalhosFeitosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaTrabalhosFeitos.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    listaTrabalhosFeitos.add(ds.getValue(TrabalhosFeitos.class));
                }
                Collections.reverse(listaTrabalhosFeitos);
                adapterTrabalhosFeitos.notifyDataSetChanged();
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

        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerViewTrabalhosFeitos);

    }

    public void excluirProduto(final RecyclerView.ViewHolder viewHolder){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(TrabalhosFeitosActivity.this);
        alertDialog.setTitle("Excluir");
        alertDialog.setMessage("Tem certeza que deseja excluir esse produto?");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int position = viewHolder.getAdapterPosition();
                trabalhosFeitosSelecionado = listaTrabalhosFeitos.get(position);
                trabalhosFeitosSelecionado.deletar();
                adapterTrabalhosFeitos.notifyItemRemoved(position);
                listaTrabalhosFeitos.clear();
                adapterTrabalhosFeitos.notifyDataSetChanged();
            }
        });
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                adapterTrabalhosFeitos.notifyDataSetChanged();
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.show();
    }

}