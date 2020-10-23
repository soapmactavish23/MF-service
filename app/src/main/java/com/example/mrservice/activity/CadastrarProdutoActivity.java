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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.bumptech.glide.Glide;
import com.example.mrservice.R;
import com.example.mrservice.config.ConfiguracaoFirebase;
import com.example.mrservice.helper.Permissao;
import com.example.mrservice.model.Produto;
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

        //Carregar Spinner de linha
        String[] linha = getResources().getStringArray(R.array.linhas);
        ArrayAdapter<String> adapterLinha = new ArrayAdapter<String>(
                this, R.layout.simple_pinner_item_white, linha
        );
        adapterLinha.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLinha.setAdapter(adapterLinha);

        //Carregar Spinner de categoria
        String[] categoria = getResources().getStringArray(R.array.categorias);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, R.layout.simple_pinner_item_white, categoria
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapter);

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
                    btnExcluir.setVisibility(View.VISIBLE);
                    //Foto de Produto
                    for(int index = 0; index < produtoSelecionado.getFotos().size(); index ++){
                        String foto = produtoSelecionado.getFotos().get(index);
                        Uri uri = Uri.parse(foto);
                        String img = "imagem" + index;
                        if(uri != null){
                            Glide.with(CadastrarProdutoActivity.this).load(uri).into(imagem1);
                        }else{
                            imagem1.setImageResource(R.drawable.galery_padrao);
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
        String precoVenda = editPrecoVenda.getText().toString();
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
                        produto1.setTitulo(titulo);
                        produto1.setDescricao(descricao);
                        produto1.setPrecoVenda(precoVenda);
                        produto1.setLinha(linha);
                        produto1.atualizar();
                        finish();
                    } else {
                        produto1 = new Produto();
                        produto1.setTitulo(titulo);
                        produto1.setDescricao(descricao);
                        produto1.setPrecoVenda(precoVenda);
                        produto1.setCategoria(categoria);
                        produto1.setLinha(linha);
                        produto1.setProduto(produto);
                        salvarProduto();
                    }
                } else {
                    exibirMensagem("Preencha o Campo Preço de Venda");
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
                storage.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        listaUrlFotos.add(urlConvertida);
                        if(totFotos == listaUrlFotos.size()){
                            produto1.setFotos(listaUrlFotos);
                            if(produtoSelecionado == null){
                                produto1.salvar();
                                dialog.dismiss();
                                finish();
                            }else{
                                produto1.atualizar();
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
