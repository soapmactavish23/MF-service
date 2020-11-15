package com.example.mfservice.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mfservice.R;
import com.example.mfservice.activity.CadastrarProdutoActivity;
import com.example.mfservice.activity.DetalhesOrcamentoProdutoActivity;
import com.example.mfservice.activity.DetalhesProdutoActivity;
import com.example.mfservice.activity.ListProdutosActivity;
import com.example.mfservice.activity.ProdutoOrcamentoActivity;
import com.example.mfservice.adapter.AdapterItem;
import com.example.mfservice.adapter.AdapterProdutos;
import com.example.mfservice.config.ConfiguracaoFirebase;
import com.example.mfservice.config.UsuarioFirebase;
import com.example.mfservice.helper.RecyclerItemClickListener;
import com.example.mfservice.model.Item;
import com.example.mfservice.model.Produto;
import com.example.mfservice.model.ProdutoOrcamento;
import com.example.mfservice.model.Usuario;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProdutosFragment extends Fragment {

    private RecyclerView recyclerView;
    private AdapterItem adapterItem;
    private List<Item> items = new ArrayList<>();
    private DatabaseReference itemRef, produtoOrcamentoRef, usuarioRef;
    private ValueEventListener valueEventListener;
    private CircleImageView foto;
    private TextView txtNome, txtNoneProduto, txtTotal, txtEndereco, txtEmail, txtTelefone,
            txtFormaPagamento, txtPrazoEntrega, txtValidade, txtObs;
    private CurrencyEditText txtPrecoTotal;
    private ProdutoOrcamento orcamentoSelecionado;
    private Usuario cliente;
    private Item itemSelecionado;
    private int valorTotal;
    private LinearLayout linearValor;
    private FloatingActionButton btnExcluir, btnExportarPdf;
    private AlertDialog dialog;

    public ProdutosFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_produtos, container, false);

        //Inicializar componentes
        foto = view.findViewById(R.id.imgFotoCliente);
        txtNome = view.findViewById(R.id.txtNome);
        txtNoneProduto = view.findViewById(R.id.txtNoneProduto);
        txtEndereco = view.findViewById(R.id.txtEndereco);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtTelefone = view.findViewById(R.id.txtTelefone);
        txtFormaPagamento = view.findViewById(R.id.txtFormaPagamento);
        txtPrazoEntrega = view.findViewById(R.id.txtPrazoEntrega);
        txtValidade = view.findViewById(R.id.txtValidade);
        txtObs = view.findViewById(R.id.txtObs);
        txtPrecoTotal = view.findViewById(R.id.txtPrecoTotal);
        txtTotal = view.findViewById(R.id.txtTotal);
        recyclerView = view.findViewById(R.id.recyclerItems);
        btnExcluir = view.findViewById(R.id.btnExcluir);
        btnExportarPdf = view.findViewById(R.id.btnExportarPdf);
        adapterItem = new AdapterItem(getActivity(), items, orcamentoSelecionado, "CLIENTE");
        linearValor = view.findViewById(R.id.linearValor);

        //DatabasesReferences
        produtoOrcamentoRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("produtoOrcamento").child(UsuarioFirebase.getIdentificadorUsuario());
        usuarioRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("usuarios").child(UsuarioFirebase.getIdentificadorUsuario());
        itemRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("itens").child(UsuarioFirebase.getIdentificadorUsuario());

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Iniciaizando dados cliente
                cliente = snapshot.getValue(Usuario.class);
                try{
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.placeholder(R.drawable.padrao);
                    Glide.with(getActivity()).applyDefaultRequestOptions(requestOptions).load(cliente.getFoto()).into(foto);

                    txtNome.setText(cliente.getNome());
                    txtEndereco.setText("Endereço: "+ cliente.getEndereco());
                    txtEmail.setText("E-mail: " + cliente.getEmail());
                    txtTelefone.setText("Contato: " + cliente.getContato());
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });

        produtoOrcamentoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try{
                    //Inicializar dados do orcamento
                    orcamentoSelecionado = snapshot.getValue(ProdutoOrcamento.class);
                    txtFormaPagamento.setText("Forma de Pagamento: " + orcamentoSelecionado.getFormaPagamento());
                    txtPrazoEntrega.setText("Prazo de Entrega: " + orcamentoSelecionado.getPrazoEntrega());
                    txtValidade.setText("Validade: " + orcamentoSelecionado.getValidade());
                    txtObs.setText("Observação: " + orcamentoSelecionado.getObs());
                    linearValor.setVisibility(View.VISIBLE);
                    if(orcamentoSelecionado.getStatus().equals("PENDENTE")){
                        txtTotal.setText("Aguarde a resposta do orçamento");
                        txtPrecoTotal.setVisibility(View.GONE);
                    }else{
                        txtPrecoTotal.setVisibility(View.VISIBLE);
                    }

                    //Configurando Recycler
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setHasFixedSize(true);
                    adapterItem = new AdapterItem(getActivity(), items, orcamentoSelecionado, "CLIENTE");
                    recyclerView.setAdapter(adapterItem);

                    //Toque no Recycler
                    recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(
                            getActivity(),
                            recyclerView,
                            new RecyclerItemClickListener.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    itemSelecionado = items.get(position);
                                    editar();
                                }

                                @Override
                                public void onLongItemClick(View view, int position) {
                                }

                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                }
                            }
                    ));

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        swipe();

        btnExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                excluirOrcamento();
            }
        });


        btnExportarPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarItens();
    }

    @Override
    public void onStop() {
        super.onStop();
        valorTotal = 0;
        items.clear();
        itemRef.removeEventListener(valueEventListener);
    }

    private void recuperarItens(){
        dialog = new SpotsDialog.Builder()
                .setContext(getActivity())
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
                if(items.size() > 0){
                    txtNoneProduto.setVisibility(View.GONE);
                }else{
                    txtNoneProduto.setVisibility(View.VISIBLE);
                    txtTotal.setVisibility(View.GONE);
                    txtPrecoTotal.setVisibility(View.GONE);
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

    public void swipe(){
        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                itemSelecionado = items.get(viewHolder.getAdapterPosition());
                remover();
            }
        };

        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerView);

    }

    private void editar(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Editar a quantidade do produto");
        alertDialog.setCancelable(true);

        View viewQtd = getLayoutInflater().inflate(R.layout.dialog_qtd, null);
        final LinearLayout linearLayout = viewQtd.findViewById(R.id.linearView);
        final TextInputEditText txtQtd = viewQtd.findViewById(R.id.editQtd);
        final CurrencyEditText editValorUnitario = viewQtd.findViewById(R.id.editValorUnitario);
        final CurrencyEditText editValorTotal = viewQtd.findViewById(R.id.editValorTotal);
        final CurrencyEditText editValorDesconto = viewQtd.findViewById(R.id.editValorDesconto);

        editValorUnitario.setText(itemSelecionado.getValorUnitario());
        editValorTotal.setText(itemSelecionado.getValorTotal());
        editValorDesconto.setText(itemSelecionado.getValorDesconto());

        txtQtd.setText(itemSelecionado.getQtd());
        alertDialog.setView(viewQtd);

        if(orcamentoSelecionado.getStatus().equals("PENDENTE")){
            linearLayout.setVisibility(View.GONE);
            alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, final int i) {
                    if(!txtQtd.getText().toString().equals("") && txtQtd.getText().toString() != null && !txtQtd.getText().toString().equals(itemSelecionado.getQtd())){
                        //Calcular o valor total
                        int valorDesconto = Integer.parseInt(itemSelecionado.getValorDesconto());
                        int valorUnitario = Integer.parseInt(itemSelecionado.getValorUnitario());
                        int qtd = Integer.parseInt(txtQtd.getText().toString());
                        int vTotal;
                        if(qtd != 0){
                            if(valorDesconto == 0){
                                vTotal = valorUnitario * qtd;
                            }else{
                                vTotal = valorDesconto * qtd;
                            }
                            //Salvar registros
                            itemSelecionado.setQtd(qtd + "");
                            itemSelecionado.setValorTotal(vTotal + "");
                            itemSelecionado.salvar();

                            valorTotal = 0;
                            items.clear();
                            adapterItem.notifyDataSetChanged();
                        }else{
                            exibitMensagem("Digite um valor inteiro!");
                        }
                    }else{
                        exibitMensagem("Preencha o campo quantidade");
                    }
                }
            });
            alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    adapterItem.notifyDataSetChanged();
                }
            });
        }else{
            txtQtd.setEnabled(false);
        }
        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    private void remover(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Excluir");
        alertDialog.setMessage("Tem certeza que deseja excluir esse produto?");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(orcamentoSelecionado.getStatus().equals("PENDENTE")){
                    itemSelecionado.deletar();
                    valorTotal = 0;
                    items.clear();
                }
                adapterItem.notifyDataSetChanged();
            }
        });
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                adapterItem.notifyDataSetChanged();
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    private void exibitMensagem(String msg){
        Toast.makeText(
                getActivity(),
                msg,
                Toast.LENGTH_SHORT
        ).show();
    }

    public void excluirOrcamento(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
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
                getActivity().finish();
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
