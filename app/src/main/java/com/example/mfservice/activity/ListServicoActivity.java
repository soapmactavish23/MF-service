package com.example.mfservice.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.example.mfservice.R;
import com.example.mfservice.adapter.AdapterServicos;
import com.example.mfservice.config.ConfiguracaoFirebase;
import com.example.mfservice.helper.RecyclerItemClickListener;
import com.example.mfservice.model.ItemServico;
import com.example.mfservice.model.ServicoOrcamento;
import com.example.mfservice.model.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class ListServicoActivity extends AppCompatActivity {

    private String categoria;
    private AdapterServicos adapterServicos;
    private List<ItemServico> itemServicos = new ArrayList<>();
    private RecyclerView recyclerView;
    private AlertDialog dialog;
    private DatabaseReference servicosRef;
    private Usuario usuario;
    private ServicoOrcamento servicoOrcamentoSelecionado;
    private FloatingActionButton fabAddServico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_servico);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        //Checar se o usuario e adm
        Bundle bundle = getIntent().getExtras();
        categoria = bundle.getString("categoria");
        toolbar.setTitle(categoria);
        usuario = (Usuario) bundle.getSerializable("usuario");
        if(usuario.getTipo_usuario().equals("ADM")){
            swipe();
        }

        //Configurando o RecyclerView
        recyclerView = findViewById(R.id.recyclerViewServicos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapterServicos = new AdapterServicos(this, itemServicos);
        recyclerView.setAdapter(adapterServicos);
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarServicos();
    }

    private void recuperarServicos(){
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Recuperando Serviços")
                .setCancelable(false)
                .build();
        dialog.show();

        servicosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("servicos").child(categoria);

        Query query = servicosRef.orderByChild("titulo");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemServicos.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    //itemServicos.add(ds.getValue(ServicoOrcamento.class));
                }
                adapterServicos.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        excluirServico(position);
                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));

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
                //excluirServico(viewHolder);
            }
        };

        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerView);

    }

    private void excluirServico(final int position){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ListServicoActivity.this);
        alertDialog.setTitle("Excluir");
        alertDialog.setMessage("Tem certeza que deseja excluir esse serviço?");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //servicoOrcamentoSelecionado = listaServicoOrcamentos.get(position);
                servicoOrcamentoSelecionado.deletar();
                adapterServicos.notifyItemRemoved(position);
                //listaServicoOrcamentos.clear();
                adapterServicos.notifyDataSetChanged();
            }
        });
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                adapterServicos.notifyDataSetChanged();
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.show();

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
