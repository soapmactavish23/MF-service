package com.example.mrservice.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mrservice.R;
import com.example.mrservice.model.Produto;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.io.Serializable;

public class DetalhesProdutoActivity extends AppCompatActivity {

    private Produto produtoSelecionado;
    private TextView txtTitulo, txtDescricao, txtCategoria, txtTipoProduto;
    private CarouselView carouselView;

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
            txtTitulo.setText(produtoSelecionado.getTitulo());
            toolbar.setTitle(produtoSelecionado.getTitulo());
            txtDescricao.setText(produtoSelecionado.getDescricao());
            txtCategoria.setText(produtoSelecionado.getCategoria());
            txtTipoProduto.setText(produtoSelecionado.getProduto());
            ImageListener imageListener = new ImageListener() {
                @Override
                public void setImageForPosition(int position, ImageView imageView) {
                    String urlString = produtoSelecionado.getFotos().get(position);
                    Picasso.get().load(urlString).into(imageView);
                }
            };
            carouselView.setPageCount(produtoSelecionado.getFotos().size());
            carouselView.setImageListener(imageListener);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
