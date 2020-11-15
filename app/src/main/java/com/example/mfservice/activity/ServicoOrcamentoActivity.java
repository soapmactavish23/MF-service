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
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mfservice.R;
import com.example.mfservice.adapter.AdapterServicos;
import com.example.mfservice.config.ConfiguracaoFirebase;
import com.example.mfservice.config.UsuarioFirebase;
import com.example.mfservice.helper.RecyclerItemClickListener;
import com.example.mfservice.model.ItemServico;
import com.example.mfservice.model.ServicoOrcamento;
import com.example.mfservice.model.Usuario;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class ServicoOrcamentoActivity extends AppCompatActivity {

    private Usuario cliente;
    private CircleImageView foto;
    private TextView txtNome, txtEndereco, txtEmail, txtContato;
    private ServicoOrcamento servicoOrcamentoSelecionado;
    private List<ItemServico> items = new ArrayList<>();
    private AdapterServicos adapterServicos;
    private RecyclerView recyclerServicos;
    private DatabaseReference firebaseRef, itensServicoRef, servicoOrcamentoRef;
    private ValueEventListener valueEventListener;
    private ItemServico itemSelecionado;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servico_orcamento);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        //Inicializar Componentes
        foto = findViewById(R.id.imgFotoCliente);
        txtNome = findViewById(R.id.txtNome);
        txtEndereco = findViewById(R.id.txtEndereco);
        txtEmail = findViewById(R.id.txtEmail);
        txtContato = findViewById(R.id.txtContato);
        recyclerServicos = findViewById(R.id.recyclerServicos);

        //Recuperando Cliente
        Bundle bundle = getIntent().getExtras();
        cliente = (Usuario) bundle.getSerializable("cliente");
        servicoOrcamentoSelecionado = (ServicoOrcamento) bundle.getSerializable("servicoOrcamentoSelecionado");
        toolbar.setTitle(servicoOrcamentoSelecionado.getStatus());

        //Firebase
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        itensServicoRef = firebaseRef.child("itensServico").child(cliente.getId());
        servicoOrcamentoRef = firebaseRef.child("servicoOrcamento").child(cliente.getId());

        //Inicializar dados do cliente
        try{
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.padrao);
            Glide.with(this).applyDefaultRequestOptions(requestOptions).load(cliente.getFoto()).into(foto);
            txtNome.setText(cliente.getNome());
            txtEndereco.setText("Endereço: "+ cliente.getEndereco());
            txtEmail.setText("E-mail: " + cliente.getEmail());
            txtContato.setText("Contato: "+ cliente.getContato());
        }catch (Exception e){
            e.printStackTrace();
        }

        recyclerServicos.setLayoutManager(new LinearLayoutManager(this));
        recyclerServicos.setHasFixedSize(true);
        adapterServicos = new AdapterServicos(this, items);
        recyclerServicos.setAdapter(adapterServicos);

        recyclerServicos.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                recyclerServicos,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        itemSelecionado = items.get(position);
                        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                        intent.putExtra("cliente", cliente);
                        intent.putExtra("item", itemSelecionado);
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
        recuperarItems();
    }

    @Override
    protected void onStop() {
        super.onStop();
        items.clear();
        itensServicoRef.removeEventListener(valueEventListener);
    }

    private void recuperarItems(){
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Recuperando Orçamentos")
                .setCancelable(false)
                .build();
        dialog.show();
        items.clear();
        valueEventListener = itensServicoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    items.add(ds.getValue(ItemServico.class));
                }
                adapterServicos.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    public void excluirOrcamento(View view){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Excluir");
        alertDialog.setMessage("Tem certeza que deseja excluir esse orçamento? Após isso, todos os itens serão removidos.");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                itensServicoRef.removeValue();
                servicoOrcamentoRef.removeValue();
                finish();
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

    public void finalizar(View view){
        servicoOrcamentoSelecionado.setStatus("FINALIZADO");
        servicoOrcamentoSelecionado.salvar();
        finish();
    }

    public void reabir(View view){
        servicoOrcamentoSelecionado.setStatus("PENDENTE");
        servicoOrcamentoSelecionado.salvar();
        finish();
    }

}