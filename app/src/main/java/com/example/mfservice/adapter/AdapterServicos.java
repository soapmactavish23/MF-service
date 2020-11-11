package com.example.mfservice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mfservice.R;
import com.example.mfservice.model.ItemServico;
import com.example.mfservice.model.ServicoOrcamento;

import java.util.List;

public class AdapterServicos extends RecyclerView.Adapter<AdapterServicos.MyViewHolder> {

    private Context context;
    private List<ItemServico> listaItem;

    public AdapterServicos(Context context, List<ItemServico> listaServicoOrcamentos) {
        this.context = context;
        this.listaItem = listaServicoOrcamentos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_servicos, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ItemServico item = listaItem.get(position);
        holder.txtTitulo.setText(item.getTitulo());
        holder.txtDescricao.setText(item.getDescricao());
    }

    @Override
    public int getItemCount() {
        return listaItem.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView txtTitulo, txtDescricao;
        public MyViewHolder(View itemView){
            super(itemView);
            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            txtDescricao = itemView.findViewById(R.id.txtDescricao);
        }
    }
}
