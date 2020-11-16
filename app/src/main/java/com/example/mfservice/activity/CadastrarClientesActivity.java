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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mfservice.R;
import com.example.mfservice.config.ConfiguracaoFirebase;
import com.example.mfservice.config.UsuarioFirebase;
import com.example.mfservice.helper.Permissao;
import com.example.mfservice.model.Cliente;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class CadastrarClientesActivity extends AppCompatActivity {

    private TextInputEditText editNomeCliente, editDepoimento;
    private CircleImageView imgFotoCliente;
    private String foto = "";
    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private Cliente cliente;
    private Cliente clienteSelecionado;
    private StorageReference storage;
    private android.app.AlertDialog dialog;
    private Spinner spinnerCategoria;
    private Button btnExcluir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_clientes);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        //Validar Permissoes
        Permissao.validarPermissoes(permissoes, this, 1);

        //Inicializar Componentes
        imgFotoCliente = findViewById(R.id.imgFotoCliente);
        editNomeCliente = findViewById(R.id.editNomeCliente);
        editDepoimento = findViewById(R.id.editDepoimento);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        storage = ConfiguracaoFirebase.getStorageReference();
        btnExcluir = findViewById(R.id.btnExcluir);

        //Bundle
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            clienteSelecionado = (Cliente) bundle.getSerializable("cliente");
            editNomeCliente.setText(clienteSelecionado.getNome());
            editDepoimento.setText(clienteSelecionado.getDepoimento());
            if(!clienteSelecionado.getFoto().isEmpty()){
                Uri uri = Uri.parse(clienteSelecionado.getFoto());
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.padrao);
                Glide.with(getApplicationContext()).applyDefaultRequestOptions(requestOptions).load(uri).into(imgFotoCliente);
            }else{
                imgFotoCliente.setImageResource(R.drawable.padrao);
            }
            spinnerCategoria.setVisibility(View.VISIBLE);
            btnExcluir.setVisibility(View.VISIBLE);
        }

        //Carregar Spinner de categoria
        String[] categoria = getResources().getStringArray(R.array.clientes_categorias);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, R.layout.simple_pinner_item_white, categoria
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapter);

    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.imgFotoCliente:
                escolherImagem(1);
                break;
            case R.id.btnCadastrar:
                validarCadastro();
                break;
            case R.id.btnExcluir:
                excluirCliente();
                break;
        }
    }

    public void validarCadastro(){
        String nomeCliente = editNomeCliente.getText().toString();
        String depoimentoCliente = editDepoimento.getText().toString();
        if(!nomeCliente.isEmpty()){
            if(clienteSelecionado != null){
                clienteSelecionado.setNome(nomeCliente);
                clienteSelecionado.setDepoimento(depoimentoCliente);
                if(!foto.isEmpty()){
                    clienteSelecionado.setFoto(foto);
                }
                salvarCliente();
            }else{
                cliente = new Cliente();
                cliente.setNome(nomeCliente);
                cliente.setCategoria(spinnerCategoria.getSelectedItem().toString());
                cliente.setFoto(foto);
                cliente.setDepoimento(depoimentoCliente);
                salvarCliente();
            }
        }else{
            exibirMensagem("Preencha o Campo Nome do Cliente");
        }
    }

    private void salvarCliente(){
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Salvando Cliente")
                .setCancelable(false)
                .build();
        dialog.show();
        salvarFotoStorage(foto);
    }

    private void salvarFotoStorage(String urlString){
        if(clienteSelecionado == null){
            if(urlString.isEmpty()){
                dialog.dismiss();
                cliente.salvar();
                finish();
            }else{
                //Criar nó no storage
                StorageReference imagemProduto = storage.child("imagens")
                        .child("cliente")
                        .child(cliente.getCategoria())
                        .child(cliente.getId())
                        .child("imagem");
                UploadTask uploadTask = imagemProduto.putFile(Uri.parse(urlString));
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uri.isComplete());
                        Uri url = uri.getResult();
                        final String urlConvertida = url.toString();
                        cliente.setFoto(urlConvertida);
                        cliente.salvar();
                        dialog.dismiss();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        exibirMensagem("Fala ao fazer upload");
                        Log.i("INFO", "FALHA: " + e.getMessage());
                    }
                });
            }
        }else{
            if(urlString.isEmpty()){
                dialog.dismiss();
                clienteSelecionado.atualizar();
                finish();
            }else{
                //Criar nó no storage
                StorageReference imagemProduto = storage.child("imagens")
                        .child("cliente")
                        .child(clienteSelecionado.getCategoria())
                        .child(clienteSelecionado.getId())
                        .child("imagem");
                UploadTask uploadTask = imagemProduto.putFile(Uri.parse(urlString));
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
                                clienteSelecionado.setFoto(urlConvertida);
                                clienteSelecionado.atualizar();
                                dialog.dismiss();
                                finish();
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
            imgFotoCliente.setImageURI(imagemSelecionada);
            foto = caminhoImagem;
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

    private void exibirMensagem(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void excluirCliente(){
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(CadastrarClientesActivity.this);
        alertDialog.setTitle("Excluir");
        alertDialog.setMessage("Tem certeza que deseja excluir esse cliente?");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                clienteSelecionado.deletar();
                finish();
            }
        });
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        android.app.AlertDialog alert = alertDialog.create();
        alert.show();
    }
}
