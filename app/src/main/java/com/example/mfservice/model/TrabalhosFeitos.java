package com.example.mfservice.model;

import com.example.mfservice.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrabalhosFeitos implements Serializable {

    private String id;
    private String titulo;
    private String descricao;
    private List<String> fotos;

    public TrabalhosFeitos() {
        DatabaseReference trabalhosFeitosRef = ConfiguracaoFirebase.getFirebaseDatabase().child("trabalhos feitos");
        setId(trabalhosFeitosRef.push().getKey());
    }

    public void salvar(){
        DatabaseReference trabalhosFeitosRef = ConfiguracaoFirebase.getFirebaseDatabase().child("trabalhos feitos");
        trabalhosFeitosRef.child(getId()).setValue(this);
    }

    public void deletar(){
        DatabaseReference trabalhosFeitosRef = ConfiguracaoFirebase.getFirebaseDatabase().child("trabalhos feitos")
                .child(getId());
        trabalhosFeitosRef.removeValue();

        //Criar nó no storage
        StorageReference storageReference = ConfiguracaoFirebase.getStorageReference();
        StorageReference imagemTrabalhoFeito = storageReference.child("imagens").child("trabalhos feitos").child(getId())
                .child("imagem0");
        imagemTrabalhoFeito.delete();
    }

    public void atualizar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference trabalhosFeitos = firebaseRef.child("trabalhos feitos").child(getId());
        Map<String, Object> valoresTrabalhosFeitos = converterParaMap();
        trabalhosFeitos.updateChildren(valoresTrabalhosFeitos);
    }

    public Map<String, Object> converterParaMap(){
        HashMap<String, Object> trabalhosFeitosMap = new HashMap<>();
        trabalhosFeitosMap.put("id", getId());
        trabalhosFeitosMap.put("titulo", getTitulo());
        trabalhosFeitosMap.put("descricao", getDescricao());
        return trabalhosFeitosMap;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<String> getFotos() {
        return fotos;
    }

    public void setFotos(List<String> fotos) {
        this.fotos = fotos;
    }
}
