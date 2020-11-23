package com.example.mfservice.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mfservice.R;
import com.example.mfservice.config.ConfiguracaoFirebase;
import com.example.mfservice.helper.Helper;
import com.example.mfservice.helper.Permissao;
import com.example.mfservice.model.TrabalhosFeitos;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import dmax.dialog.SpotsDialog;

public class CadastrarFotosAntesDepoisActivity extends AppCompatActivity {

    private ImageView imagem1, imagem2;
    private android.app.AlertDialog dialog;
    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private StorageReference storage;
    private TrabalhosFeitos trabalhosFeitos;
    private Helper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_fotos_antes_depois);

        //Validar Permissoes
        Permissao.validarPermissoes(permissoes, this, 1);

        imagem1 = findViewById(R.id.imgTrabalhosFeitosAntes);
        imagem2 = findViewById(R.id.imgTrabalhosFeitosDepois);
        storage = ConfiguracaoFirebase.getStorageReference();
        helper = new Helper(this);

        Bundle bundle = getIntent().getExtras();
        trabalhosFeitos = (TrabalhosFeitos) bundle.getSerializable("trabalhoFeito");

        Glide.with(this)
                .applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.galery_padrao))
                .load(trabalhosFeitos.getFotoAntes()).into(imagem1);

        Glide.with(this)
                .applyDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.galery_padrao))
                .load(trabalhosFeitos.getFotoDepois()).into(imagem2);
    }

    private void salvarFotoAntes(String urlString){
        alertar();
        //Criar nó no storage
        StorageReference imgAntesRef = storage.child("imagens")
                .child("trabalhos feitos")
                .child(trabalhosFeitos.getId())
                .child("antes");

        UploadTask uploadTask = imgAntesRef.putFile(Uri.parse(urlString));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                while(!uri.isComplete());
                Uri url = uri.getResult();

                final String urlConvertida = url.toString();
                trabalhosFeitos.setFotoAntes(urlConvertida);
                trabalhosFeitos.salvar();
                dialog.dismiss();
                helper.exibirMensagem("Foto salva com sucesso");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                helper.exibirMensagem("Fala ao fazer upload");
                Log.i("INFO", "FALHA: " + e.getMessage());
            }
        });
    }

    private void salvarFotoDepois(String urlString){
        alertar();
        //Criar nó no storage
        StorageReference imgAntesRef = storage.child("imagens")
                .child("trabalhos feitos")
                .child(trabalhosFeitos.getId())
                .child("depois");

        UploadTask uploadTask = imgAntesRef.putFile(Uri.parse(urlString));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                while(!uri.isComplete());
                Uri url = uri.getResult();

                final String urlConvertida = url.toString();
                trabalhosFeitos.setFotoDepois(urlConvertida);
                trabalhosFeitos.salvar();
                dialog.dismiss();
                helper.exibirMensagem("Foto salva com sucesso");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                helper.exibirMensagem("Fala ao fazer upload");
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
            case R.id.btnCancelar:
                cancelar();
                break;
            case R.id.btnFinalizar:
                finalizar();
                break;
        }
    }

    private void alertar(){
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Salvando Foto")
                .setCancelable(false)
                .build();
        dialog.show();
    }


    private void cancelar(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cancelar");
        builder.setMessage("Tem certeza que deseja cancelar o projeto?");
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                trabalhosFeitos.deletar();
                finish();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void finalizar(){
        if(!trabalhosFeitos.getFotoDepois().equals("") || !trabalhosFeitos.getFotoAntes().equals("")){
            finish();
        }else{
            helper.exibirMensagem("Selecione as fotos antes de finalizar");
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
                salvarFotoAntes(caminhoImagem);
                imagem1.setImageURI(imagemSelecionada);
            }else if(requestCode == 2){
                salvarFotoDepois(caminhoImagem);
                imagem2.setImageURI(imagemSelecionada);
            }
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

}
