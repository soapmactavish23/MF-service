package com.example.mfservice.activity;

import androidx.annotation.NonNull;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mfservice.R;
import com.example.mfservice.config.ConfiguracaoFirebase;
import com.example.mfservice.model.Item;
import com.example.mfservice.model.Produto;
import com.example.mfservice.model.ProdutoOrcamento;
import com.example.mfservice.model.Usuario;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
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
    private TextView txtTitulo, txtDescricao, txtCategoria, txtTipoProduto, txtLinha;
    private CarouselView carouselView;
    private Usuario cliente;
    private CurrencyEditText txtPreco;
    private ProdutoOrcamento produtoOrcamentoSelecionado;
    private List<Item> items = new ArrayList<>();
    private Boolean orcamentoPendente = false;
    private ProdutoOrcamento produtoOrcamento;

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
        txtLinha = findViewById(R.id.txtLinha);
        txtPreco = findViewById(R.id.txtPreco);
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
            txtLinha.setText(produtoSelecionado.getLinha());
            if(cliente.getTipo_usuario().equals("ADM")){
                txtPreco.setText(produtoSelecionado.getPrecoVenda());
            }else{
                txtPreco.setVisibility(View.GONE);
            }
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
        final LinearLayout linearLayout = viewQtd.findViewById(R.id.linearView);
        linearLayout.setVisibility(View.GONE);
        final TextInputEditText qtd = viewQtd.findViewById(R.id.editQtd);

        alertDialog.setView(viewQtd);

        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, final int i) {
                String qtdProdutos = qtd.getText().toString();

                //Salvar o ProdutoOrcamento
                produtoOrcamento = new ProdutoOrcamento();
                produtoOrcamento.setCliente(cliente);
                produtoOrcamento.setFormaPagamento("A COMBINAR");
                produtoOrcamento.setPrazoEntrega("EQUIPAMENTOS: 50 A 70 DIAS / CÁRDIO PISOS E ACESSÓRIOS: 30 A 45 DIAS");
                produtoOrcamento.setValidade("10 DIAS");
                produtoOrcamento.setObs("FRETE FOB - POR CONTA DO CLIENTE / INSTALAÇÃO GRÁTIS !");
                produtoOrcamento.setStatus("PENDENTE");
                produtoOrcamento.setPrecoFinal("0");
                produtoOrcamento.salvar();

                //Salvar o Item
                Item item = new Item();
                item.setIdCliente(cliente.getId());
                item.setIdProduto(produtoSelecionado.getId());
                item.setProduto(produtoSelecionado.getTitulo());
                item.setValorUnitario(produtoSelecionado.getPrecoVenda());
                item.setValorDesconto("0");
                item.setQtd(qtdProdutos);
                int valorTotal;
                if(!qtdProdutos.equals("") || !qtdProdutos.isEmpty()){
                    valorTotal = Integer.parseInt(produtoSelecionado.getPrecoVenda()) * Integer.parseInt(qtdProdutos);
                    item.setValorTotal(valorTotal + "");
                    item.salvar();
                    exibirMensagem("Orçamento Enviado Com Sucesso!");
                }else{
                    exibirMensagem("Preencha o campo de quantidade");
                }
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
