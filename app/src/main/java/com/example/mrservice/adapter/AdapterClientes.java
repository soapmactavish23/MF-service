package com.example.mrservice.adapter;

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
import com.example.mrservice.R;
import com.example.mrservice.model.Cliente;
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

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_foto_nome, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Cliente cliente = listaClientes.get(position);
        holder.titulo.setText(cliente.getNome());
        //Picasso.get().load(cliente.getFoto()).into(holder.foto);
        Uri uri = Uri.parse(cliente.getFoto());
        Glide.with(context).load(uri).into(holder.foto);

    }

    @Override
    public int getItemCount() {
        return listaClientes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView titulo;
        CircleImageView foto;
        public MyViewHolder(View itemView){
            super(itemView);
            foto = itemView.findViewById(R.id.img);
            titulo = itemView.findViewById(R.id.txtNome);
        }
    }

}
