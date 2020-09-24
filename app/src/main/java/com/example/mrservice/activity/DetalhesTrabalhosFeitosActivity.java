package com.example.mrservice.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mrservice.R;
import com.example.mrservice.model.TrabalhosFeitos;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

public class DetalhesTrabalhosFeitosActivity extends AppCompatActivity {

    private TrabalhosFeitos trabalhosFeitos;
    private TextView txtTitulo, txtDescricao;
    private CarouselView carouselView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_trabalhos_feitos);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        //Inicializar componentes
        txtTitulo = findViewById(R.id.txtTituloDetalhes);
        txtDescricao = findViewById(R.id.txtDescricaoDetalhes);
        carouselView = findViewById(R.id.carouselView2);
        //Dados do trabalho
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            trabalhosFeitos = (TrabalhosFeitos) bundle.getSerializable("trabalhoFeitoSelecionado");
            toolbar.setTitle(trabalhosFeitos.getTitulo());
            txtTitulo.setText(trabalhosFeitos.getTitulo());
            txtDescricao.setText(trabalhosFeitos.getDescricao());
            ImageListener imageListener = new ImageListener() {
                @Override
                public void setImageForPosition(int position, ImageView imageView) {
                    String urlString = trabalhosFeitos.getFotos().get(position);
                    Picasso.get().load(urlString).into(imageView);
                }
            };
            carouselView.setPageCount(trabalhosFeitos.getFotos().size());
            carouselView.setImageListener(imageListener);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
