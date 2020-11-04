package com.example.mfservice.model;

import com.example.mfservice.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

public class Item {
    private String id;
    private String nome;
    private String preco;
    private String qtd;

    public Item() {
        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();
        setId(databaseReference.child("itens").push().getKey());
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

    public String getPreco() {
        return preco;
    }

    public void setPreco(String preco) {
        this.preco = preco;
    }

    public String getQtd() {
        return qtd;
    }

    public void setQtd(String qtd) {
        this.qtd = qtd;
    }
}
