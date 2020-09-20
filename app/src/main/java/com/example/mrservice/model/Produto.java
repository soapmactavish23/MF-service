package com.example.mrservice.model;

import com.example.mrservice.config.ConfiguracaoFirebase;
import com.example.mrservice.config.UsuarioFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.List;

public class Produto implements Serializable {
    private String id;
    private String titulo;
    private String descricao;
    private String precoVenda;
    private String precoCusto;
    private String categoria;
    private String produto;
    private List<String> fotos;

    public Produto() {
        DatabaseReference produtoRef = ConfiguracaoFirebase.getFirebaseDatabase().child("produtos");
        setId(produtoRef.push().getKey());
    }

    public void salvar(){
        DatabaseReference produtoRef = ConfiguracaoFirebase.getFirebaseDatabase().child("produtos");
        produtoRef.child(UsuarioFirebase.getIdentificadorUsuario())
                .child(getId())
                .setValue(this);
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

    public String getPrecoVenda() {
        return precoVenda;
    }

    public void setPrecoVenda(String precoVenda) {
        this.precoVenda = precoVenda;
    }

    public String getPrecoCusto() {
        return precoCusto;
    }

    public void setPrecoCusto(String precoCusto) {
        this.precoCusto = precoCusto;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public List<String> getFotos() {
        return fotos;
    }

    public void setFotos(List<String> fotos) {
        this.fotos = fotos;
    }
}
