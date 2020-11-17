package com.example.mfservice.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mfservice.R;
import com.example.mfservice.config.ConfiguracaoFirebase;
import com.example.mfservice.helper.Permissao;
import com.example.mfservice.model.TrabalhosFeitos;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class CadastrarTrabalhosFeitosActivity extends AppCompatActivity {

    private TextInputEditText editTitulo, editDescricao;
    private TextView txtTitulo;
    private TrabalhosFeitos trabalhosFeitosSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_trabalhos_feitos);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Cadastrar Trabalhos Feitos");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        //Inicializando componentes
        editTitulo = findViewById(R.id.editTitulo);
        editDescricao = findViewById(R.id.editDescricao);
        txtTitulo = findViewById(R.id.txtTitulo);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            trabalhosFeitosSelecionado = (TrabalhosFeitos) bundle.getSerializable("trabalhoFeitoSelecionado");
            editTitulo.setText(trabalhosFeitosSelecionado.getTitulo());
            editDescricao.setText(trabalhosFeitosSelecionado.getDescricao());
            txtTitulo.setText("Atualizar Trabalho Feito");
        }else{
            trabalhosFeitosSelecionado = new TrabalhosFeitos();
            txtTitulo.setText("Passo 1: Preencha os Campos");
        }
    }


    public void validarDadosTrabalhosFeitos(View view){
        String titulo = editTitulo.getText().toString();
        String descricao = editDescricao.getText().toString();
        if(!titulo.isEmpty()){
            trabalhosFeitosSelecionado.setTitulo(titulo);
            trabalhosFeitosSelecionado.setDescricao(descricao);
            trabalhosFeitosSelecionado.salvar();
            Intent intent = new Intent(getApplicationContext(), CadastrarFotosAntesDepoisActivity.class);
            intent.putExtra("trabalhoFeito", trabalhosFeitosSelecionado);
            startActivity(intent);
            finish();
        }else{
            exibirMensagem("Preencha o Campo Título");
        }
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