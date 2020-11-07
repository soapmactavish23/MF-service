package com.example.mfservice.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mfservice.R;
import com.example.mfservice.model.ProdutoOrcamento;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterOrcamentoProduto extends RecyclerView.Adapter<AdapterOrcamentoProduto.MyViewHolder> {

    private Context context;
    private List<ProdutoOrcamento> listaOrcamento;

    public AdapterOrcamentoProduto(Context context, List<ProdutoOrcamento> listaOrcamento) {
        this.context = context;
        this.listaOrcamento = listaOrcamento;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_nome_categoria_tipo_produto, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ProdutoOrcamento produtoOrcamento = listaOrcamento.get(position);
        holder.txtTitulo.setText(produtoOrcamento.getCliente().getNome());
        holder.txtStatus.setText(produtoOrcamento.getStatus());
        holder.txtPreco.setVisibility(View.GONE);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.padrao);
        requestOptions.fitCenter();
        Glide.with(context).applyDefaultRequestOptions(requestOptions)
                .load(produtoOrcamento.getCliente().getFoto())
                .into(holder.foto);
    }

    @Override
    public int getItemCount() {
        return listaOrcamento.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        CircleImageView foto;
        TextView txtTitulo, txtStatus;
        CurrencyEditText txtPreco;
        public MyViewHolder(View itemView){
            super(itemView);
            foto = itemView.findViewById(R.id.imgProduto);
            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            txtStatus = itemView.findViewById(R.id.txtCategoria);
            txtPreco = itemView.findViewById(R.id.txtPreco);
        }
    }

}
