package com.example.mfservice.model;

import com.example.mfservice.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

public class ClientesSatisfeitos implements Serializable {

    private String id;
    private String nomeCliente;
    private String depoimento;
    private String foto;

    public ClientesSatisfeitos() {
        DatabaseReference clientesSatisfeitosRef = ConfiguracaoFirebase.getFirebaseDatabase().child("clientes satisfeitos");
        setId(clientesSatisfeitosRef.push().getKey());

    }

    public void salvar(){
        DatabaseReference clientesSatisfeitosRef = ConfiguracaoFirebase.getFirebaseDatabase().child("clientes satisfeitos");
        clientesSatisfeitosRef.child(getId()).setValue(this);
    }

    public void deletar(){
        DatabaseReference clientesSatisfeitosRef = ConfiguracaoFirebase.getFirebaseDatabase().child("clientes satisfeitos").child(getId());
        clientesSatisfeitosRef.removeValue();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getDepoimento() {
        return depoimento;
    }

    public void setDepoimento(String depoimento) {
        this.depoimento = depoimento;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
