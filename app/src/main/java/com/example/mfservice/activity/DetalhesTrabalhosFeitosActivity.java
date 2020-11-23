package com.example.mfservice.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.mfservice.R;
import com.example.mfservice.fragment.AntesFragment;
import com.example.mfservice.fragment.DepoisFragment;
import com.example.mfservice.model.TrabalhosFeitos;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.synnapps.carouselview.ImageClickListener;

public class DetalhesTrabalhosFeitosActivity extends AppCompatActivity {

    private TrabalhosFeitos trabalhosFeitos;
    private TextView txtTitulo, txtDescricao;
    private FragmentPagerItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_trabalhos_feitos);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        //Inicializar componentes
        txtTitulo = findViewById(R.id.txtTituloDetalhes);
        txtDescricao = findViewById(R.id.txtDescricaoDetalhes);

        //Dados do trabalho
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            trabalhosFeitos = (TrabalhosFeitos) bundle.getSerializable("trabalhoFeitoSelecionado");
            toolbar.setTitle(trabalhosFeitos.getTitulo());
            txtTitulo.setText(trabalhosFeitos.getTitulo());
            txtDescricao.setText(trabalhosFeitos.getDescricao());

            adapter = new FragmentPagerItemAdapter(
                    getSupportFragmentManager(),
                    FragmentPagerItems
                            .with(this)
                            .add("Antes", AntesFragment.class)
                            .add("Depois", DepoisFragment.class)
                            .create()
            );

            ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
            viewPager.setAdapter(adapter);

            SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
            viewPagerTab.setViewPager(viewPager);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}