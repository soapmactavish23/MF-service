package com.example.mfservice.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mfservice.R;
import com.example.mfservice.adapter.AdapterItem;
import com.example.mfservice.adapter.AdapterOrcamentoProduto;
import com.example.mfservice.config.ConfiguracaoFirebase;
import com.example.mfservice.config.UsuarioFirebase;
import com.example.mfservice.helper.RecyclerItemClickListener;
import com.example.mfservice.model.Item;
import com.example.mfservice.model.Produto;
import com.example.mfservice.model.ProdutoOrcamento;
import com.example.mfservice.model.Usuario;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class ProdutoOrcamentoActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdapterItem adapterItem;
    private List<Item> items = new ArrayList<>();
    private DatabaseReference itemRef, usuarioRef;
    private Usuario cliente;
    private ValueEventListener valueEventListener;
    private CircleImageView foto;
    private TextView txtNome, txtNoneProduto, txtTotal, txtEndereco, txtEmail, txtTelefone, txtFormaPagamento,
            txtPrazoEntrega, txtValidade, txtObs;
    private FloatingActionMenu floatingActionMenu;
    private CurrencyEditText txtPrecoTotal;
    private ProdutoOrcamento orcamentoSelecionado;
    private Item itemSelecionado;
    private String idCliente;
    private int valorTotal;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produto_orcamento);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        //Inicializar Componentes
        foto = findViewById(R.id.imgFotoCliente);
        txtNome = findViewById(R.id.txtNomeCliente);
        txtPrecoTotal = findViewById(R.id.txtPrecoTotal);
        txtNoneProduto = findViewById(R.id.txtNoneProduto);
        txtEndereco = findViewById(R.id.txtEndereco);
        txtEmail = findViewById(R.id.txtEmail);
        txtTelefone = findViewById(R.id.txtTelefone);
        txtTotal = findViewById(R.id.txtTotal);
        txtPrecoTotal = findViewById(R.id.txtPrecoTotal);
        recyclerView = findViewById(R.id.recyclerProdutosOrcamentos);
        txtFormaPagamento = findViewById(R.id.txtFormaPagamento);
        txtPrazoEntrega = findViewById(R.id.txtPrazoEntrega);
        txtValidade = findViewById(R.id.txtValidade);
        floatingActionMenu = findViewById(R.id.floatingActionMenu);
        txtObs = findViewById(R.id.txtObs);
        Bundle bundle = getIntent().getExtras();

        //Recuperar o orcamento
        orcamentoSelecionado = (ProdutoOrcamento) bundle.getSerializable("orcamento");
        idCliente = orcamentoSelecionado.getIdCliente();
        toolbar.setTitle(orcamentoSelecionado.getStatus());

        //Firebase
        usuarioRef = ConfiguracaoFirebase.getFirebaseDatabase().child("usuarios").child(idCliente);
        itemRef = ConfiguracaoFirebase.getFirebaseDatabase().child("itens").child(idCliente);

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cliente = snapshot.getValue(Usuario.class);

                //Configurando informações do cliente
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.padrao);
                Glide.with(getApplicationContext()).applyDefaultRequestOptions(requestOptions).load(cliente.getFoto()).into(foto);
                txtNome.setText(cliente.getNome());
                if(cliente.getEndereco().equals("")){
                    txtEndereco.setText("Endereço: SEM ENDEREÇO CADASTRADO");
                }else{
                    txtEndereco.setText("Endereço: " + cliente.getEndereco());
                }
                txtEmail.setText("E-mail: " + cliente.getEmail());
                txtTelefone.setText("Contato: " + cliente.getContato());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Configurando o footer
        txtFormaPagamento.setText("Forma de Pagamento: "+ orcamentoSelecionado.getFormaPagamento());
        txtPrazoEntrega.setText("Prazo de Entrega: " + orcamentoSelecionado.getPrazoEntrega());
        txtValidade.setText("Validade: " + orcamentoSelecionado.getValidade());
        txtObs.setText("Obs: " + orcamentoSelecionado.getObs());

        //Configurar O RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapterItem = new AdapterItem(this, items, orcamentoSelecionado, "ADM");
        recyclerView.setAdapter(adapterItem);

        //Toque no Recycler
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if(orcamentoSelecionado.getStatus().equals("PENDENTE")){
                            itemSelecionado = items.get(position);
                            editarValorItem();
                        }
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarItens();
        recuperarCliente();
    }

    @Override
    protected void onStop() {
        super.onStop();
        valorTotal = 0;
        items.clear();
        itemRef.removeEventListener(valueEventListener);
    }

    private void recuperarCliente(){

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    public void recuperarItens(){
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Recuperando Orçamentos")
                .setCancelable(false)
                .build();
        dialog.show();
        items.clear();
        valueEventListener = itemRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    Item item = ds.getValue(Item.class);
                    valorTotal += Integer.parseInt(item.getValorTotal());
                    items.add(item);
                }
                txtPrecoTotal.setText(valorTotal + "");
                adapterItem.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void editarValorItem(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Editar o valor do item");

        View viewPrecos = getLayoutInflater().inflate(R.layout.dialog_preco, null);
        alertDialog.setView(viewPrecos);

        final TextView txtQtd = viewPrecos.findViewById(R.id.txtQtd);
        final CurrencyEditText editValorUnitario = viewPrecos.findViewById(R.id.editValorUnitario);
        final CurrencyEditText editValorTotal = viewPrecos.findViewById(R.id.editValorTotal);
        final CurrencyEditText editValorDesconto = viewPrecos.findViewById(R.id.editValorDesconto);

        txtQtd.setText(itemSelecionado.getQtd());
        editValorUnitario.setText(itemSelecionado.getValorUnitario());
        editValorTotal.setText(itemSelecionado.getValorTotal());
        editValorDesconto.setText(itemSelecionado.getValorDesconto());

        alertDialog.setPositiveButton("Editar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, final int i) {
                //Calcular o valor total
                int valorDesconto = Integer.parseInt(String.valueOf(editValorDesconto.getRawValue()));
                int qtd = Integer.parseInt(itemSelecionado.getQtd());
                int vTotal = valorDesconto * qtd;

                //Salvar registros
                itemSelecionado.setValorDesconto(valorDesconto + "");
                itemSelecionado.setValorTotal(vTotal + "");
                itemSelecionado.salvar();
                valorTotal = 0;
                items.clear();

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

    public void editar(View view){
        Intent intent = new Intent(this, EditarProdutoOrcamentoActivity.class);
        intent.putExtra("orcamentoSelecionado", orcamentoSelecionado);
        startActivity(intent);
    }

    public void finalizar(View view){
        orcamentoSelecionado.setStatus("FINALIZADO");
        orcamentoSelecionado.salvar();
        finish();
    }

    public void reabir(View view){
        orcamentoSelecionado.setStatus("FINALIZADO");
        orcamentoSelecionado.salvar();
        finish();
    }

    public void excluirOrcamento(View view){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Excluir");
        alertDialog.setMessage("Tem certeza que deseja excluir esse orçamento? Após isso, todos os itens serão removidos.");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
                DatabaseReference itensRef = firebaseRef.child("itens").child(UsuarioFirebase.getIdentificadorUsuario());
                DatabaseReference produtoOrcamento = firebaseRef.child("produtoOrcamento").child(UsuarioFirebase.getIdentificadorUsuario());
                itensRef.removeValue();
                produtoOrcamento.removeValue();
                finish();
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

}
