package com.example.mfservice.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.mfservice.R;
import com.example.mfservice.adapter.AdapterItem;
import com.example.mfservice.fragment.ProdutosFragment;
import com.example.mfservice.fragment.ProdutosFragmentAdm;
import com.example.mfservice.fragment.ServicosFragment;
import com.example.mfservice.fragment.ServicosFragmentAdm;
import com.example.mfservice.model.Produto;
import com.example.mfservice.model.Usuario;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class OrcamentoActivity extends AppCompatActivity {

    private Usuario usuarioLogado;
    private MaterialSearchView searchView;
    private FragmentPagerItemAdapter adapterAdm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orcamento);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchView = findViewById(R.id.materialSearch);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            usuarioLogado = (Usuario) bundle.getSerializable("DadosUsuario");
        }

        if(usuarioLogado.getTipo_usuario().equals("ADM")){
            adapterAdm = new FragmentPagerItemAdapter(
                    getSupportFragmentManager(), FragmentPagerItems.with(this)
                    .add("Produtos", ProdutosFragmentAdm.class)
                    .add("Serviços", ServicosFragmentAdm.class)
                    .create());

            ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
            viewPager.setAdapter(adapterAdm);

            SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
            viewPagerTab.setViewPager(viewPager);

            //Configurar o SearchView
            searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if(newText != null && !newText.isEmpty()){
                        ProdutosFragmentAdm fragmentAdm = (ProdutosFragmentAdm) adapterAdm.getPage(0);
                        fragmentAdm.pesquisarOrcamentos(newText.toLowerCase());
                    }
                    return true;
                }
            });

            searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
                @Override
                public void onSearchViewShown() {

                }

                @Override
                public void onSearchViewClosed() {
                    ProdutosFragmentAdm fragmentAdm = (ProdutosFragmentAdm) adapterAdm.getPage(0);
                    fragmentAdm.recarregarOrcamentos();
                }
            });

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(usuarioLogado.getTipo_usuario().equals("ADM")){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_search_filter, menu);

            //Configurar botao pesquisa
            MenuItem item = menu.findItem(R.id.menu_pesquisa);
            searchView.setMenuItem(item);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_filtro:
                ProdutosFragmentAdm fragmentAdm = (ProdutosFragmentAdm) adapterAdm.getPage(0);
                fragmentAdm.selectStatus();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
