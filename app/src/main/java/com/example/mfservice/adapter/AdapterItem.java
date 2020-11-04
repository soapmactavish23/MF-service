package com.example.mfservice.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

        String valor1 = produtoOrcamento.getProduto().getPrecoVenda().replaceAll("[^0-9]", "");
        int valor = Integer.parseInt(valor1) * Integer.parseInt(produtoOrcamento.getQtd());

        StringBuilder stringBuilder = new StringBuilder(valor + "");
        stringBuilder.insert(Integer.toString(valor).length() - 2, ",");

        if(Integer.parseInt(produtoOrcamento.getQtd()) > 1){
            holder.txtTitulo.setText(produtoOrcamento.getProduto().getTitulo() + " (x " + produtoOrcamento.getQtd() + ")");
        }else{
            holder.txtTitulo.setText(produtoOrcamento.getProduto().getTitulo());
        }
        if(produtoOrcamento.getStatus().equals("FINALIZADO") || UsuarioFirebase.getUsuarioLogado().getTipo_usuario().equals("ADM")){
           holder.txtPreco.setText("R$ "+ stringBuilder);
        }else{
            holder.txtPreco.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return produtoOrcamentos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView txtTitulo, txtPreco;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            txtPreco = itemView.findViewById(R.id.txtPreco);
        }
    }

}
