package com.example.mfservice.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.mfservice.R;
import com.example.mfservice.activity.ProdutoOrcamentoActivity;
import com.example.mfservice.adapter.AdapterClientes;
import com.example.mfservice.adapter.AdapterOrcamentoProduto;
import com.example.mfservice.adapter.AdapterProdutos;
import com.example.mfservice.adapter.AdapterUsuarios;
import com.example.mfservice.config.ConfiguracaoFirebase;
import com.example.mfservice.helper.RecyclerItemClickListener;
import com.example.mfservice.model.Produto;
import com.example.mfservice.model.ProdutoOrcamento;
import com.example.mfservice.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProdutosFragmentAdm extends Fragment {

    private RecyclerView recyclerView;
    private List<ProdutoOrcamento> orcamentos = new ArrayList<>();
    private List<Usuario> clientes = new ArrayList<>();
    private DatabaseReference produtosOrcamentosRef;
    private ValueEventListener valueEventListener;
    private AdapterOrcamentoProduto adapterOrcamentoProduto;
    private TextView txtNone;

    public ProdutosFragmentAdm() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_produtos_adm, container, false);

        //Inicializar Componentes
        txtNone = view.findViewById(R.id.txtNoneProduto);
        recyclerView = view.findViewById(R.id.recyclerProdutosOrcamentos);
        produtosOrcamentosRef = ConfiguracaoFirebase.getFirebaseDatabase().child("produtoOrcamento");

        //Configurar o RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        adapterOrcamentoProduto = new AdapterOrcamentoProduto(getActivity(), orcamentos, clientes);
        recyclerView.setAdapter(adapterOrcamentoProduto);

        //Toque do Recycler
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(),
                recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        ProdutoOrcamento produtoOrcamento = orcamentos.get(position);
                        Intent intent = new Intent(getActivity(), ProdutoOrcamentoActivity.class);
                        intent.putExtra("orcamento", produtoOrcamento);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, final int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));

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
    }

    private void recuperarOrcamentos(){
        orcamentos.clear();
        valueEventListener = produtosOrcamentosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    ProdutoOrcamento produtoOrcamento = ds.getValue(ProdutoOrcamento.class);
                    recuperarClientes(produtoOrcamento.getIdCliente());
                    orcamentos.add(produtoOrcamento);
                }
                if(orcamentos.size() > 0){
                    txtNone.setVisibility(View.GONE);
                }
                adapterOrcamentoProduto.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void recuperarClientes(String id){

    }

    private void recuperarPorStatus(String s){
        List<ProdutoOrcamento> listOrcamentoStatus = new ArrayList<>();
        for(ProdutoOrcamento orcamentoStatus: orcamentos){
            String status = orcamentoStatus.getStatus();
            if(status.contains(s)){
                listOrcamentoStatus.add(orcamentoStatus);
            }
        }
        adapterOrcamentoProduto = new AdapterOrcamentoProduto(getActivity(), listOrcamentoStatus, clientes);
        recyclerView.setAdapter(adapterOrcamentoProduto);
        adapterOrcamentoProduto.notifyDataSetChanged();
    }

    public void pesquisarOrcamentos(String texto){
        List<ProdutoOrcamento> listOrcamentoBusca = new ArrayList<>();
        for(ProdutoOrcamento orcamento : orcamentos){
            /*String nome = orcamento.getIdCliente().getNome().toLowerCase();
            if(nome.contains(texto)){
                listOrcamentoBusca.add(orcamento);
            }*/
        }
        adapterOrcamentoProduto = new AdapterOrcamentoProduto(getActivity(), listOrcamentoBusca, clientes);
        recyclerView.setAdapter(adapterOrcamentoProduto);
        adapterOrcamentoProduto.notifyDataSetChanged();
    }

    public void recarregarOrcamentos(){
        adapterOrcamentoProduto = new AdapterOrcamentoProduto(getActivity(), orcamentos, clientes);
        recyclerView.setAdapter(adapterOrcamentoProduto);
        adapterOrcamentoProduto.notifyDataSetChanged();
    }

    public void selectStatus(){
        AlertDialog.Builder dialogCategoria = new AlertDialog.Builder(getActivity());
        dialogCategoria.setTitle("Escolher Status");

        //Configurar spinner
        View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);
        final Spinner spinner = viewSpinner.findViewById(R.id.spinnerFiltro);

        final String[] linha = getResources().getStringArray(R.array.status);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_spinner_item, linha
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        dialogCategoria.setView(viewSpinner);

        dialogCategoria.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String s = spinner.getSelectedItem().toString();
                if(s.equals("TODOS")){
                    recuperarPorStatus("");
                }else{
                    recuperarPorStatus(s);
                }
            }
        });
        dialogCategoria.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = dialogCategoria.create();
        dialog.show();
    }

}