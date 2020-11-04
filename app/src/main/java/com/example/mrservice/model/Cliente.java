package com.example.mrservice.model;

import com.example.mrservice.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Cliente implements Serializable {

    private String id;
    private String nome;
    private String foto;
    private String categoria;
    private String depoimento;

    public Cliente() {
        DatabaseReference clienteRef = ConfiguracaoFirebase.getFirebaseDatabase().child("clientes");
        setId(clienteRef.push().getKey());
    }

    public void salvar(){
        DatabaseReference clienteRef = ConfiguracaoFirebase.getFirebaseDatabase().child("clientes");
        clienteRef.child(getCategoria()).child(getId()).setValue(this);
    }

    public void deletar(){
        DatabaseReference clienteRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("clientes")
                .child(getCategoria())
                .child(getId());
        clienteRef.removeValue();
    }

    public void atualizar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference clientesRef = firebaseRef
                .child("clientes")
                .child(getCategoria())
                .child(getId());
        Map<String, Object> valoresClientes = converterParaMap();
        clientesRef.updateChildren(valoresClientes);
    }

    public Map<String, Object> converterParaMap(){
        HashMap<String, Object> clienteMap = new HashMap<>();
        clienteMap.put("nome", getNome());
        clienteMap.put("depoimento", getDepoimento());
        clienteMap.put("id", getId());
        clienteMap.put("foto", getFoto());
        clienteMap.put("categoria", getCategoria());
        return clienteMap;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDepoimento() {
        return depoimento;
    }

    public void setDepoimento(String depoimento) {
        this.depoimento = depoimento;
    }
}
