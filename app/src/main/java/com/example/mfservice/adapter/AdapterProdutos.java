package com.example.mfservice.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mfservice.R;
import com.example.mfservice.model.Produto;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterProdutos extends RecyclerView.Adapter<AdapterProdutos.MyViewHolder> {

    private List<Produto> produtos;
    private String tipoUsuario;
    private Context context;

    public AdapterProdutos(List<Produto> produtos, String tipoUsuario, Context context) {
        this.produtos = produtos;
        this.tipoUsuario = tipoUsuario;
        this.context = context;
    }

    public List<Produto> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<Produto> produtos) {
        this.produtos = produtos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_nome_categoria_tipo_produto, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        try{
            Produto produto = produtos.get(position);
            holder.titulo.setText(produto.getTitulo());
            holder.categoria.setText(produto.getCategoria());
            if(produto.getLinha().equals("")){
                holder.produto.setText(produto.getProduto());
            }else{
                holder.produto.setText(produto.getLinha());
            }
            if(tipoUsuario.equals("ADM")){
                holder.preco.setText( produto.getPrecoVenda());
            }else{
                holder.preco.setVisibility(View.GONE);
            }
            //Pega a primira imagem da lista
            List<String> urlFotos = produto.getFotos();
            String urlCapa = urlFotos.get(0);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.galery_padrao);
            requestOptions.fitCenter();
            Glide.with(context).load(urlCapa).apply(requestOptions).into(holder.fotoProduto);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return produtos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView titulo;
        TextView categoria;
        TextView produto;
        TextView preco;
        ImageView fotoProduto;

        public MyViewHolder(View itemView){
            super(itemView);
            titulo = itemView.findViewById(R.id.txtTitulo);
            categoria = itemView.findViewById(R.id.txtCategoria);
            produto = itemView.findViewById(R.id.txtTipoProduto);
            preco = itemView.findViewById(R.id.txtPreco);
            fotoProduto = itemView.findViewById(R.id.imgProduto);
        }

    }

}
