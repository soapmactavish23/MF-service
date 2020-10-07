package com.example.mrservice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mrservice.R;
import com.example.mrservice.model.Produto;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterProdutos extends RecyclerView.Adapter<AdapterProdutos.MyViewHolder> {

    private List<Produto> produtos;
    private Context context;

    public AdapterProdutos(List<Produto> produtos, Context context) {
        this.produtos = produtos;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_nome_categoria_tipo_produto, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Produto produto = produtos.get(position);
        holder.titulo.setText(produto.getTitulo());
        holder.categoria.setText(produto.getCategoria());
        holder.produto.setText(produto.getProduto());

        //Pega a primira imagem da lista
        List<String> urlFotos = produto.getFotos();
        String urlCapa = urlFotos.get(0);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.padrao);
        requestOptions.fitCenter();
        Glide.with(context).load(urlCapa).apply(requestOptions).into(holder.fotoProduto);


    }

    @Override
    public int getItemCount() {
        return produtos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView titulo;
        TextView categoria;
        TextView produto;
        ImageView fotoProduto;

        public MyViewHolder(View itemView){
            super(itemView);
            titulo = itemView.findViewById(R.id.txtTitulo);
            categoria = itemView.findViewById(R.id.txtCategoria);
            produto = itemView.findViewById(R.id.txtTipoProduto);
            fotoProduto = itemView.findViewById(R.id.imgProduto);
        }

    }

}
