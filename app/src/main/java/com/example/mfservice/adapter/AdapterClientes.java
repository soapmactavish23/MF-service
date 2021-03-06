package com.example.mfservice.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mfservice.R;
import com.example.mfservice.model.Cliente;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterClientes extends RecyclerView.Adapter<AdapterClientes.MyViewHolder> {

    private List<Cliente> listaClientes;
    private Context context;

    public AdapterClientes(List<Cliente> listaClientes, Context context) {
        this.listaClientes = listaClientes;
        this.context = context;
    }

    public List<Cliente> getListaClientes() {
        return listaClientes;
    }

    public void setListaClientes(List<Cliente> listaClientes) {
        this.listaClientes = listaClientes;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_nome_descricao, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Cliente cliente = listaClientes.get(position);
        holder.titulo.setText(cliente.getNome());
        holder.descricao.setText(cliente.getDepoimento());
        if(!cliente.getFoto().isEmpty()){
            Uri uri = Uri.parse(cliente.getFoto());
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.padrao);
            requestOptions.fitCenter();
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(uri).into(holder.foto);
        }else{
            holder.foto.setImageResource(R.drawable.padrao);
        }
    }

    @Override
    public int getItemCount() {
        return listaClientes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView titulo, descricao;
        CircleImageView foto;
        public MyViewHolder(View itemView){
            super(itemView);
            foto = itemView.findViewById(R.id.img);
            titulo = itemView.findViewById(R.id.txtNome);
            descricao = itemView.findViewById(R.id.txtDepoimento);
        }
    }

}
