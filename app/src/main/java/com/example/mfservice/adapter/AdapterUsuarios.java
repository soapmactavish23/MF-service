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
import com.example.mfservice.model.ServicoOrcamento;
import com.example.mfservice.model.Usuario;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterUsuarios extends RecyclerView.Adapter<AdapterUsuarios.MyViewHolder> {
    private List<ServicoOrcamento> orcamentosServicos;
    private Context context;

    public AdapterUsuarios(List<ServicoOrcamento> orcamentosServicos, Context context) {
        this.orcamentosServicos = orcamentosServicos;
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
        ServicoOrcamento servicoOrcamento = orcamentosServicos.get(position);
        holder.txtNome.setText(servicoOrcamento.getCliente().getNome());
        holder.txtDepoimento.setText(servicoOrcamento.getStatus());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.padrao);
        Glide.with(context).applyDefaultRequestOptions(requestOptions)
                .load(servicoOrcamento.getCliente().getFoto())
                .into(holder.foto);

    }

    @Override
    public int getItemCount() {
        return orcamentosServicos.size();
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
