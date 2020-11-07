package com.example.mfservice.model;

import com.example.mfservice.config.ConfiguracaoFirebase;
import com.example.mfservice.config.UsuarioFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProdutoOrcamento implements Serializable {
    private Usuario cliente;
    private String formaPagamento;
    private String status;
    private String prazoEntrega;
    private String validade;
    private String obs;
    private String precoFinal;

    public ProdutoOrcamento() {
    }

    public void salvar(){
        DatabaseReference produtoOrcamentoRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("produtoOrcamento")
                .child(getCliente().getId());
        produtoOrcamentoRef.setValue(this);
    }

    public void deletar(){
        DatabaseReference produtoOrcamentoRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("produtoOrcamento")
                .child(getCliente().getId());
        produtoOrcamentoRef.removeValue();
    }

    public void atualizar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference produtosRef = firebaseRef
                .child("produtoOrcamento")
                .child(getCliente().getId());
        Map<String, Object> valoresProdutosOrcamento = converterParaMap();
        produtosRef.updateChildren(valoresProdutosOrcamento);
    }

    public Map<String, Object> converterParaMap(){
        HashMap<String, Object> produtoOrcamentoMap = new HashMap<>();
        produtoOrcamentoMap.put("cliente", getCliente());
        produtoOrcamentoMap.put("status", getStatus());
        produtoOrcamentoMap.put("formaPagamento", getFormaPagamento());
        produtoOrcamentoMap.put("prazoEntrega", getPrazoEntrega());
        produtoOrcamentoMap.put("validade", getValidade());
        produtoOrcamentoMap.put("obs", getObs());
        return produtoOrcamentoMap;
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

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public String getPrazoEntrega() {
        return prazoEntrega;
    }

    public void setPrazoEntrega(String prazoEntrega) {
        this.prazoEntrega = prazoEntrega;
    }

    public String getValidade() {
        return validade;
    }

    public void setValidade(String validade) {
        this.validade = validade;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public String getPrecoFinal() {
        return precoFinal;
    }

    public void setPrecoFinal(String precoFinal) {
        this.precoFinal = precoFinal;
    }
}