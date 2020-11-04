package com.example.mfservice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.example.mfservice.R;
import com.example.mfservice.model.Produto;
import com.example.mfservice.model.ProdutoOrcamento;

import java.util.List;

public class AdapterOrcamento extends RecyclerView.Adapter<AdapterOrcamento.MyViewHolder> {

    private Context context;
    private List<Produto> produtoList;

    public AdapterOrcamento(Context context, List<Produto> produtoList) {
        this.context = context;
        this.produtoList = produtoList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_descricao, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Produto produto = produtoList.get(position);
        holder.txtProduto.setText(produto.getTitulo());
        holder.txtPreco.setText(produto.getPrecoVenda());
    }

    @Override
    public int getItemCount() {
        return produtoList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView txtProduto;
        CurrencyEditText txtPreco;
        public MyViewHolder(View itemView){
            super(itemView);
            txtProduto = itemView.findViewById(R.id.txtNomeProduto);
            txtPreco = itemView.findViewById(R.id.txtPreco);
        }
    }
}
