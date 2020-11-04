package com.example.mfservice.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.mfservice.model.Produto;
import com.example.mfservice.model.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mfservice.R;

public class ProdutosActivity extends AppCompatActivity {

    private Usuario usuario;
    private String filtroCategoria;
    private String filtroTipoProduto;
    private String[] tipoProduto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produtos);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProdutosActivity.this, CadastrarProdutoActivity.class));
            }
        });

        //Checar se o usuario e adm
        Bundle bundle = getIntent().getExtras();
        usuario = (Usuario) bundle.getSerializable("DadosUsuario");
        if(!usuario.getTipo_usuario().equals("ADM")){
            fab.setVisibility(View.GONE);
        }

        tipoProduto = getResources().getStringArray(R.array.equipamentos);

    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.imgEquipamento:
                filtroCategoria = "EQUIPAMENTOS";
                tipoProduto = getResources().getStringArray(R.array.equipamentos);
                recuperar();
                break;
            case R.id.imgCardio:
                filtroCategoria = "CÁRDIO";
                tipoProduto = getResources().getStringArray(R.array.cardio);
                recuperar();
                break;
            case R.id.imgAcessorio:
                filtroCategoria = "ACESSÓRIOS";
                tipoProduto = getResources().getStringArray(R.array.acessorios);
                recuperar();
                break;
            case R.id.imgPiso:
                filtroCategoria = "PISOS";
                tipoProduto = getResources().getStringArray(R.array.piso);
                recuperar();
                break;
            case R.id.imgRevestimento:
                filtroCategoria = "REVESTIMENTOS";
                tipoProduto = getResources().getStringArray(R.array.revestimento);
                recuperar();
                break;
            case R.id.imgBrinquedo:
                filtroCategoria = "BRINQUEDOS";
                tipoProduto = getResources().getStringArray(R.array.brinquedo);
                recuperar();
                break;
        }
    }

    public void recuperar(){
        AlertDialog.Builder dialogCategoria = new AlertDialog.Builder(this);
        dialogCategoria.setTitle("Selecione o tipo de produto desejado:");

        //Configurar spinner
        View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);
        final Spinner spinner = viewSpinner.findViewById(R.id.spinnerFiltro);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, tipoProduto
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        dialogCategoria.setView(viewSpinner);

        dialogCategoria.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Abrir activity
                filtroTipoProduto = spinner.getSelectedItem().toString();
                Intent intent = new Intent(ProdutosActivity.this, ListProdutosActivity.class);
                intent.putExtra("DadosUsuario", usuario);
                intent.putExtra("tipo_produto", filtroTipoProduto);
                intent.putExtra("categoria", filtroCategoria);
                startActivity(intent);
            }
        });
        dialogCategoria.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = dialogCategoria.create();
        dialog.show();
    }

    private void exibirMensagem(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
