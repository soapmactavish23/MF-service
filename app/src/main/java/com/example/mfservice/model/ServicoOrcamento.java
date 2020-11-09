package com.example.mfservice.model;

import com.example.mfservice.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

public class ServicoOrcamento implements Serializable {

    private Usuario cliente;
    private String status;

    public ServicoOrcamento() {
        setStatus("PENDENTE");
    }

    public void salvar(){
        DatabaseReference servicosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("servicoOrcamento")
                .child(getCliente().getId());
        servicosRef.setValue(this);
    }

    public void deletar(){
        DatabaseReference servicosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("servicoOrcamento")
                .child(getCliente().getId());
        servicosRef.removeValue();
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
}
