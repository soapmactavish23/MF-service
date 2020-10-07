package com.example.mrservice.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;

import com.example.mrservice.R;
import com.example.mrservice.model.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

public class ServicosActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicos);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Configuracoes Iniciais
        fab = findViewById(R.id.addServico);

        //Checar se o usuario e adm
        Bundle bundle = getIntent().getExtras();
        usuario = (Usuario) bundle.getSerializable("DadosUsuario");

    }

    public void onClick(View view){
        Intent intent = new Intent(ServicosActivity.this, ListServicoActivity.class);
        intent.putExtra("usuario", usuario);
        switch (view.getId()){
            case R.id.addServico:
                startActivity(new Intent(ServicosActivity.this, CadastrarServicosActivity.class));
                break;
            case R.id.imgAssessoria:
                intent.putExtra("categoria", "ASSESSORIA PARA IMPLEMENTAÇÃO DE PROJETOS");
                startActivity(intent);
                break;
            case R.id.imgServicos:
                intent.putExtra("categoria", "SERVIÇOS DE ENGENHARIA");
                startActivity(intent);
                break;
            case R.id.imgManutencao:
                intent.putExtra("categoria", "MANUTENÇÃO PREVENTIVA E CORRETIVA");
                startActivity(intent);
                break;
            case R.id.imgInstalacaoEquipamentos:
                intent.putExtra("categoria", "INSTALAÇÃO EQUIPAMENTOS");
                startActivity(intent);
                break;
            case R.id.imgInstalacaoPisosEsportivos:
                intent.putExtra("categoria", "INSTALAÇÃO PISOS ESPORTIVOS");
                startActivity(intent);
                break;
            case R.id.imgInstalacaoGramadoSintetico:
                intent.putExtra("categoria", "INSTALAÇÃO GRAMADO SINTÉTICO");
                startActivity(intent);
                break;
        }
    }

    private void enviarOrcamento(){

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

}
