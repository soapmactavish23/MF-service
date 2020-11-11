package com.example.mfservice.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mfservice.R;
import com.example.mfservice.model.ItemServico;
import com.example.mfservice.model.Usuario;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private Usuario cliente;
    private ItemServico itemServico;
    private CircleImageView foto;
    private TextView txtDescricao, txtNome;

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
        txtDescricao = findViewById(R.id.txtDescricaoChat);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            cliente = (Usuario) bundle.getSerializable("cliente");
            itemServico = (ItemServico) bundle.getSerializable("item");

            Glide.with(getApplicationContext()).load(cliente.getFoto()).into(foto);
            txtNome.setText(cliente.getNome());
            txtDescricao.setText(itemServico.getDescricao());
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
