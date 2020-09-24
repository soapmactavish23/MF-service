package com.example.mrservice.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.TextView;

import com.example.mrservice.R;
import com.example.mrservice.model.ClientesSatisfeitos;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetalhesClienteSatisfeitoActivity extends AppCompatActivity {

    private ClientesSatisfeitos clientesSatisfeitos;
    private TextView txtNome, txtDepoimento;
    private CircleImageView circleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_cliente_satisfeito);
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
            clientesSatisfeitos = (ClientesSatisfeitos) bundle.getSerializable("clienteSatisfeito");
            txtNome.setText(clientesSatisfeitos.getNomeCliente());
            toolbar.setTitle(clientesSatisfeitos.getNomeCliente());
            txtDepoimento.setText(clientesSatisfeitos.getDepoimento());
            Picasso.get().load(clientesSatisfeitos.getFoto()).into(circleImageView);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
