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

                        ServicosFragmentAdm fragmentServico = (ServicosFragmentAdm) adapterAdm.getPage(1);
                        fragmentServico.pesquisarOrcamentos(newText.toLowerCase());
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
                selectStatus();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void selectStatus(){
        android.app.AlertDialog.Builder dialogCategoria = new android.app.AlertDialog.Builder(OrcamentoActivity.this);
        dialogCategoria.setTitle("Escolher Status");

        //Configurar spinner
        View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);
        final Spinner spinner = viewSpinner.findViewById(R.id.spinnerFiltro);

        final String[] linha = getResources().getStringArray(R.array.status);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getApplicationContext(), android.R.layout.simple_spinner_item, linha
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        dialogCategoria.setView(viewSpinner);

        dialogCategoria.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String s = spinner.getSelectedItem().toString();
                //Produtos
                ProdutosFragmentAdm produtosFragmentAdm = (ProdutosFragmentAdm) adapterAdm.getPage(0);

                //Servicos
                ServicosFragmentAdm servicosFragmentAdm = (ServicosFragmentAdm) adapterAdm.getPage(1);
                if(s.equals("TODOS")){
                    produtosFragmentAdm.recuperarPorStatus("");
                    servicosFragmentAdm.recuperarStatus("");
                }else{
                    produtosFragmentAdm.recuperarPorStatus(s);
                    servicosFragmentAdm.recuperarStatus(s);
                }
            }
        });
        dialogCategoria.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog = dialogCategoria.create();
        alertDialog.show();
    }

}
