package com.example.mrservice.model;

import com.example.mrservice.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ProdutoOrcamento implements Serializable {

    private String id;
    private Produto produto;
    private Usuario cliente;
    private String qtd;
    private String status;
    private String precoFinal;

    public ProdutoOrcamento() {
        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("produtoOrcamento");
        setId(databaseReference.push().getKey());
        setStatus("PENDENTE");
    }

    public void salvar(){
        DatabaseReference produtoOrcamentoRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("produtoOrcamento")
                .child(getCliente().getId())
                .child(getId());
        produtoOrcamentoRef.setValue(this);
    }

    public void deletar(){
        DatabaseReference produtoOrcamentoRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("produtoOrcamento")
                .child(getCliente().getId())
                .child(getId());
        produtoOrcamentoRef.removeValue();
    }

    public void atualizar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference produtosRef = firebaseRef
                .child("produtoOrcamento")
                .child(getCliente().getId())
                .child(getId());
        Map<String, Object> valoresProdutosOrcamento = converterParaMap();
        produtosRef.updateChildren(valoresProdutosOrcamento);
    }

    public Map<String, Object> converterParaMap(){
        HashMap<String, Object> produtoOrcamentoMap = new HashMap<>();
        produtoOrcamentoMap.put("produto", getProduto());
        produtoOrcamentoMap.put("cliente", getCliente());
        produtoOrcamentoMap.put("id", getId());
        produtoOrcamentoMap.put("qtd", getQtd());
        produtoOrcamentoMap.put("status", getStatus());
        produtoOrcamentoMap.put("precoFinal", getPrecoFinal());
        return produtoOrcamentoMap;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Usuario getCliente() {
        return cliente;
    }

    public void setCliente(Usuario cliente) {
        this.cliente = cliente;
    }

    public String getQtd() {
        return qtd;
    }

    public void setQtd(String qtd) {
        this.qtd = qtd;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPrecoFinal() {
        return precoFinal;
    }

    public void setPrecoFinal(String precoFinal) {
        this.precoFinal = precoFinal;
    }
}
