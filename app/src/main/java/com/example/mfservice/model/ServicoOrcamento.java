package com.example.mfservice.model;

import com.example.mfservice.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

public class ServicoOrcamento implements Serializable {

    private String idCliente;
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
}
