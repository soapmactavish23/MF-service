package com.example.mfservice.model;

import com.example.mfservice.config.ConfiguracaoFirebase;
import com.example.mfservice.config.UsuarioFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

public class ItemServico implements Serializable {
    private String id;
    private String idCliente;
    private String idItem;
    private String titulo;
    private String descricao;

    public ItemServico() {
        setIdCliente(UsuarioFirebase.getIdentificadorUsuario());
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        setId(firebaseRef.child("mensagens").push().getKey());
    }

    public void salvar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        firebaseRef.child("itensServico").child(getIdCliente()).child(getTitulo()).setValue(this);
    }

    public void deletar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        firebaseRef.child("itensServico").child(getIdCliente()).child(getTitulo()).removeValue();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
