package com.example.mrservice.activity;

import androidx.appcompat.app.AppCompatActivity;

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

        /*Element contato = new Element();
        contato.setTitle("Contato: (91) 98182-2113");

        //Equipamentos
        Element profissional = new Element();
        profissional.setTitle("PROFISSIONAL");

        Element REMANUFATURADO = new Element();
        REMANUFATURADO.setTitle("REMANUFATURADO");

        Element PILATES = new Element();
        PILATES.setTitle("AO AR LIVRE");

        Element arlivre = new Element();
        arlivre.setTitle("PILATES");

        //Cardio
        Element ESTEIRA = new Element();

        View aboutPage = new AboutPage(QuemSomosActivity.this)
                .isRTL(false)
                .setImage(R.drawable.logo_about)
                .setDescription("A MF service, fundada em novembro de 2012, é uma empresa que procura a satisfação dos seus clientes aliando a oferta de produtos que são novidades no mercado nacional e internacional a um atendimento especializado e personalizado.\n" +
                        "Oferecemos uma diversificada linha de produtos e serviços tais como:")
                .addGroup("1- EQUIPAMENTOS")
                .addItem(profissional)
                .addItem(REMANUFATURADO)
                .addItem(PILATES)
                .addItem(arlivre)
                .addGroup("2- CÁRDIO")
                .addItem()
                .addGroup("Entre em Contato")
                .addEmail("mfstoreservices@gmail.com", "Envie um e-mail")
                .addItem(contato)
                .addGroup("Redes Sociais")
                .addFacebook("the.HenrickNogueira", "Curta no Facebook: MFSTORESERVICE")
                .addInstagram("mfstoreservice", "Siga no Instagram")
                .create();
        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(aboutPage);*/
    }
}
