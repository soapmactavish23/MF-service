package com.example.mfservice.adapter;

import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mfservice.R;
import com.example.mfservice.config.ConfiguracaoFirebase;
import com.example.mfservice.model.ProdutoOrcamento;
import com.example.mfservice.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterOrcamentoProduto extends RecyclerView.Adapter<AdapterOrcamentoProduto.MyViewHolder> {

    private Context context;
    private List<ProdutoOrcamento> listaOrcamento;

    public AdapterOrcamentoProduto(Context context, List<ProdutoOrcamento> listaOrcamento) {
        this.context = context;
        this.listaOrcamento = listaOrcamento;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_nome_categoria_tipo_produto, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        ProdutoOrcamento produtoOrcamento = listaOrcamento.get(position);

        holder.txtStatus.setText(produtoOrcamento.getStatus());

        DatabaseReference usuarioRef = ConfiguracaoFirebase.getFirebaseDatabase().child("usuarios");
        usuarioRef.child(produtoOrcamento.getIdCliente()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario cliente = snapshot.getValue(Usuario.class);
                holder.txtTitulo.setText(cliente.getNome());
                holder.txtPreco.setVisibility(View.GONE);
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.padrao);
                requestOptions.fitCenter();
                Glide.with(context).applyDefaultRequestOptions(requestOptions)
                        .load(cliente.getFoto())
                        .into(holder.foto);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return listaOrcamento.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        CircleImageView foto;
        TextView txtTitulo, txtStatus;
        CurrencyEditText txtPreco;
        public MyViewHolder(View itemView){
            super(itemView);
            foto = itemView.findViewById(R.id.imgProduto);
            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            txtStatus = itemView.findViewById(R.id.txtCategoria);
            txtPreco = itemView.findViewById(R.id.txtPreco);
        }
    }

}
