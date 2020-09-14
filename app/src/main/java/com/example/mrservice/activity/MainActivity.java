package com.example.mrservice.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.mrservice.R;
import com.example.mrservice.config.ConfiguracaoFirebase;
import com.example.mrservice.config.UsuarioFirebase;
import com.example.mrservice.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;

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
}
