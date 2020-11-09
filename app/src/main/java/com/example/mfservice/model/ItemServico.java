package com.example.mfservice.model;

import com.example.mfservice.config.ConfiguracaoFirebase;
import com.example.mfservice.config.UsuarioFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

public class ItemServico implements Serializable {

    private String idCliente;
    private String titulo;
    private String status;
    private String descricao;

    public ItemServico() {
        setIdCliente(UsuarioFirebase.getIdentificadorUsuario());
    }

    public void salvar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        firebaseRef.child("itensServico").child(getIdCliente()).child(getTitulo()).setValue(this);
    }

    public void deletar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        firebaseRef.child("itensServico").child(getIdCliente()).child(getTitulo()).removeValue();
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
