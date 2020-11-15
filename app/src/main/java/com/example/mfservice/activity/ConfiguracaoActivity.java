package com.example.mfservice.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mfservice.R;
import com.example.mfservice.config.ConfiguracaoFirebase;
import com.example.mfservice.config.UsuarioFirebase;
import com.example.mfservice.helper.Base64Custom;
import com.example.mfservice.helper.Permissao;
import com.example.mfservice.model.ProdutoOrcamento;
import com.example.mfservice.model.ServicoOrcamento;
import com.example.mfservice.model.Usuario;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConfiguracaoActivity extends AppCompatActivity {

    private TextInputEditText txtNome, txtEmail, txtCpf, txtContato, txtEndereco;
    private CircleImageView imgEditarPerfil;
    private Usuario usuarioLogado;
    private ProdutoOrcamento produtoOrcamento;
    private ServicoOrcamento servicoOrcamento;
    private FirebaseUser usuarioPerfil;
    private StorageReference storageReference;
    private String idUsuario;

    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;
    private DatabaseReference firebaseRef, usuarioRef, produtoOrcamentoRef, servicoOrcamentoRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracao);

        //Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Editar Perfil");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        //Validar Permissoes
        Permissao.validarPermissoes(permissoesNecessarias, this, 1);

        //Configuracoes Iniciais
        txtNome = findViewById(R.id.editNome);
        txtEmail = findViewById(R.id.editEmail);
        txtCpf = findViewById(R.id.editCpf);
        txtContato = findViewById(R.id.editContato);
        txtEndereco = findViewById(R.id.editEndereco);
        imgEditarPerfil = findViewById(R.id.imgFotoCliente);

        //Configuacao Firebase
        storageReference = ConfiguracaoFirebase.getStorageReference();
        idUsuario = UsuarioFirebase.getIdentificadorUsuario();

        //Firebase
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        usuarioRef = firebaseRef.child("usuarios").child(idUsuario);
        produtoOrcamentoRef = firebaseRef.child("produtoOrcamento").child(idUsuario);
        servicoOrcamentoRef = firebaseRef.child("servicoOrcamento").child(idUsuario);

        //Dados usuario
        Bundle bundle = this.getIntent().getExtras();
        usuarioLogado = (Usuario) bundle.getSerializable("DadosUsuario");

        //Ajustando mascaras
        ajustarMascaras();

        //Carregar dados do usuario
        txtNome.setText(usuarioLogado.getNome());
        txtEmail.setText(usuarioLogado.getEmail());
        txtContato.setText(usuarioLogado.getContato());
        txtCpf.setText(usuarioLogado.getCpf());
        txtEndereco.setText(usuarioLogado.getEndereco());

        //Foto do usuario
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.fitCenter();
        requestOptions.placeholder(R.drawable.padrao);
        Glide.with(this).applyDefaultRequestOptions(requestOptions).load(usuarioLogado.getFoto()).into(imgEditarPerfil);

        //Recuperar orcamentos
        recuperarProdutoOrcamento();
        recupererServicoOrcamento();

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    private void ajustarMascaras(){
        //Criando Formato da Mascara
        SimpleMaskFormatter simpleMaskFormatterCPF = new SimpleMaskFormatter("NNN.NNN.NNN-NN");
        SimpleMaskFormatter simpleMaskFormatterContato = new SimpleMaskFormatter("(NN) NNNNN-NNNN");

        //Ajustando para os textos
        MaskTextWatcher maskTextWatcherCPF = new MaskTextWatcher(txtCpf, simpleMaskFormatterCPF);
        MaskTextWatcher maskTextWatcherContato = new MaskTextWatcher(txtContato, simpleMaskFormatterContato);

        //Aplicando Mascaras
        txtCpf.addTextChangedListener(maskTextWatcherCPF);
        txtContato.addTextChangedListener(maskTextWatcherContato);
    }

    public void atualizar(View view){
        String nome = txtNome.getText().toString();
        String cpf = txtCpf.getText().toString();
        String contato = txtContato.getText().toString();
        String endereco = txtEndereco.getText().toString();
        if(!nome.isEmpty() && !cpf.isEmpty() && !contato.isEmpty()){
            //Atualizar o nome no perfil
            UsuarioFirebase.atualizarNomeUsuario(nome);

            //Atualizar o nome no banco de dados
            usuarioLogado.setNome(nome);
            usuarioLogado.setCpf(cpf);
            usuarioLogado.setContato(contato);
            usuarioLogado.setEndereco(endereco);
            usuarioLogado.atualizar();

            atualizarOrcamentos();

            Toast.makeText(
                    ConfiguracaoActivity.this,
                    "Nome de usuário atualizado com sucesso!",
                    Toast.LENGTH_SHORT
            ).show();
        }else{
            Toast.makeText(
                    ConfiguracaoActivity.this,
                    "Preencha os Campos",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void recuperarProdutoOrcamento(){
        produtoOrcamentoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                produtoOrcamento = snapshot.getValue(ProdutoOrcamento.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void recupererServicoOrcamento(){
        servicoOrcamentoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                servicoOrcamento = snapshot.getValue(ServicoOrcamento.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void atualizaFotoUsuario(Uri url){
        boolean retorno = UsuarioFirebase.atualizarFotoUsuario(url);
        if(retorno){
            usuarioLogado.setFoto(url.toString());
            usuarioLogado.atualizar();
            Toast.makeText(
                    ConfiguracaoActivity.this,
                    "Sua foto foi atualizada",
                    Toast.LENGTH_SHORT
            ).show();
            atualizarOrcamentos();
        }
    }

    public void mudarSenha(View view){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAddress = usuarioLogado.getEmail();

        auth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(
                            ConfiguracaoActivity.this,
                            "Email com redefinição de senha enviado",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });
    }

    public void excluirConta(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(ConfiguracaoActivity.this);
        builder.setTitle("Excluir Conta");
        builder.setMessage("Tem certeza que deseja excluir sua conta? Após isso você só poderá acessar o app se criar outra conta");
        builder.setCancelable(true);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ConfiguracaoActivity.this, "Conta Excluída", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                DatabaseReference usuariosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                        .child("usuarios");
                usuariosRef.child(Base64Custom.codificarBase64(usuarioLogado.getEmail())).removeValue();
                FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
                autenticacao.signOut();
                startActivity(new Intent(ConfiguracaoActivity.this, LoginActivity.class));
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void alterarFoto(View view){
        switch (view.getId()){
            case R.id.btnCamera:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if ( intent.resolveActivity(getPackageManager()) != null ){
                    startActivityForResult(intent, SELECAO_CAMERA );
                }
                break;
            case R.id.btnGaleria:
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if( i.resolveActivity(ConfiguracaoActivity.this.getPackageManager()) != null){
                    startActivityForResult(i, SELECAO_GALERIA);
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Bitmap imagem = null;
            try{
                //Selecao apenas da galeria
                switch (requestCode){
                    case SELECAO_CAMERA:
                        imagem = (Bitmap) data.getExtras().get("data");
                        break;
                    case SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(ConfiguracaoActivity.this.getContentResolver(), localImagemSelecionada);
                        break;
                }

                //Caso tenha sido escolhido uma imagem
                if(imagem != null){
                    //Configura imagem na tela
                    imgEditarPerfil.setImageBitmap(imagem);

                    //Recuperar dados da imagem para o Firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    //Salvar imagem no firebase
                    StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("perfil")
                            .child(idUsuario + ".jpeg");
                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(
                                    ConfiguracaoActivity.this,
                                    "Erro ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(
                                    ConfiguracaoActivity.this,
                                    "Sucesso ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT
                            ).show();
                            Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                            while(!uri.isComplete());
                            Uri url = uri.getResult();
                            atualizaFotoUsuario(url);
                        }
                    });

                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void atualizarOrcamentos(){
        try{
            produtoOrcamento.setNomeCliente(usuarioLogado.getNome());
            produtoOrcamento.setFotoCliente(usuarioLogado.getFoto());
            produtoOrcamento.atualizar();

            servicoOrcamento.setNomeCliente(usuarioLogado.getNome());
            servicoOrcamento.setFotoCliente(usuarioLogado.getFoto());
            servicoOrcamento.salvar();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
