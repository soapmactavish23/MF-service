package com.example.mrservice.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.example.mrservice.R;
import com.example.mrservice.model.ProdutoOrcamento;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetalhesOrcamentoProdutoActivity extends AppCompatActivity {

    private ProdutoOrcamento produtoOrcamentoSelecionado;
    private TextView txtCliente, txtQuantidade, txtProduto, txtPreco, txtStatus;
    private CurrencyEditText editPrecoFinal;
    private CarouselView carouselView;
    private String tipoUsuario;
    private FloatingActionButton fabSave;
    private CircleImageView imgFotoCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_orcamento_produto);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        //Configuracoes Iniciais
        txtCliente = findViewById(R.id.txtCliente);
        txtQuantidade = findViewById(R.id.txtQuantidade);
        txtProduto = findViewById(R.id.txtProduto);
        txtPreco = findViewById(R.id.txtPreco);
        editPrecoFinal = findViewById(R.id.editPrecoFinal);
        fabSave = findViewById(R.id.fabSave);
        imgFotoCliente = findViewById(R.id.imgFotoCliente);
        carouselView = findViewById(R.id.carouselView);
        txtStatus = findViewById(R.id.txtStatus);
        Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            produtoOrcamentoSelecionado = (ProdutoOrcamento) bundle.getSerializable("produtoOrcamentoSelecionado");
            tipoUsuario = bundle.getString("tipoUsuario");
            txtCliente.setText("Cliente: "+ produtoOrcamentoSelecionado.getCliente().getNome());
            txtQuantidade.setText("Quantidade: " + produtoOrcamentoSelecionado.getQtd());
            txtProduto.setText("Produto: " + produtoOrcamentoSelecionado.getProduto().getTitulo());
            txtStatus.setText("Status: " + produtoOrcamentoSelecionado.getStatus());

            if(tipoUsuario.equals("ADM")){
                if(!produtoOrcamentoSelecionado.getStatus().equals("FINALIZADO")){
                    fabSave.setVisibility(View.VISIBLE);
                }else{
                    fabSave.setVisibility(View.GONE);
                }
                imgFotoCliente.setVisibility(View.VISIBLE);
                carouselView.setVisibility(View.GONE);
                if(!produtoOrcamentoSelecionado.getCliente().getFoto().equals("")){
                    Picasso.get().load(produtoOrcamentoSelecionado.getCliente().getFoto()).into(imgFotoCliente);
                }else{
                    imgFotoCliente.setImageResource(R.drawable.padrao);
                }
                imgFotoCliente.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(DetalhesOrcamentoProdutoActivity.this, GaleryActivity.class);
                        intent.putExtra("foto", produtoOrcamentoSelecionado.getCliente().getFoto());
                        intent.putExtra("titulo", produtoOrcamentoSelecionado.getCliente().getNome());
                        startActivity(intent);
                    }
                });
            }else{
                fabSave.setVisibility(View.GONE);
                imgFotoCliente.setVisibility(View.GONE);
                carouselView.setVisibility(View.VISIBLE);
                ImageListener imageListener = new ImageListener() {
                    @Override
                    public void setImageForPosition(int position, ImageView imageView) {
                        //imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        String urlString = produtoOrcamentoSelecionado.getProduto().getFotos().get(position);
                        Picasso.get().load(urlString).into(imageView);
                    }
                };
                carouselView.setPageCount(produtoOrcamentoSelecionado.getProduto().getFotos().size());
                carouselView.setImageListener(imageListener);

                carouselView.setImageClickListener(new ImageClickListener() {
                    @Override
                    public void onClick(int position) {
                        Intent intent = new Intent(DetalhesOrcamentoProdutoActivity.this, GaleryActivity.class);
                        intent.putExtra("foto", produtoOrcamentoSelecionado.getProduto().getFotos().get(position));
                        intent.putExtra("titulo", produtoOrcamentoSelecionado.getProduto().getTitulo());
                        startActivity(intent);
                    }
                });
            }

            if(produtoOrcamentoSelecionado.getStatus().equals("FINALIZADO") || tipoUsuario.equals("ADM")){
                txtPreco.setVisibility(View.VISIBLE);
                editPrecoFinal.setVisibility(View.VISIBLE);
            }else{
                txtPreco.setVisibility(View.GONE);
                editPrecoFinal.setVisibility(View.GONE);
            }

            double precoVenda = Double.parseDouble(produtoOrcamentoSelecionado.getProduto().getPrecoVenda());
            int qtd = Integer.parseInt(produtoOrcamentoSelecionado.getQtd());
            double precoFinal = precoVenda * qtd;
            editPrecoFinal.setText(precoFinal + "");
        }

    }

    public void finalizarOrcamento(View view){
        produtoOrcamentoSelecionado.setPrecoFinal(editPrecoFinal.getText().toString());
        produtoOrcamentoSelecionado.setStatus("FINALIZADO");
        produtoOrcamentoSelecionado.atualizar();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
