package com.example.mfservice.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mfservice.R;
import com.example.mfservice.model.ItemServico;
import com.example.mfservice.model.Usuario;
import com.google.android.material.textfield.TextInputEditText;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private Usuario cliente;
    private ItemServico itemServico;
    private CircleImageView foto;
    private TextView txtNome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        toolbar.setTitle("");

        foto = findViewById(R.id.fotoChat);
        txtNome = findViewById(R.id.txtNomeChat);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            cliente = (Usuario) bundle.getSerializable("cliente");
            itemServico = (ItemServico) bundle.getSerializable("item");

            Glide.with(getApplicationContext()).load(cliente.getFoto()).into(foto);
            txtNome.setText(cliente.getNome());
        }

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
}
