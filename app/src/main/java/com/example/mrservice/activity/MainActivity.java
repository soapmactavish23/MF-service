package com.example.mrservice.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.mrservice.R;
import com.example.mrservice.config.ConfiguracaoFirebase;
import com.example.mrservice.config.UsuarioFirebase;
import com.example.mrservice.model.Produto;
import com.example.mrservice.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.example.mrservice.activity.GaleryActivity;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private String[] activits = {
            "PRODUTOS",
            "SERVICOS",
            "ORCAMENTOS",
            "TRABALHOS FEITOS",
            "CLIENTES SATISFEITOS",
            "CLIENTES",
            "PARCEIROS"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("MF service");

        //Configurações Iniciais
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_principal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.sair:
                autenticacao.signOut();
                finish();
                break;
            case R.id.config:
                DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();
                databaseReference.child("usuarios")
                        .child(UsuarioFirebase.getIdentificadorUsuario())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                                Intent intent = new Intent(MainActivity.this, ConfiguracaoActivity.class);
                                intent.putExtra("DadosUsuario", usuario);
                                startActivity(intent);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void quemSomos(View view){
        startActivity(new Intent(this, QuemSomosActivity.class));
    }

    public void abrirProdutos(View view){
        abrir(activits[0]);
    }

    public void abrirServicos(View view){
        abrir(activits[1]);
    }

    public void abrirOrcamento(View view){
        abrir(activits[2]);
    }

    public void abrirTrabalhosFeitos(View view){
        abrir(activits[3]);
    }

    public void abrirClientesSatisfeitos(View view){
        abrir(activits[4]);
    }

    public void abrirNossosClientes(View view){
        abrir(activits[5]);
    }

    public void abrirNossosParceiros(View view){
        abrir(activits[6]);
    }

    private void abrir(final String activity) {
        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();
        databaseReference.child("usuarios")
                .child(UsuarioFirebase.getIdentificadorUsuario())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Usuario usuario = dataSnapshot.getValue(Usuario.class);
                        switch (activity){
                            case "PRODUTOS":
                                Intent intent_produtos = new Intent(MainActivity.this, ProdutosActivity.class);
                                intent_produtos.putExtra("DadosUsuario", usuario);
                                startActivity(intent_produtos);
                                break;
                            case "SERVICOS":
                                break;
                            case "ORCAMENTO":
                                break;
                            case "TRABALHOS FEITOS":
                                Intent intent_trabalhos = new Intent(MainActivity.this, TrabalhosFeitosActivity.class);
                                intent_trabalhos.putExtra("DadosUsuario", usuario);
                                startActivity(intent_trabalhos);
                                break;
                            case "CLIENTES SATISFEITOS":
                                Intent intent_clientes_satisfeitos = new Intent(MainActivity.this, ClientesSatisfeitosActivity.class);
                                intent_clientes_satisfeitos.putExtra("DadosUsuario", usuario);
                                startActivity(intent_clientes_satisfeitos);
                                break;
                            case "CLIENTES":
                                Intent intent_clientes = new Intent(MainActivity.this, ClientesActivity.class);
                                intent_clientes.putExtra("DadosUsuario", usuario);
                                startActivity(intent_clientes);
                                break;
                            case "PARCEIROS":
                                Intent intent_parceiros = new Intent(MainActivity.this, ParceirosActivity.class);
                                intent_parceiros.putExtra("DadosUsuario", usuario);
                                startActivity(intent_parceiros);
                                break;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

}
