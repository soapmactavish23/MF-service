package com.example.mfservice.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.mfservice.R;
import com.example.mfservice.helper.Helper;
import com.example.mfservice.model.ProdutoOrcamento;
import com.google.android.material.textfield.TextInputEditText;

public class EditarProdutoOrcamentoActivity extends AppCompatActivity {

    private TextInputEditText editFormaPagamento, editPrazoEntrega, editValidade, editObs;
    private ProdutoOrcamento orcamentoSelecionado;
    private Helper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_produto_orcamento);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        //Inicializar componentes
        editFormaPagamento = findViewById(R.id.editFormaPagamento);
        editPrazoEntrega = findViewById(R.id.editPrazoEntrega);
        editValidade = findViewById(R.id.editValidade);
        editObs = findViewById(R.id.editObs);
        Bundle bundle = getIntent().getExtras();
        helper = new Helper(this);

        //Carregar Dados
        orcamentoSelecionado = (ProdutoOrcamento) bundle.getSerializable("orcamentoSelecionado");
        editFormaPagamento.setText(orcamentoSelecionado.getFormaPagamento());
        editPrazoEntrega.setText(orcamentoSelecionado.getPrazoEntrega());
        editValidade.setText(orcamentoSelecionado.getValidade());
        editObs.setText(orcamentoSelecionado.getObs());

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    public void salvar(View view){
        String formaPagamento = editFormaPagamento.getText().toString();
        String prazoEntrega = editPrazoEntrega.getText().toString();
        String validade = editValidade.getText().toString();
        String obs = editObs.getText().toString();
        orcamentoSelecionado.setFormaPagamento(formaPagamento);
        orcamentoSelecionado.setPrazoEntrega(prazoEntrega);
        orcamentoSelecionado.setValidade(validade);
        orcamentoSelecionado.setObs(obs);
        orcamentoSelecionado.salvar();
        helper.exibirMensagem("Orçamento Atualizado");
        finish();
    }
}
