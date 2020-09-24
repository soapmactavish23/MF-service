package com.example.mrservice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mrservice.R;
import com.example.mrservice.model.TrabalhosFeitos;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterTrabalhosFeitos extends RecyclerView.Adapter<AdapterTrabalhosFeitos.MyViewHolder> {

    private List<TrabalhosFeitos> trabalhosFeitosList;
    private Context context;

    public AdapterTrabalhosFeitos(List<TrabalhosFeitos> trabalhosFeitosList, Context context) {
        this.trabalhosFeitosList = trabalhosFeitosList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_trabalhos_feitos, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TrabalhosFeitos trabalhosFeitos = trabalhosFeitosList.get(position);

        holder.titulo.setText(trabalhosFeitos.getTitulo());
        holder.descricao.setText(trabalhosFeitos.getDescricao());

        //Pega a primira imagem da lista
        List<String> urlFotos = trabalhosFeitos.getFotos();
        String urlCapa = urlFotos.get(1);
        Picasso.get().load(urlCapa).into(holder.fotoTrabalhoFeito);
    }

    @Override
    public int getItemCount() {
        return trabalhosFeitosList.size();
    }

    public class MyViewHolder  extends RecyclerView.ViewHolder {

        TextView titulo;
        TextView descricao;
        ImageView fotoTrabalhoFeito;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.txtTitulo);
            descricao = itemView.findViewById(R.id.txtDescricao);
            fotoTrabalhoFeito = itemView.findViewById(R.id.imgTrabalhosFeitos);
        }
    }
}
