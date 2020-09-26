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
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mrservice.R;
import com.example.mrservice.config.ConfiguracaoFirebase;
import com.example.mrservice.helper.Permissao;
import com.example.mrservice.model.Cliente;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class CadastrarClientesActivity extends AppCompatActivity {

    private TextInputEditText editNomeCliente;
    private CircleImageView imgFotoCliente;
    private String foto = "";
    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private Cliente cliente;
    private StorageReference storage;
    private android.app.AlertDialog dialog;
    private Spinner spinnerCategoria;

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
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        storage = ConfiguracaoFirebase.getStorageReference();

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
        }
    }

    public void validarCadastro(){
        String nomeCliente = editNomeCliente.getText().toString();
        if(!foto.equals("")){
            if(!nomeCliente.isEmpty()){
                cliente = new Cliente();
                cliente.setNome(nomeCliente);
                cliente.setCategoria(spinnerCategoria.getSelectedItem().toString());
                cliente.setFoto(foto);
                salvarCliente();
            }else{
                exibirMensagem("Preencha o Campo Nome do Cliente");
            }
        }else{
            exibirMensagem("Insira uma foto");
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
                storage.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        cliente.setFoto(urlConvertida);
                        cliente.salvar();
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
}
