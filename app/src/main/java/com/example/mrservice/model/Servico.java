package com.example.mrservice.model;

import com.example.mrservice.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

public class Servico implements Serializable {

    private String id;
    private String titulo;
    private String categoria;

    public Servico() {
        DatabaseReference servicosRef = ConfiguracaoFirebase.getFirebaseDatabase().child("servicos");
        setId(servicosRef.push().getKey());
    }

    public Boolean salvar(){
        DatabaseReference servicosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("servicos")
                .child(getCategoria())
                .child(getId());
        servicosRef.setValue(this);
        return true;
    }

    public void deletar(){
        DatabaseReference servicosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("servicos")
                .child(getCategoria())
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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}
