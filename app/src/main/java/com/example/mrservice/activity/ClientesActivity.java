package com.example.mrservice.activity;

import android.content.Intent;
import android.os.Bundle;

import com.example.mrservice.model.Cliente;
import com.example.mrservice.model.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import com.example.mrservice.R;

public class ClientesActivity extends AppCompatActivity {

    private Usuario usuario;
    private String categoria = "";
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientes);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ClientesActivity.this, CadastrarClientesActivity.class));
            }
        });

        //Checar se o usuario e adm
        Bundle bundle = getIntent().getExtras();
        usuario = (Usuario) bundle.getSerializable("DadosUsuario");
        if(!usuario.getTipo_usuario().equals("ADM")){
            fab.setVisibility(View.GONE);
        }

        intent = new Intent(ClientesActivity.this, ListClienteActivity.class);
        intent.putExtra("DadosUsuario", usuario);
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.imgAcademia:
                intent.putExtra("categoria", "Academia");
                startActivity(intent);
                break;
            case R.id.imgCondominio:
                intent.putExtra("categoria", "Condomínio");
                startActivity(intent);
                break;
            case R.id.imgClinica:
                intent.putExtra("categoria", "Clínica");
                startActivity(intent);
                break;
            case R.id.imgClube:
                intent.putExtra("categoria", "Clube");
                startActivity(intent);
                break;
            case R.id.imgHotel:
                intent.putExtra("categoria", "Hotel");
                startActivity(intent);
                break;
            case R.id.imgOrgaoPublico:
                intent.putExtra("categoria", "Orgão Público");
                startActivity(intent);
                break;
            case R.id.imgOrgaoPrivado:
                intent.putExtra("categoria", "Orgão Privado");
                startActivity(intent);
                break;
            case R.id.imgResidencia:
                intent.putExtra("categoria", "Residência");
                startActivity(intent);
                break;
        }
    }

}
