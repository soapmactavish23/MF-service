package com.example.mfservice.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.example.mfservice.R;
import com.example.mfservice.config.UsuarioFirebase;
import com.example.mfservice.model.Produto;
import com.example.mfservice.model.ProdutoOrcamento;
import com.example.mfservice.model.Usuario;

import java.util.List;

public class AdapterItem extends RecyclerView.Adapter<AdapterItem.MyViewHolder> {

    private Context context;
    private List<ProdutoOrcamento> produtoOrcamentos;

    public AdapterItem(Context context, List<ProdutoOrcamento> produtoOrcamentos) {
        this.context = context;
        this.produtoOrcamentos = produtoOrcamentos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.produto_orcamento, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ProdutoOrcamento produtoOrcamento = produtoOrcamentos.get(position);

        if(Integer.parseInt(produtoOrcamento.getQtd()) > 1){
            holder.txtTitulo.setText(produtoOrcamento.getProduto().getTitulo() + " (x " + produtoOrcamento.getQtd() + ")");
        }else{
            holder.txtTitulo.setText(produtoOrcamento.getProduto().getTitulo());
        }
        if(produtoOrcamento.getStatus().equals("FINALIZADO")){
           holder.txtPreco.setText(produtoOrcamento.getProduto().getPrecoVenda());
        }else{
            holder.txtPreco.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return produtoOrcamentos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView txtTitulo;
        CurrencyEditText txtPreco;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            txtPreco = itemView.findViewById(R.id.txtPreco);
        }
    }

}
