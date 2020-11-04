package com.example.mfservice.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.mfservice.R;
import com.example.mfservice.adapter.AdapterItem;
import com.example.mfservice.fragment.ProdutosFragment;
import com.example.mfservice.fragment.ProdutosFragmentAdm;
import com.example.mfservice.fragment.ServicosFragment;
import com.example.mfservice.fragment.ServicosFragmentAdm;
import com.example.mfservice.model.Produto;
import com.example.mfservice.model.Usuario;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class OrcamentoActivity extends AppCompatActivity {

    private Usuario usuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orcamento);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            usuarioLogado = (Usuario) bundle.getSerializable("DadosUsuario");
        }

        if(usuarioLogado.getTipo_usuario().equals("ADM")){
            FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                    getSupportFragmentManager(), FragmentPagerItems.with(this)
                    .add("Produtos", ProdutosFragmentAdm.class)
                    .add("Serviços", ServicosFragmentAdm.class)
                    .create());

            ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
            viewPager.setAdapter(adapter);

            SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
            viewPagerTab.setViewPager(viewPager);
        }else{
            FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                    getSupportFragmentManager(), FragmentPagerItems.with(this)
                    .add("Produtos", ProdutosFragment.class)
                    .add("Serviços", ServicosFragment.class)
                    .create());

            ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
            viewPager.setAdapter(adapter);

            SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
            viewPagerTab.setViewPager(viewPager);
        }

    }

}
