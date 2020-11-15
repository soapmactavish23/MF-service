package com.example.mfservice.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mfservice.R;
import com.example.mfservice.adapter.AdapterMensagem;
import com.example.mfservice.config.ConfiguracaoFirebase;
import com.example.mfservice.config.UsuarioFirebase;
import com.example.mfservice.model.ItemServico;
import com.example.mfservice.model.Mensagem;
import com.example.mfservice.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class ChatActivity extends AppCompatActivity {

    private Usuario cliente;
    private ItemServico itemServico;
    private CircleImageView foto;
    private TextView txtNome;
    private EditText editMsg;
    private RecyclerView recyclerMensagens;
    private AdapterMensagem adapterMensagem;
    private List<Mensagem> listaMensagens = new ArrayList<>();
    private ValueEventListener valueEventListener;
    private DatabaseReference firebaseRef, mensagensRef;
    private String idUsuario;
    private AlertDialog dialog;

    //private static String ID_MF_SERVICE = "cmlja25vZ3VlaXJhMTIzMUBnbWFpbC5jb20=";
    private static String ID_MF_SERVICE = "bWZzZXJ2aWNlcGFAZ21haWwuY29t";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        toolbar.setTitle("");

        //Inicializando componentes de interface
        foto = findViewById(R.id.fotoChat);
        txtNome = findViewById(R.id.txtNomeChat);
        editMsg = findViewById(R.id.editMsg);
        recyclerMensagens = findViewById(R.id.recyclerMensagens);

        //Inicializando componentes do firebase
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        idUsuario = UsuarioFirebase.getIdentificadorUsuario();

        //RequestOptions da foto
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.padrao);
        requestOptions.fitCenter();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            cliente = (Usuario) bundle.getSerializable("cliente");
            itemServico = (ItemServico) bundle.getSerializable("item");

            mensagensRef = firebaseRef.child("mensagens").child(itemServico.getId());

            if(idUsuario.equals(ID_MF_SERVICE)){
                Glide.with(getApplicationContext())
                        .applyDefaultRequestOptions(requestOptions)
                        .load(cliente.getFoto())
                        .into(foto);
                txtNome.setText(cliente.getNome());
            }else{
                Glide.with(getApplicationContext()).applyDefaultRequestOptions(requestOptions).load(R.drawable.logo_about).into(foto);
                txtNome.setText("MF SERVICE");
            }
        }

        //Configurar o RecyclerView
        recyclerMensagens.setLayoutManager(new LinearLayoutManager(this));
        recyclerMensagens.setHasFixedSize(true);
        adapterMensagem = new AdapterMensagem(listaMensagens, this);
        recyclerMensagens.setAdapter(adapterMensagem);

    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarMensagens();
    }

    @Override
    protected void onStop() {
        super.onStop();
        listaMensagens.clear();
        mensagensRef.removeEventListener(valueEventListener);
    }

    private void recuperarMensagens(){
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Recuperando Mensagens")
                .setCancelable(false)
                .build();
        dialog.show();
        listaMensagens.clear();
        valueEventListener = mensagensRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dialog.dismiss();
                listaMensagens.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    listaMensagens.add(ds.getValue(Mensagem.class));
                }
                recyclerMensagens.smoothScrollToPosition(listaMensagens.size());
                adapterMensagem.notifyDataSetChanged();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_interative, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuInteratividade:
                abrirDescricao();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void abrirDescricao(){
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(ChatActivity.this);
        alertDialog.setTitle("Descrição");
        alertDialog.setMessage(itemServico.getDescricao());
        alertDialog.setCancelable(true);

        alertDialog.setPositiveButton("FECHAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    public void enviarMensagem(View view){
        String msg = editMsg.getText().toString();
        if(!msg.equals("") || !msg.isEmpty()) {
            Mensagem mensagem = new Mensagem();

            if(idUsuario.equals(ID_MF_SERVICE)){
                mensagem.setIdRemetente(ID_MF_SERVICE);
                mensagem.setIdDestinatario(cliente.getId());
            }else{
                mensagem.setIdRemetente(cliente.getId());
                mensagem.setIdDestinatario(ID_MF_SERVICE);
            }

            mensagem.setIdItem(itemServico.getId());
            mensagem.setMensagem(msg);
            mensagem.setImage("");
            mensagem.salvar();
            editMsg.setText("");
            listaMensagens.clear();
        }else {
            exibirToast("Preencha o campo de mensagem");
        }
    }

    private void exibirToast(String msg){
        Toast.makeText(
                getApplicationContext(),
                msg,
                Toast.LENGTH_SHORT
        ).show();
    }
}
