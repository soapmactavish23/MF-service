package com.example.mfservice.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mfservice.R;
import com.example.mfservice.config.ConfiguracaoFirebase;
import com.example.mfservice.config.UsuarioFirebase;
import com.example.mfservice.helper.Permissao;
import com.example.mfservice.model.Cliente;
import com.example.mfservice.model.Produto;
import com.example.mfservice.model.Usuario;
import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.example.mfservice.activity.GaleryActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private EditText txtJson;
    private String[] activits = {
            "PRODUTOS",
            "SERVICOS",
            "ORCAMENTOS",
            "TRABALHOS FEITOS",
            "CLIENTES SATISFEITOS",
            "CLIENTES",
            "PARCEIROS"
    };

    private GoogleMap mMap;
    private String[] permissoes = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("MF service");
        txtJson = findViewById(R.id.txtJson);

        //Configurações Iniciais
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        Permissao.validarPermissoes(permissoes,this, 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_principal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for(int permissaoResultado : grantResults){
            if(permissaoResultado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
            }
        }
    }

    private void alertaValidacaoPermissao(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utitlizar o app é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.sair:
                autenticacao.signOut();
                finish();
                break;
            case R.id.endereco:
                abrirEndereco();
                break;
            case R.id.config:
                abrirConfiguracoes();
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

    public void abrirEndereco(){
        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();
        databaseReference.child("usuarios")
                .child(UsuarioFirebase.getIdentificadorUsuario())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Usuario usuario = dataSnapshot.getValue(Usuario.class);
                        Intent intent = new Intent(MainActivity.this, EnderecoActivity.class);
                        intent.putExtra("DadosUsuario", usuario);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void abrirConfiguracoes(){
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
                                Intent intent_servicos = new Intent(MainActivity.this, ServicosActivity.class);
                                intent_servicos.putExtra("DadosUsuario", usuario);
                                startActivity(intent_servicos);
                                break;
                            case "ORCAMENTOS":
                                Intent intent_orcamento = new Intent(MainActivity.this, OrcamentoActivity.class);
                                intent_orcamento.putExtra("DadosUsuario", usuario);
                                startActivity(intent_orcamento);
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
