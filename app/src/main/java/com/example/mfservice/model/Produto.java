package com.example.mfservice.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.mfservice.config.ConfiguracaoFirebase;
import com.example.mfservice.config.UsuarioFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Produto implements Serializable {
    private String id;
    private String titulo;
    private String descricao;
    private String precoVenda;
    private String categoria;
    private String produto;
    private String linha;
    private List<String> fotos;

    public Produto() {
        DatabaseReference produtoRef = ConfiguracaoFirebase.getFirebaseDatabase().child("produtos");
        setId(produtoRef.push().getKey());
    }

    public void salvar(){
        DatabaseReference produtoRef = ConfiguracaoFirebase.getFirebaseDatabase().child("produtos");
        produtoRef.child(getCategoria())
                .child(getProduto())
                .child(getId())
                .setValue(this);
    }

    public void deletar(){
        DatabaseReference produtoRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("produtos")
                .child(getCategoria())
                .child(getProduto())
                .child(getId());
        produtoRef.removeValue();
    }

    public void atualizar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference produtosRef = firebaseRef
                .child("produtos")
                .child(getCategoria())
                .child(getProduto())
                .child(getId());
        Map<String, Object> valoresProdutos = converterParaMap();
        produtosRef.updateChildren(valoresProdutos);
    }

    public Map<String, Object> converterParaMap(){
        HashMap<String, Object> produtoMap = new HashMap<>();
        produtoMap.put("titulo", getTitulo());
        produtoMap.put("descricao", getDescricao());
        produtoMap.put("id", getId());
        produtoMap.put("precoVenda", getPrecoVenda());
        produtoMap.put("fotos", getFotos());
        produtoMap.put("categoria", getCategoria());
        produtoMap.put("linha", getLinha());
        produtoMap.put("produto", getProduto());
        return produtoMap;
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

    public String getLinha() {
        return linha;
    }

    public void setLinha(String linha) {
        this.linha = linha;
    }

    public List<String> getFotos() {
        return fotos;
    }

    public void setFotos(List<String> fotos) {
        this.fotos = fotos;
    }
}
