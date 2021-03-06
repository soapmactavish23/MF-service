package com.example.mfservice.model;

import com.example.mfservice.config.ConfiguracaoFirebase;
import com.example.mfservice.config.UsuarioFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProdutoOrcamento implements Serializable {
    private String idCliente;
    private String nomeCliente;
    private String fotoCliente;
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
                .child(getIdCliente());
        produtoOrcamentoRef.setValue(this);
    }

    public void deletar(){
        DatabaseReference produtoOrcamentoRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("produtoOrcamento")
                .child(getIdCliente());
        produtoOrcamentoRef.removeValue();
    }

    public void atualizar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference produtosRef = firebaseRef
                .child("produtoOrcamento")
                .child(getIdCliente());
        Map<String, Object> valoresProdutosOrcamento = converterParaMap();
        produtosRef.updateChildren(valoresProdutosOrcamento);
    }

    public Map<String, Object> converterParaMap(){
        HashMap<String, Object> produtoOrcamentoMap = new HashMap<>();
        produtoOrcamentoMap.put("idCliente", getIdCliente());
        produtoOrcamentoMap.put("nomeCliente", getNomeCliente());
        produtoOrcamentoMap.put("fotoCliente", getFotoCliente());
        produtoOrcamentoMap.put("status", getStatus());
        produtoOrcamentoMap.put("formaPagamento", getFormaPagamento());
        produtoOrcamentoMap.put("prazoEntrega", getPrazoEntrega());
        produtoOrcamentoMap.put("validade", getValidade());
        produtoOrcamentoMap.put("obs", getObs());
        return produtoOrcamentoMap;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
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

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getFotoCliente() {
        return fotoCliente;
    }

    public void setFotoCliente(String fotoCliente) {
        this.fotoCliente = fotoCliente;
    }
}