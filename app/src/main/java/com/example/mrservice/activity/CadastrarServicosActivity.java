package com.example.mrservice.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mrservice.R;
import com.example.mrservice.model.ServicoOrcamento;
import com.google.android.material.textfield.TextInputEditText;

import dmax.dialog.SpotsDialog;

public class CadastrarServicosActivity extends AppCompatActivity {

    private TextInputEditText editTitulo;
    private Spinner spinnerCategoria;
    private android.app.AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_servicos);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        //Configuracoes Iniciais
        editTitulo = findViewById(R.id.editTitulo);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);

        //Configurando as categorias
        String[] categoria = getResources().getStringArray(R.array.categorias_servicos);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, R.layout.simple_pinner_item_white, categoria
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    public void salvarServico(View view){
        String titulo = editTitulo.getText().toString();
        String categoria = spinnerCategoria.getSelectedItem().toString();
        if(!titulo.isEmpty()){
            dialog = new SpotsDialog.Builder()
                    .setContext(this)
                    .setMessage("Salvando Serviço")
                    .setCancelable(false)
                    .build();
            dialog.show();
            ServicoOrcamento servicoOrcamento = new ServicoOrcamento();
            servicoOrcamento.setTitulo(titulo);
            //servicoOrcamento.setCategoria(categoria);
            if(servicoOrcamento.salvar()){
                dialog.dismiss();
                finish();
            }
        }else{
            exibirMsg("Preencha o campo Título");
        }
    }

    private void exibirMsg(String msg){
        Toast.makeText(
                getApplicationContext(),
                msg,
                Toast.LENGTH_SHORT
        ).show();
    }

}