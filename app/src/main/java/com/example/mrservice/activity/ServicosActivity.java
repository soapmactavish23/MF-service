package com.example.mrservice.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mrservice.R;
import com.example.mrservice.model.ServicoOrcamento;
import com.example.mrservice.model.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

public class ServicosActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private Usuario usuario;
    private ServicoOrcamento servicoOrcamento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicos);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Configuracoes Iniciais
        //fab = findViewById(R.id.addServico);

        //Checar se o usuario e adm
        Bundle bundle = getIntent().getExtras();
        usuario = (Usuario) bundle.getSerializable("DadosUsuario");
        servicoOrcamento = new ServicoOrcamento();
        servicoOrcamento.setCliente(usuario);
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.imgAssessoria:
                servicoOrcamento.setTitulo("ASSESSORIA PARA IMPLEMENTAÇÃO DE PROJETOS");
                break;
            case R.id.imgServicos:
                servicoOrcamento.setTitulo("SERVIÇOS DE ENGENHARIA");
                break;
            case R.id.imgManutencao:
                servicoOrcamento.setTitulo("MANUTENÇÃO PREVENTIVA E CORRETIVA");
                break;
            case R.id.imgInstalacaoEquipamentos:
                servicoOrcamento.setTitulo("INSTALAÇÃO EQUIPAMENTOS");
                break;
            case R.id.imgInstalacaoPisosEsportivos:
                servicoOrcamento.setTitulo("INSTALAÇÃO PISOS ESPORTIVOS");
                break;
            case R.id.imgInstalacaoGramadoSintetico:
                servicoOrcamento.setTitulo("INSTALAÇÃO GRAMADO SINTÉTICO");
                break;
        }
        //addOrcamento(view);
    }

    public void addOrcamento(View view){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ServicosActivity.this);
        alertDialog.setTitle("Solicitar Orçamento");
        alertDialog.setMessage("Deseja solicitar o orçamento desse serviço?");
        alertDialog.setCancelable(true);

        View viewQtd = getLayoutInflater().inflate(R.layout.dialog_descricao, null);
        final TextInputEditText descricao = viewQtd.findViewById(R.id.editDepoimento);

        alertDialog.setView(viewQtd);

        alertDialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                servicoOrcamento.setDescricao(descricao.getText().toString());
                servicoOrcamento.salvar();
                exibirMensagem("Orçamento enviado com sucesso!");
            }
        });
        alertDialog.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    private void exibirMensagem(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

}
