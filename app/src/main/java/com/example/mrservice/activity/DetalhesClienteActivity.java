package com.example.mrservice.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mrservice.R;
import com.example.mrservice.model.Cliente;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetalhesClienteActivity extends AppCompatActivity {

    private Cliente cliente;
    private TextView txtNome, txtDepoimento;
    //private CircleImageView circleImageView;
    private ImageView circleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_cliente);
        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar6);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        //Inicializando components
        txtNome = findViewById(R.id.txtNome);
        txtDepoimento = findViewById(R.id.txtDepoimento);
        circleImageView = findViewById(R.id.imgClienteSatisfeito);

        //Linkando componentes
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            cliente = (Cliente) bundle.getSerializable("cliente");
            txtNome.setText(cliente.getNome());
            toolbar.setTitle(cliente.getNome());
            txtDepoimento.setText(cliente.getDepoimento());
            if (cliente.getFoto().equals("")){
                circleImageView.setImageResource(R.drawable.padrao);
            }else{
                Picasso.get().load(cliente.getFoto()).into(circleImageView);
            }
        }

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetalhesClienteActivity.this, GaleryActivity.class);
                intent.putExtra("foto", cliente.getFoto());
                intent.putExtra("titulo", cliente.getNome());
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
