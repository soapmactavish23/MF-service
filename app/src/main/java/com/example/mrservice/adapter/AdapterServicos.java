package com.example.mrservice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mrservice.R;
import com.example.mrservice.model.Servico;

import java.util.List;

public class AdapterServicos extends RecyclerView.Adapter<AdapterServicos.MyViewHolder> {

    private Context context;
    private List<Servico> listaServicos;

    public AdapterServicos(Context context, List<Servico> listaServicos) {
        this.context = context;
        this.listaServicos = listaServicos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_servicos, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Servico servico = listaServicos.get(position);
        holder.checkBox.setText(servico.getTitulo());

    }


    public void onCheckboxClicked(View view){

    }

    @Override
    public int getItemCount() {
        return listaServicos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        CheckBox checkBox;

        public MyViewHolder(View itemView){
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkServico);
        }
    }
}
