package com.example.mfservice.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.example.mfservice.R;
import com.example.mfservice.adapter.AdapterOrcamento;
import com.example.mfservice.model.Produto;
import com.example.mfservice.model.ProdutoOrcamento;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetalhesOrcamentoProdutoActivity extends AppCompatActivity {

    private ProdutoOrcamento produtoOrcamentoSelecionado;
    private TextView txtCliente, txtQuantidade, txtProduto, txtPreco, txtStatus;
    private CurrencyEditText editPrecoFinal;
    private String tipoUsuario;
    private FloatingActionButton fabSave;
    private CircleImageView imgFotoCliente;
    private List<Produto> produtosList = new ArrayList<>();
    private AdapterOrcamento adapterOrcamento;
    private RecyclerView recyclerView;

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
        txtPreco = findViewById(R.id.txtPreco);
        editPrecoFinal = findViewById(R.id.editPrecoFinal);
        fabSave = findViewById(R.id.fabSave);
        imgFotoCliente = findViewById(R.id.imgFotoCliente);
        txtStatus = findViewById(R.id.txtStatus);
        recyclerView = findViewById(R.id.recyclerProdutos);
        Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            produtoOrcamentoSelecionado = (ProdutoOrcamento) bundle.getSerializable("produtoOrcamentoSelecionado");
            tipoUsuario = bundle.getString("tipoUsuario");
            txtCliente.setText("Cliente: "+ produtoOrcamentoSelecionado.getCliente().getNome());
            //txtStatus.setText("Status: " + produtoOrcamentoSelecionado.getStatus());

            if(tipoUsuario.equals("ADM")){
                /*if(!produtoOrcamentoSelecionado.getStatus().equals("FINALIZADO")){
                    fabSave.setVisibility(View.VISIBLE);
                }else{
                    fabSave.setVisibility(View.GONE);
                }*/
                imgFotoCliente.setVisibility(View.VISIBLE);
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
            }

            /*if(produtoOrcamentoSelecionado.getStatus().equals("FINALIZADO") || tipoUsuario.equals("ADM")){
                txtPreco.setVisibility(View.VISIBLE);
                editPrecoFinal.setVisibility(View.VISIBLE);
            }else{
                txtPreco.setVisibility(View.GONE);
                editPrecoFinal.setVisibility(View.GONE);
            }*/

            adapterOrcamento = new AdapterOrcamento(this, produtosList);

            //Configurar o RecyclerView
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapterOrcamento);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void finalizarOrcamento(View view){
        //produtoOrcamentoSelecionado.setStatus("FINALIZADO");
        produtoOrcamentoSelecionado.atualizar();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
