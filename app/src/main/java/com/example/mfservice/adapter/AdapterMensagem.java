package com.example.mfservice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mfservice.R;
import com.example.mfservice.config.UsuarioFirebase;
import com.example.mfservice.model.Mensagem;
import com.example.mfservice.model.Usuario;

import java.util.List;

public class AdapterMensagem extends RecyclerView.Adapter<AdapterMensagem.MyViewHolder> {

    private List<Mensagem> listaMensagens;
    private Context context;
    private static final int TIPO_REMETENTE = 0;
    private static final int TIPO_DESTINATARIO = 1;

    public AdapterMensagem(List<Mensagem> listaMensagens, Context context) {
        this.listaMensagens = listaMensagens;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = null;
        if (viewType == TIPO_REMETENTE){
            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_mensagens_remetente, parent, false);
        }else if(viewType == TIPO_DESTINATARIO){
            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_mensagens_destinatario, parent, false);
        }
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Mensagem mensagem = listaMensagens.get(position);
        holder.txtMensagem.setText(mensagem.getMensagem());

    }

    @Override
    public int getItemCount() {
        return listaMensagens.size();
    }

    @Override
    public int getItemViewType(int position) {
        Mensagem mensagem = listaMensagens.get(position);
        String idUsuario = UsuarioFirebase.getIdentificadorUsuario();
        if(idUsuario.equals(mensagem.getIdRemetente())){
            return TIPO_REMETENTE;
        }
        return TIPO_DESTINATARIO;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private ImageView imgMensagem;
        private TextView txtMensagem;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMensagem = itemView.findViewById(R.id.txtMsg);
            imgMensagem = itemView.findViewById(R.id.imgMsgFoto);
        }
    }

}
