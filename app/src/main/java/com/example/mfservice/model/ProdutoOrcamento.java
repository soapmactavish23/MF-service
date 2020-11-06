package com.example.mfservice.model;

import com.example.mfservice.config.ConfiguracaoFirebase;
import com.example.mfservice.config.UsuarioFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProdutoOrcamento implements Serializable {

    private String id;
    private Usuario cliente;
    private Produto produto;
    private String qtd;
    private String status;

    public ProdutoOrcamento() {
        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("produtoOrcamento");
        setId(databaseReference.push().getKey());
    }

    public void salvar(){
        DatabaseReference produtoOrcamentoRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("produtoOrcamento")
                .child(getCliente().getId())
                .child(getProduto().getId());
        produtoOrcamentoRef.setValue(this);
    }

    public void deletar(){
        DatabaseReference produtoOrcamentoRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("produtoOrcamento")
                .child(getCliente().getId())
                .child(getProduto().getId());
        produtoOrcamentoRef.removeValue();
    }

    public void atualizar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference produtosRef = firebaseRef
                .child("produtoOrcamento")
                .child(getCliente().getId())
                .child(getProduto().getId());
        Map<String, Object> valoresProdutosOrcamento = converterParaMap();
        produtosRef.updateChildren(valoresProdutosOrcamento);
    }

    public Map<String, Object> converterParaMap(){
        HashMap<String, Object> produtoOrcamentoMap = new HashMap<>();
        produtoOrcamentoMap.put("cliente", getCliente());
        produtoOrcamentoMap.put("id", getId());
        produtoOrcamentoMap.put("produto", getProduto());
        produtoOrcamentoMap.put("qtd", getQtd());
        return produtoOrcamentoMap;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public Usuario getCliente() {
        return cliente;
    }

    public void setCliente(Usuario cliente) {
        this.cliente = cliente;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
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
}
