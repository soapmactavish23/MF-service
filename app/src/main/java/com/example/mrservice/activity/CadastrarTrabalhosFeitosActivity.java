package com.example.mrservice.activity;

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

import com.example.mrservice.R;
import com.example.mrservice.config.ConfiguracaoFirebase;
import com.example.mrservice.helper.Permissao;
import com.example.mrservice.model.TrabalhosFeitos;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class CadastrarTrabalhosFeitosActivity extends AppCompatActivity {

    private TextInputEditText editTitulo, editDescricao;
    private ImageView imagem1, imagem2;
    private android.app.AlertDialog dialog;
    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private List<String> listaFotosRecuperadas = new ArrayList<>();
    private List<String> listaUrlFotos = new ArrayList<>();
    private StorageReference storage;
    private TrabalhosFeitos trabalhosFeitos;
    private TrabalhosFeitos trabalhosFeitosSelecionado;
    private TextView txtAntes, txtDepois;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_trabalhos_feitos);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Cadastrar Trabalhos Feitos");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        //Validar Permissoes
        Permissao.validarPermissoes(permissoes, this, 1);

        //Inicializando componentes
        editTitulo = findViewById(R.id.editTitulo);
        editDescricao = findViewById(R.id.editDescricao);
        imagem1 = findViewById(R.id.imgTrabalhosFeitosAntes);
        imagem2 = findViewById(R.id.imgTrabalhosFeitosDepois);
        storage = ConfiguracaoFirebase.getStorageReference();
        txtAntes = findViewById(R.id.txtAntes);
        txtDepois = findViewById(R.id.txtDepois);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            trabalhosFeitosSelecionado = (TrabalhosFeitos) bundle.getSerializable("trabalhoFeitoSelecionado");
            editTitulo.setText(trabalhosFeitosSelecionado.getTitulo());
            editDescricao.setText(trabalhosFeitosSelecionado.getDescricao());
            imagem1.setVisibility(View.GONE);
            imagem2.setVisibility(View.GONE);
            txtAntes.setVisibility(View.GONE);
            txtDepois.setVisibility(View.GONE);
        }

    }

    public void validarDadosTrabalhosFeitos(View view){
        String titulo = editTitulo.getText().toString();
        String descricao = editDescricao.getText().toString();
        if(listaFotosRecuperadas.size() != 0 || trabalhosFeitosSelecionado != null){
            if(!titulo.isEmpty()){
                if(!descricao.isEmpty()){
                    if(trabalhosFeitosSelecionado != null){
                        trabalhosFeitos = trabalhosFeitosSelecionado;
                        trabalhosFeitos.setTitulo(titulo);
                        trabalhosFeitos.setDescricao(descricao);
                        trabalhosFeitos.atualizar();
                        finish();
                    }else{
                        trabalhosFeitos = new TrabalhosFeitos();
                    }
                    trabalhosFeitos.setTitulo(titulo);
                    trabalhosFeitos.setDescricao(descricao);
                    salvarTrabalhosFeitos();
                }else{
                    exibirMensagem("Preencha o Campo de Descrição");
                }
            }else{
                exibirMensagem("Preencha o Campo Título");
            }
        }else{
            exibirMensagem("Selecione ao menos uma foto");
        }
    }

    private void exibirMensagem(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void salvarTrabalhosFeitos(){
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Salvando Trabalho Feito")
                .setCancelable(false)
                .build();
        dialog.show();
        int tamanhoLista = listaFotosRecuperadas.size();
        for(int i = 0; i < tamanhoLista; i++){
            String urlImagem = listaFotosRecuperadas.get(i);
            salvarFotoStorage(urlImagem, tamanhoLista, i);
        }
    }

    private void salvarFotoStorage(String urlString, final int totFotos, int contador){
        //Criar nó no storage
        StorageReference imagemTrabalhoFeito = storage.child("imagens")
                .child("trabalhos feitos")
                .child(trabalhosFeitos.getId())
                .child("imagem" + contador);

        UploadTask uploadTask = imagemTrabalhoFeito.putFile(Uri.parse(urlString));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                while(!uri.isComplete());
                Uri url = uri.getResult();
                final String urlConvertida = url.toString();
                storage.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        listaUrlFotos.add(urlConvertida);
                        if(totFotos == listaUrlFotos.size()){
                            trabalhosFeitos.setFotos(listaUrlFotos);
                            if(trabalhosFeitosSelecionado == null){
                                trabalhosFeitos.salvar();
                                dialog.dismiss();
                                finish();
                            }else{
                                trabalhosFeitos.atualizar();
                                dialog.dismiss();
                                finish();
                            }
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                exibirMensagem("Fala ao fazer upload");
                Log.i("INFO", "FALHA: " + e.getMessage());
            }
        });

    }
    
    public void onClick(View view){
        switch (view.getId()){
            case R.id.imgTrabalhosFeitosAntes:
                escolherImagem(1);
                break;
            case R.id.imgTrabalhosFeitosDepois:
                escolherImagem(2);
                break;
        }
    }

    public void escolherImagem(int requestCode){
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){
            //Recuperar Imagem
            Uri imagemSelecionada = data.getData();
            String caminhoImagem = imagemSelecionada.toString();
            if(requestCode == 1){
                imagem1.setImageURI(imagemSelecionada);
            }else if(requestCode == 2){
                imagem2.setImageURI(imagemSelecionada);
            }
            listaFotosRecuperadas.add(caminhoImagem);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for(int permissaoResultado : grantResults){
            if(permissaoResultado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
            }
        }
    }

    private void alertaValidacaoPermissao(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utitlizar o app é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}