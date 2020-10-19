package com.example.mrservice.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mrservice.R;
import com.example.mrservice.model.ProdutoOrcamento;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterOrcamentoProduto extends RecyclerView.Adapter<AdapterOrcamentoProduto.MyViewHolder> {

    private Context context;
    private List<ProdutoOrcamento> listaOrcamento;
    private String tipoUsuario;

    public AdapterOrcamentoProduto(Context context, List<ProdutoOrcamento> listaOrcamento, String tipoUsuario) {
        this.context = context;
        this.listaOrcamento = listaOrcamento;
        this.tipoUsuario = tipoUsuario;
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
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.padrao);
        requestOptions.fitCenter();
        holder.txtTitulo.setText(produtoOrcamento.getCliente().getNome());
        Glide.with(context)
                .applyDefaultRequestOptions(requestOptions)
                .load(produtoOrcamento.getCliente().getFoto())
                .into(holder.foto);
        holder.txtStatus.setText(produtoOrcamento.getStatus());
    }

    @Override
    public int getItemCount() {
        return listaOrcamento.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        CircleImageView foto;
        TextView txtTitulo, txtStatus;
        public MyViewHolder(View itemView){
            super(itemView);
            foto = itemView.findViewById(R.id.imgProduto);
            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            txtStatus = itemView.findViewById(R.id.txtCategoria);
        }
    }

}
