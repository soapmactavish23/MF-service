package com.example.mfservice.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.bumptech.glide.Glide;
import com.example.mfservice.R;
import com.example.mfservice.config.ConfiguracaoFirebase;
import com.example.mfservice.helper.Helper;
import com.example.mfservice.helper.Permissao;
import com.example.mfservice.model.Produto;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class CadastrarProdutoActivity extends AppCompatActivity {

    private TextInputEditText editTitulo, editDescricao;
    private CurrencyEditText editPrecoVenda, editPrecoCusto;
    private ImageView imagem1, imagem2, imagem3, imagem4, imagem5, imagem6;
    private Spinner spinnerCategoria, spinnerTipoProduto, spinnerLinha;
    private android.app.AlertDialog dialog;
    private Button btnExcluir;

    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private List<String> listaFotosRecuperadas = new ArrayList<>();
    private List<String> listaUrlFotos = new ArrayList<>();
    private StorageReference storage;
    private Produto produto1;
    private Produto produtoSelecionado;
    private Helper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_produto);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        //Validar Permissoes
        Permissao.validarPermissoes(permissoes, this, 1);

        //Inicializar Componentes
        editTitulo = findViewById(R.id.editTitulo);
        editDescricao = findViewById(R.id.editDescricao);
        editPrecoVenda = findViewById(R.id.editPrecoVenda);
        imagem1 = findViewById(R.id.imgCadastroProduto1);
        imagem2 = findViewById(R.id.imgCadastroProduto2);
        imagem3 = findViewById(R.id.imgCadastroProduto3);
        imagem4 = findViewById(R.id.imgCadastroProduto4);
        imagem5 = findViewById(R.id.imgCadastroProduto5);
        imagem6 = findViewById(R.id.imgCadastroProduto6);
        btnExcluir = findViewById(R.id.btnExcluir);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        spinnerTipoProduto = findViewById(R.id.spinnerTipoProduto);
        spinnerLinha = findViewById(R.id.spinnerLinha);
        storage = ConfiguracaoFirebase.getStorageReference();
        helper = new Helper(this);

        //Carregar Spinner de linha
        String[] linha = getResources().getStringArray(R.array.linhas);
        final ArrayAdapter<String> adapterLinha = new ArrayAdapter<String>(
                this, R.layout.simple_pinner_item_white, linha
        );
        adapterLinha.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLinha.setAdapter(adapterLinha);

        //Carregar Spinner de categoria
        String[] categoria = getResources().getStringArray(R.array.categorias);
        final ArrayAdapter<String> adapterCategoria = new ArrayAdapter<String>(
                this, R.layout.simple_pinner_item_white, categoria
        );
        adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapterCategoria);

        //Carregar Spinner de Tipo Produto
        spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] produto = getResources().getStringArray(R.array.equipamentos);
                switch (i){
                    case 0:
                        produto = getResources().getStringArray(R.array.equipamentos);
                        break;
                    case 1:
                        produto = getResources().getStringArray(R.array.cardio);
                        break;
                    case 2:
                        produto = getResources().getStringArray(R.array.acessorios);
                        break;
                    case 3:
                        produto = getResources().getStringArray(R.array.piso);
                        break;
                    case 4:
                        produto = getResources().getStringArray(R.array.revestimento);
                        break;
                    case 5:
                        produto = getResources().getStringArray(R.array.brinquedo);
                        break;
                }
                ArrayAdapter<String> adapterProduto = new ArrayAdapter<String>(
                        CadastrarProdutoActivity.this, R.layout.simple_pinner_item_white, produto
                );
                adapterProduto.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerTipoProduto.setAdapter(adapterProduto);

                //Dados produto
                Bundle bundle = getIntent().getExtras();
                if(bundle != null) {
                    produtoSelecionado = (Produto) bundle.getSerializable("produtoSelecionado");
                    editTitulo.setText(produtoSelecionado.getTitulo());
                    editDescricao.setText(produtoSelecionado.getDescricao());
                    editPrecoVenda.setText(produtoSelecionado.getPrecoVenda());
                    spinnerLinha.setSelection(adapterLinha.getPosition(produtoSelecionado.getLinha()));
                    spinnerCategoria.setSelection(adapterCategoria.getPosition(produtoSelecionado.getCategoria()));
                    spinnerTipoProduto.setSelection(adapterProduto.getPosition(produtoSelecionado.getProduto()));
                    btnExcluir.setVisibility(View.VISIBLE);

                    int totalFotos = produtoSelecionado.getFotos().size();
                    if(totalFotos > 0){
                        try{
                            if(!produtoSelecionado.getFotos().get(0).equals("") && !produtoSelecionado.getFotos().get(0).isEmpty()){
                                Glide.with(CadastrarProdutoActivity.this).load(produtoSelecionado.getFotos().get(0))
                                        .into(imagem1);
                                imagem1.setDrawingCacheEnabled(true);
                                imagem1.buildDrawingCache();

                            }else if(produtoSelecionado.getFotos().get(1) != null && !produtoSelecionado.getFotos().get(1).isEmpty()){
                                Glide.with(CadastrarProdutoActivity.this).load(produtoSelecionado.getFotos().get(1))
                                        .into(imagem2);
                            }else if(produtoSelecionado.getFotos().get(2) != null && !produtoSelecionado.getFotos().get(2).isEmpty()){
                                Glide.with(CadastrarProdutoActivity.this).load(produtoSelecionado.getFotos().get(2))
                                        .into(imagem3);
                            }else if(produtoSelecionado.getFotos().get(3) != null && !produtoSelecionado.getFotos().get(3).isEmpty()){
                                Glide.with(CadastrarProdutoActivity.this).load(produtoSelecionado.getFotos().get(3))
                                        .into(imagem4);
                            }else if(produtoSelecionado.getFotos().get(4) != null && !produtoSelecionado.getFotos().get(4).isEmpty()){
                                Glide.with(CadastrarProdutoActivity.this).load(produtoSelecionado.getFotos().get(4))
                                        .into(imagem5);
                            }else if(produtoSelecionado.getFotos().get(5) != null && !produtoSelecionado.getFotos().get(5).isEmpty()){
                                Glide.with(CadastrarProdutoActivity.this).load(produtoSelecionado.getFotos().get(6))
                                        .into(imagem6);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    public void validarDadosProduto(View view){
        String titulo = editTitulo.getText().toString();
        String descricao = editDescricao.getText().toString();
        //String precoVenda = editPrecoVenda.getText().toString();
        String precoVenda = String.valueOf(editPrecoVenda.getRawValue());
        String linha = "";
        if(!spinnerLinha.getSelectedItem().toString().equals("NENHUM")){
            linha = spinnerLinha.getSelectedItem().toString();
        }
        String categoria = spinnerCategoria.getSelectedItem().toString();
        String produto = spinnerTipoProduto.getSelectedItem().toString();
        if(listaFotosRecuperadas.size() != 0 || produtoSelecionado != null){
            if(!titulo.isEmpty()) {
                if (!precoVenda.isEmpty()) {
                    if (produtoSelecionado != null) {
                        produto1 = produtoSelecionado;
                    } else {
                        produto1 = new Produto();
                    }
                    produto1.setTitulo(titulo.toUpperCase());
                    produto1.setDescricao(descricao);
                    produto1.setPrecoVenda(precoVenda);
                    produto1.setCategoria(categoria);
                    produto1.setLinha(linha);
                    produto1.setProduto(produto);
                    salvarProduto();
                } else {
                    helper.exibirMensagem("Preencha o Campo Preço de Venda");
                }
            }else{
                helper.exibirMensagem("Preencha o Campo Título");
            }
        }else{
            helper.exibirMensagem("Selecione ao menos uma foto");
        }
    }

    private void salvarProduto(){
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Salvando Produto")
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
        StorageReference imagemProduto = storage.child("imagens")
                .child("produtos")
                .child(produto1.getId())
                .child("imagem" + contador);

        UploadTask uploadTask = imagemProduto.putFile(Uri.parse(urlString));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                while(!uri.isComplete());
                Uri url = uri.getResult();
                final String urlConvertida = url.toString();
                listaUrlFotos.add(urlConvertida);
                if(totFotos == listaUrlFotos.size()){
                    produto1.setFotos(listaUrlFotos);
                    produto1.salvar();
                    /*if(produtoSelecionado != null){
                        produto1.atualizar();
                    }else{
                        produto1.setFotos(listaUrlFotos);
                        produto1.salvar();
                    }*/
                    dialog.dismiss();
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                helper.exibirMensagem("Fala ao fazer upload");
                Log.i("INFO", "FALHA: " + e.getMessage());
            }
        });

    }

    private void atualizarFotoStorage(byte[] data, int contador){
        //Criar nó no storage
        StorageReference imagemProduto = storage.child("imagens")
                .child("produtos")
                .child(produto1.getId())
                .child("imagem" + contador);

        UploadTask uploadTask = imagemProduto.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            }
        });
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.imgCadastroProduto1:
                escolherImagem(1);
                break;
            case R.id.imgCadastroProduto2:
                escolherImagem(2);
                break;
            case R.id.imgCadastroProduto3:
                escolherImagem(3);
                break;
            case R.id.imgCadastroProduto4:
                escolherImagem(4);
                break;
            case R.id.imgCadastroProduto5:
                escolherImagem(5);
                break;
            case R.id.imgCadastroProduto6:
                escolherImagem(6);
                break;
        }
    }

    public void escolherImagem(int requestCode){
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, requestCode);
    }

    public byte[] escolherFotoExistente(View view){
        Bitmap bitmap = ((BitmapDrawable) imagem1.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        return data;
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
            }else if(requestCode == 3){
                imagem3.setImageURI(imagemSelecionada);
            }else if(requestCode == 4){
                imagem4.setImageURI(imagemSelecionada);
            }else if(requestCode == 5){
                imagem5.setImageURI(imagemSelecionada);
            }else if(requestCode == 6){
                imagem6.setImageURI(imagemSelecionada);
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

    public void excluir(View view){
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(CadastrarProdutoActivity.this);
        alertDialog.setTitle("Excluir");
        alertDialog.setMessage("Tem certeza que deseja excluir esse cliente?");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                produtoSelecionado.deletar();
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
