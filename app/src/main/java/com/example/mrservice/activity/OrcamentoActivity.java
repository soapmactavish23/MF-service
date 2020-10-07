package com.example.mrservice.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.mrservice.R;
import com.example.mrservice.model.Usuario;

public class OrcamentoActivity extends AppCompatActivity {

    private Usuario usuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orcamento);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            usuarioLogado = (Usuario) bundle.getSerializable("DadosUsuario");
        }

    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.btnProduto:
                Intent intent = new Intent(this, ProdutoOrcamentoActivity.class);
                intent.putExtra("DadosUsuario", usuarioLogado);
                startActivity(intent);
                break;
            case R.id.btnServico:
                break;
        }
    }
}
