package com.example.mrservice.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mrservice.R;
import com.example.mrservice.model.Produto;
import com.example.mrservice.model.ProdutoOrcamento;
import com.example.mrservice.model.Usuario;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetalhesProdutoActivity extends AppCompatActivity {

    private Produto produtoSelecionado;
    private TextView txtTitulo, txtDescricao, txtCategoria, txtTipoProduto;
    private CarouselView carouselView;
    private Usuario cliente;
    private ProdutoOrcamento produtoOrcamentoSelecionado;
    private List<Produto> produtos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_produto);
        Toolbar toolbar = findViewById(R.id.toolbar5);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        //Inicializar Componentes
        txtTitulo = findViewById(R.id.txtTituloProduto);
        txtCategoria = findViewById(R.id.txtCategoriaDetalhes);
        txtTipoProduto = findViewById(R.id.txtTipoProdutoDetalhes);
        txtDescricao = findViewById(R.id.txtDescricaoDetalhes);
        carouselView = findViewById(R.id.carouselView);

        //Dados produto
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            produtoSelecionado = (Produto) bundle.getSerializable("produtoSelecionado");
            cliente = (Usuario) bundle.getSerializable("cliente");
            txtTitulo.setText(produtoSelecionado.getTitulo());
            toolbar.setTitle(produtoSelecionado.getTitulo());
            txtDescricao.setText(produtoSelecionado.getDescricao());
            txtCategoria.setText(produtoSelecionado.getCategoria());
            txtTipoProduto.setText(produtoSelecionado.getProduto());
            ImageListener imageListener = new ImageListener() {
                @Override
                public void setImageForPosition(int position, ImageView imageView) {
                    String urlString = produtoSelecionado.getFotos().get(position);
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.placeholder(R.drawable.padrao);
                    Glide.with(getApplicationContext()).applyDefaultRequestOptions(requestOptions).load(urlString).into(imageView);
                }
            };
            carouselView.setPageCount(produtoSelecionado.getFotos().size());
            carouselView.setImageListener(imageListener);

            carouselView.setImageClickListener(new ImageClickListener() {
                @Override
                public void onClick(int position) {
                    Intent intent = new Intent(DetalhesProdutoActivity.this, GaleryActivity.class);
                    intent.putExtra("foto", produtoSelecionado.getFotos().get(position));
                    intent.putExtra("titulo", produtoSelecionado.getTitulo());
                    startActivity(intent);
                }
            });

        }

    }

    public void addOrcamento(View view){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DetalhesProdutoActivity.this);
        alertDialog.setTitle("Solicitar Orçamento");
        alertDialog.setMessage("Deseja solicitar o orçamento desse produto?");
        alertDialog.setCancelable(true);

        View viewQtd = getLayoutInflater().inflate(R.layout.dialog_qtd, null);
        final TextInputEditText qtd = viewQtd.findViewById(R.id.editQtd);

        alertDialog.setView(viewQtd);

        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ProdutoOrcamento produtoOrcamento = new ProdutoOrcamento();
                for(int index = 0 ; index <= Integer.parseInt(qtd.getText().toString()); index++){
                    produtos.add(produtoSelecionado);
                }
                //Recuperar dados do usuario
                produtoOrcamento.setCliente(cliente);
                produtoOrcamento.setListaProdutos(produtos);
                produtoOrcamento.salvar();
                exibirMensagem("Orçamento enviado com sucesso");
            }
        });
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
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
