package com.example.mfservice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mfservice.R;
import com.example.mfservice.model.ServicoOrcamento;

import java.util.List;

public class AdapterServicos extends RecyclerView.Adapter<AdapterServicos.MyViewHolder> {

    private Context context;
    private List<ServicoOrcamento> listaServicoOrcamentos;

    public AdapterServicos(Context context, List<ServicoOrcamento> listaServicoOrcamentos) {
        this.context = context;
        this.listaServicoOrcamentos = listaServicoOrcamentos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_servicos, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ServicoOrcamento servicoOrcamento = listaServicoOrcamentos.get(position);
        holder.checkBox.setText(servicoOrcamento.getTitulo());

    }


    public void onCheckboxClicked(View view){

    }

    @Override
    public int getItemCount() {
        return listaServicoOrcamentos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        CheckBox checkBox;

        public MyViewHolder(View itemView){
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkServico);
        }
    }
}
