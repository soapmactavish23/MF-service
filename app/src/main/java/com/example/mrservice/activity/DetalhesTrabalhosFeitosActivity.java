package com.example.mrservice.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mrservice.R;
import com.example.mrservice.model.TrabalhosFeitos;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;
import com.synnapps.carouselview.ViewListener;

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
            final ImageListener imageListener = new ImageListener() {
                @Override
                public void setImageForPosition(int position, ImageView imageView) {
                    String urlString = trabalhosFeitos.getFotos().get(position);
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.placeholder(R.drawable.galery_padrao);
                    requestOptions.fitCenter();
                    Glide.with(getApplicationContext())
                            .applyDefaultRequestOptions(requestOptions)
                            .load(urlString)
                            .into(imageView);
                }
            };
            carouselView.setPageCount(trabalhosFeitos.getFotos().size());
            carouselView.setImageListener(imageListener);
            carouselView.setImageClickListener(new ImageClickListener() {
                @Override
                public void onClick(int position) {
                    Intent intent = new Intent(DetalhesTrabalhosFeitosActivity.this, GaleryActivity.class);
                    intent.putExtra("foto", trabalhosFeitos.getFotos().get(position));
                    intent.putExtra("titulo", trabalhosFeitos.getTitulo());
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
