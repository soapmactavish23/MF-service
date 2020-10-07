package com.example.mrservice.model;

import com.example.mrservice.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

public class ServicoOrcamento implements Serializable {

    private String id;
    private String titulo;
    private String descricao;
    private Usuario cliente;

    public ServicoOrcamento() {
        DatabaseReference servicosRef = ConfiguracaoFirebase.getFirebaseDatabase().child("servicoOrcamento")
                .child(getCliente().getId());
        setId(servicosRef.push().getKey());
    }

    public Boolean salvar(){
        DatabaseReference servicosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("servicosOrcamento")
                .child(getCliente().getId())
                .child(getId());
        servicosRef.setValue(this);
        return true;
    }

    public void deletar(){
        DatabaseReference servicosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("servicosOrcamento")
                .child(getCliente().getId())
                .child(getId());
        servicosRef.removeValue();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Usuario getCliente() {
        return cliente;
    }

    public void setCliente(Usuario cliente) {
        this.cliente = cliente;
    }
}
