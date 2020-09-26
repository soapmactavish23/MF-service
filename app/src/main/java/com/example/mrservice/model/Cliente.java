package com.example.mrservice.model;

import com.example.mrservice.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

public class Cliente implements Serializable {

    private String id;
    private String nome;
    private String foto;
    private String categoria;

    public Cliente() {
        DatabaseReference clienteRef = ConfiguracaoFirebase.getFirebaseDatabase().child("clientes");
        setId(clienteRef.push().getKey());
    }

    public void salvar(){
        DatabaseReference clienteRef = ConfiguracaoFirebase.getFirebaseDatabase().child("clientes");
        clienteRef.child(getCategoria()).child(getId()).setValue(this);
    }

    public void deletar(){
        DatabaseReference clienteRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("clientes")
                .child(getCategoria())
                .child(getId());
        clienteRef.removeValue();

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}
