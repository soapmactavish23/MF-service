package com.example.mrservice.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import com.example.mrservice.R;

import org.w3c.dom.Text;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class QuemSomosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quem_somos);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Element contato = new Element();
        contato.setTitle("Contato: (91) 98182-2113");
        contato.setIconDrawable(R.drawable.ic_contato);
        contato.setIconTint(R.color.dark);
        contato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",  "9198182-2113", null)));
            }
        });

        Element face = new Element();
        face.setTitle("Curta no Facebook");
        face.setIconDrawable(R.drawable.about_icon_facebook);
        face.setIconTint(R.color.about_facebook_color);
        face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/MFSTORESERVICE/")));
            }
        });

        View aboutPage = new AboutPage(QuemSomosActivity.this)
                .isRTL(false)
                .setImage(R.drawable.logo_about)
                .setDescription("A MF service, fundada em novembro de 2012, é uma empresa que procura a satisfação dos seus clientes aliando a oferta de produtos que são novidades no mercado nacional e internacional a um atendimento especializado e personalizado.\n" +
                        "Confira !")
                .addGroup("Entre em Contato")
                .addEmail("mfstoreservices@gmail.com", "Envie um e-mail")
                .addItem(contato)
                .addGroup("Redes Sociais")
                .addItem(face)
                .addInstagram("mfservice_pa", "Siga no Instagram")
                .create();
        setContentView(aboutPage);
    }
}
