package com.example.mrservice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mrservice.R;
import com.example.mrservice.model.ClientesSatisfeitos;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterClientesSatisfeitos extends RecyclerView.Adapter<AdapterClientesSatisfeitos.MyViewHolder> {
    private List<ClientesSatisfeitos> clientesSatisfeitosList;
    private Context context;

    public AdapterClientesSatisfeitos(List<ClientesSatisfeitos> clientesSatisfeitosList, Context context) {
        this.clientesSatisfeitosList = clientesSatisfeitosList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_clientes_satisfeitos, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ClientesSatisfeitos clientesSatisfeitos = clientesSatisfeitosList.get(position);

        holder.txtDepoimento.setText(clientesSatisfeitos.getDepoimento());
        holder.txtNomeClienteSatisfeito.setText(clientesSatisfeitos.getNomeCliente());
        Picasso.get().load(clientesSatisfeitos.getFoto()).into(holder.imgClienteSatisfeito);
    }

    @Override
    public int getItemCount() {
        return clientesSatisfeitosList.size();
    }

    public class MyViewHolder  extends RecyclerView.ViewHolder {

        CircleImageView imgClienteSatisfeito;
        TextView txtNomeClienteSatisfeito, txtDepoimento;

        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            imgClienteSatisfeito = itemView.findViewById(R.id.imgClienteSatisfeito);
            txtNomeClienteSatisfeito = itemView.findViewById(R.id.txtNomeClienteSatisfeito);
            txtDepoimento = itemView.findViewById(R.id.txtDepoimento);
        }
    }
}
