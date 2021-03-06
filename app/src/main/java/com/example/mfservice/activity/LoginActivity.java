package com.example.mfservice.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mfservice.R;
import com.example.mfservice.config.ConfiguracaoFirebase;
import com.example.mfservice.config.UsuarioFirebase;
import com.example.mfservice.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout textEmail, textSenha;
    private TextInputEditText editEmail, editSenha;
    private FirebaseAuth autenticacao;
    private TextView txtNaoTemConta;
    private Button btnEntrar, btnEsqueceuSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = findViewById(R.id.editEmail);
        editSenha = findViewById(R.id.editSenha);
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        textEmail = findViewById(R.id.textEmail);
        textSenha = findViewById(R.id.textSenha);
        txtNaoTemConta = findViewById(R.id.txtNaoTemConta);
        btnEntrar = findViewById(R.id.btnEntrar);
        btnEsqueceuSenha = findViewById(R.id.btnEsqueceuSenha);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser usuarioAtual = autenticacao.getCurrentUser();
        if(usuarioAtual != null){
            btnEsqueceuSenha.setVisibility(View.GONE);
            textEmail.setVisibility(View.GONE);
            textSenha.setVisibility(View.GONE);
            btnEntrar.setVisibility(View.GONE);
            txtNaoTemConta.setVisibility(View.GONE);
            abrirMainActivity();
        }else{
            btnEsqueceuSenha.setVisibility(View.VISIBLE);
            textEmail.setVisibility(View.VISIBLE);
            textSenha.setVisibility(View.VISIBLE);
            btnEntrar.setVisibility(View.VISIBLE);
            txtNaoTemConta.setVisibility(View.VISIBLE);
        }
    }

    //Validar autenticacao
    public void validarAutenticacao(View view){
        String email = editEmail.getText().toString();
        String senha = editSenha.getText().toString();
        if(!email.isEmpty() || !senha.isEmpty()){
            Usuario usuario = new Usuario();
            usuario.setEmail(email);
            usuario.setSenha(senha);
            logar(usuario);
        }else{
            Toast.makeText(
                    getApplicationContext(),
                    "Preencha Todos os Campos",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    public void logar(Usuario usuario){
        autenticacao.signInWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            abrirMainActivity();
                        }else{
                            String excecao = "";
                            try {
                                throw task.getException();
                            }catch ( FirebaseAuthInvalidUserException e ) {
                                excecao = "Usuário não está cadastrado.";
                            }catch ( FirebaseAuthInvalidCredentialsException e ){
                                excecao = "E-mail e senha não correspondem a um usuário cadastrado";
                            }catch (Exception e){
                                excecao = "Erro ao cadastrar usuário: "  + e.getMessage();
                                e.printStackTrace();
                            }
                            Toast.makeText(
                                    LoginActivity.this,
                                    excecao,
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
                });
    }
    public void cadastrarSe(View view){
        startActivity(new Intent(LoginActivity.this, CadastroActivity.class));
    }
    private void abrirMainActivity(){
        UsuarioFirebase.redirecionarUsuarioLogado(LoginActivity.this);
    }

    public void esqueceuSenha(View view){
        AlertDialog.Builder dialogCategoria = new AlertDialog.Builder(this);
        dialogCategoria.setTitle("Digite seu email para redefinição de senha:");

        //Configurar spinner
        View viewMudarSenha = getLayoutInflater().inflate(R.layout.dialog_input, null);
        final TextInputEditText editEmailMudar = viewMudarSenha.findViewById(R.id.editEmail);
        dialogCategoria.setView(viewMudarSenha);

        dialogCategoria.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                String emailAddress = editEmailMudar.getText().toString();

                auth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(
                                    LoginActivity.this,
                                    "Email com redefinição de senha enviado",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
                });
            }
        });
        dialogCategoria.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = dialogCategoria.create();
        dialog.show();
    }
}
