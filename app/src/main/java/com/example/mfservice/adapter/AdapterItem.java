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
import com.example.mfservice.model.Item;
import com.example.mfservice.model.Produto;
import com.example.mfservice.model.ProdutoOrcamento;
import com.example.mfservice.model.Usuario;

import java.util.List;

public class AdapterItem extends RecyclerView.Adapter<AdapterItem.MyViewHolder> {

    private Context context;
    private List<Item> items;
    private ProdutoOrcamento produtoOrcamento;
    private String tipoUsuario;

    public AdapterItem(Context context, List<Item> items, ProdutoOrcamento produtoOrcamento, String tipoUsuario) {
        this.context = context;
        this.items = items;
        this.produtoOrcamento = produtoOrcamento;
        this.tipoUsuario = tipoUsuario;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.produto_orcamento, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Item item = items.get(position);
        holder.txtTitulo.setText(item.getProduto() + "(x" + item.getQtd() + ")");
        if(produtoOrcamento.getStatus().equals("FINALIZADO") || tipoUsuario.equals("ADM")){
            holder.txtPreco.setText(item.getValorTotal());
        }else{
            holder.txtPreco.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
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
