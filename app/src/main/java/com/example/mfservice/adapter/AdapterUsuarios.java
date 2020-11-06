package com.example.mfservice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mfservice.R;
import com.example.mfservice.model.ClientesSatisfeitos;
import com.example.mfservice.model.ProdutoOrcamento;
import com.example.mfservice.model.Usuario;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterUsuarios extends RecyclerView.Adapter<AdapterUsuarios.MyViewHolder> {
    private List<Usuario> clientes;
    private List<ProdutoOrcamento> orcamentos;
    private Context context;

    public AdapterUsuarios(List<Usuario> clientes, List<ProdutoOrcamento> orcamentos, Context context) {
        this.clientes = clientes;
        this.orcamentos = orcamentos;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_nome_descricao, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Usuario cliente = clientes.get(position);

        for(int i = 0; i < orcamentos.size(); i++){
            if(orcamentos.get(i).getStatus().equals("PENDENTE")){
                holder.txtDepoimento.setText("PENDENTE");
            }else{
                holder.txtDepoimento.setText("FINALIZADO");
            }
        }

        holder.txtNome.setText(cliente.getNome());

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.padrao);
        Glide.with(context).applyDefaultRequestOptions(requestOptions).load(cliente.getFoto()).into(holder.foto);

    }

    @Override
    public int getItemCount() {
        return clientes.size();
    }

    public class MyViewHolder  extends RecyclerView.ViewHolder {

        CircleImageView foto;
        TextView txtNome, txtDepoimento;

        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            foto = itemView.findViewById(R.id.img);
            txtNome = itemView.findViewById(R.id.txtNome);
            txtDepoimento = itemView.findViewById(R.id.txtDepoimento);
        }
    }
}
