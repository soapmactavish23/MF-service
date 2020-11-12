package com.example.mfservice.model;

import com.example.mfservice.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

public class ServicoOrcamento implements Serializable {

    private String idCliente;
    private String fotoCliente;
    private String nomeCliente;
    private String status;

    public ServicoOrcamento() {
        setStatus("PENDENTE");
    }

    public void salvar(){
        DatabaseReference servicosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("servicoOrcamento")
                .child(getIdCliente());
        servicosRef.setValue(this);
    }

    public void deletar(){
        DatabaseReference servicosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("servicoOrcamento")
                .child(getIdCliente());
        servicosRef.removeValue();
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

    public String getFotoCliente() {
        return fotoCliente;
    }

    public void setFotoCliente(String fotoCliente) {
        this.fotoCliente = fotoCliente;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }
}
