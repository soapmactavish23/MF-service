package com.example.mfservice.adapter;

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
import com.example.mfservice.R;
import com.example.mfservice.model.TrabalhosFeitos;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;

import java.util.List;

public class AdapterTrabalhosFeitos extends RecyclerView.Adapter<AdapterTrabalhosFeitos.MyViewHolder> {

    private List<TrabalhosFeitos> trabalhosFeitosList;
    private Context context;

    public AdapterTrabalhosFeitos(List<TrabalhosFeitos> trabalhosFeitosList, Context context) {
        this.trabalhosFeitosList = trabalhosFeitosList;
        this.context = context;
    }

    public List<TrabalhosFeitos> getTrabalhosFeitosList() {
        return trabalhosFeitosList;
    }

    public void setTrabalhosFeitosList(List<TrabalhosFeitos> trabalhosFeitosList) {
        this.trabalhosFeitosList = trabalhosFeitosList;
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
        /*List<String> urlFotos = trabalhosFeitos.getFotos();
        String urlCapa = urlFotos.get(1);*/
        String urlCapa = trabalhosFeitos.getFotoDepois();

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.galery_padrao);
        requestOptions.fitCenter();
        Glide.with(context).load(urlCapa).apply(requestOptions).into(holder.fotoTrabalhoFeito);
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
