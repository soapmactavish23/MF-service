package com.example.mrservice.model;

import com.example.mrservice.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProdutoOrcamento implements Serializable {

    private String id;
    private Usuario cliente;
    private List<Produto> listaProdutos;
    private String status;

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
        produtoOrcamentoMap.put("cliente", getCliente());
        produtoOrcamentoMap.put("id", getId());
        produtoOrcamentoMap.put("listaProdutos", getListaProdutos());
        produtoOrcamentoMap.put("status", getStatus());
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Produto> getListaProdutos() {
        return listaProdutos;
    }

    public void setListaProdutos(List<Produto> listaProdutos) {
        this.listaProdutos = listaProdutos;
    }
}
