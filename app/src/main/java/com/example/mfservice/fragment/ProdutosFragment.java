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
import android.widget.TextView;

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
import com.example.mfservice.model.Produto;
import com.example.mfservice.model.ProdutoOrcamento;
import com.example.mfservice.model.Usuario;
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
    private List<ProdutoOrcamento> listProdutoOrcamentos = new ArrayList<>();
    private DatabaseReference produtosOrcamentosRef;
    private DatabaseReference usuarioRef;
    private Usuario usuarioLogado;
    private ValueEventListener valueEventListener;
    private CircleImageView foto;
    private TextView txtNome, txtNoneProduto, txtTotal, txtEndereco, txtEmail, txtTelefone;
    private CurrencyEditText txtPrecoTotal;
    private ProdutoOrcamento produtoOrcamentoSelecionado;

    public ProdutosFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_produtos, container, false);

        //Inicializar Componentes
        foto = view.findViewById(R.id.imgFotoCliente);
        txtNome = view.findViewById(R.id.txtNomeCliente);
        txtPrecoTotal = view.findViewById(R.id.txtPrecoTotal);
        txtNoneProduto = view.findViewById(R.id.txtNoneProduto);
        txtEndereco = view.findViewById(R.id.txtEndereco);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtTelefone = view.findViewById(R.id.txtTelefone);
        txtTotal = view.findViewById(R.id.txtTotal);
        recyclerView = view.findViewById(R.id.recyclerProdutosOrcamentos);

        produtosOrcamentosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("produtoOrcamento")
                .child(UsuarioFirebase.getIdentificadorUsuario());

        Glide.with(getActivity()).load(UsuarioFirebase.getUsuarioLogado().getFoto()).into(foto);
        txtNome.setText(UsuarioFirebase.getUsuarioLogado().getNome());
        txtEmail.setText("E-mail: " + UsuarioFirebase.getUsuarioLogado().getEmail());

        usuarioRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("usuarios")
                .child(UsuarioFirebase.getIdentificadorUsuario());

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usuarioLogado = snapshot.getValue(Usuario.class);
                txtEndereco.setText("Endereço: " + usuarioLogado.getEndereco());
                txtTelefone.setText("Contato: " + usuarioLogado.getContato());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Configurar O RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        adapterItem = new AdapterItem(getActivity(), listProdutoOrcamentos);
        recyclerView.setAdapter(adapterItem);

        //Toque no Recycler
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(),
                recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        produtoOrcamentoSelecionado = listProdutoOrcamentos.get(position);
                        editar();
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        produtoOrcamentoSelecionado = listProdutoOrcamentos.get(position);
                        remover();
                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));
        swipe();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarOrcamentos();
    }

    @Override
    public void onStop() {
        super.onStop();
        produtosOrcamentosRef.removeEventListener(valueEventListener);
        listProdutoOrcamentos.clear();
    }

    public void recuperarOrcamentos(){
        valueEventListener = produtosOrcamentosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listProdutoOrcamentos.clear();
                int total = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    ProdutoOrcamento produtoOrcamento = ds.getValue(ProdutoOrcamento.class);
                    String valor1 = produtoOrcamento.getProduto().getPrecoVenda().replaceAll("[^0-9]", "");
                    int valor = Integer.parseInt(valor1) * Integer.parseInt(produtoOrcamento.getQtd());
                    if(produtoOrcamento.getStatus().equals("PENDENTE")){
                        total += 0;
                    }else{
                        total += valor;
                    }
                    listProdutoOrcamentos.add(produtoOrcamento);
                    if(listProdutoOrcamentos.size() > 0){
                        txtPrecoTotal.setVisibility(View.VISIBLE);
                        txtTotal.setVisibility(View.VISIBLE);
                        txtNoneProduto.setVisibility(View.GONE);
                    }
                }
                if(listProdutoOrcamentos.size() > 0){
                    if(listProdutoOrcamentos.get(0).getStatus().equals("PENDENTE")){
                        txtPrecoTotal.setText("");
                    }else{
                        txtPrecoTotal.setText(total + "");
                    }
                }else{
                    txtPrecoTotal.setVisibility(View.GONE);
                    txtTotal.setVisibility(View.GONE);
                    txtNoneProduto.setVisibility(View.VISIBLE);
                }
                adapterItem.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                produtoOrcamentoSelecionado = listProdutoOrcamentos.get(viewHolder.getAdapterPosition());
                remover();
            }
        };

        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerView);

    }

    private void editar(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Editar a quantidade do produto");
        alertDialog.setMessage("Digite a nova quantidade");
        alertDialog.setCancelable(true);

        View viewQtd = getLayoutInflater().inflate(R.layout.dialog_qtd, null);
        final TextInputEditText qtd = viewQtd.findViewById(R.id.editQtd);

        alertDialog.setView(viewQtd);

        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, final int i) {
                String qtdProdutos = qtd.getText().toString();
                produtoOrcamentoSelecionado.setQtd(qtdProdutos);
                produtoOrcamentoSelecionado.atualizar();
                recuperarOrcamentos();
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

    private void remover(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Excluir");
        alertDialog.setMessage("Tem certeza que deseja excluir esse produto?");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                produtoOrcamentoSelecionado.deletar();
                recuperarOrcamentos();
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

}
