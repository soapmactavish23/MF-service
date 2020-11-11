package com.example.mfservice.activity;

import androidx.annotation.NonNull;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mfservice.R;
import com.example.mfservice.adapter.AdapterOrcamento;
import com.example.mfservice.config.ConfiguracaoFirebase;
import com.example.mfservice.model.Produto;
import com.example.mfservice.model.ProdutoOrcamento;
import com.example.mfservice.model.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
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
    private DatabaseReference usuarioRef;
    private Usuario cliente;

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

        //Firebase
        usuarioRef = ConfiguracaoFirebase.getFirebaseDatabase().child("usuarios");

        if(bundle != null){
            produtoOrcamentoSelecionado = (ProdutoOrcamento) bundle.getSerializable("produtoOrcamentoSelecionado");

            usuarioRef.child(produtoOrcamentoSelecionado.getIdCliente()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    cliente = snapshot.getValue(Usuario.class);
                    tipoUsuario = cliente.getTipo_usuario();
                    txtCliente.setText("Cliente: "+ cliente.getNome());

                    if(tipoUsuario.equals("ADM")){
                        imgFotoCliente.setVisibility(View.VISIBLE);

                        RequestOptions requestOptions = new RequestOptions();
                        requestOptions.placeholder(R.drawable.padrao);

                        Glide.with(getApplicationContext())
                                .applyDefaultRequestOptions(requestOptions)
                                .load(cliente.getFoto())
                                .into(imgFotoCliente);
                        imgFotoCliente.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(DetalhesOrcamentoProdutoActivity.this, GaleryActivity.class);
                                intent.putExtra("foto", cliente.getFoto());
                                intent.putExtra("titulo", cliente.getNome());
                                startActivity(intent);
                            }
                        });
                    }else{
                        fabSave.setVisibility(View.GONE);
                        imgFotoCliente.setVisibility(View.GONE);
                    }

                    adapterOrcamento = new AdapterOrcamento(getApplicationContext(), produtosList);

                    //Configurar o RecyclerView
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setAdapter(adapterOrcamento);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
