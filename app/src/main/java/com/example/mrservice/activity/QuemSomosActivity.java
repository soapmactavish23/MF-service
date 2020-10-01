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
                        "Confira!")
                /*.addGroup("EQUIPAMENTOS")
                .addItem(new Element().setTitle("PROFISSIONAL"))
                .addItem(new Element().setTitle("PILATES"))
                .addItem(new Element().setTitle("AO AR LIVRE"))
                .addGroup("CÁRDIO")
                .addItem(new Element().setTitle("ESTEIRA"))
                .addItem(new Element().setTitle("BIKE"))
                .addItem(new Element().setTitle("ELÍPTICO"))
                .addItem(new Element().setTitle("ESTAÇÃO"))
                .addItem(new Element().setTitle("SIMULADOR"))
                .addGroup("ACESSÓRIOS")
                .addItem(new Element().setTitle("MUSCULAÇÃO"))
                .addItem(new Element().setTitle("GINÁSTICA"))
                .addItem(new Element().setTitle("ARTE MARCIAL"))
                .addItem(new Element().setTitle("FUNCIONAL"))
                .addItem(new Element().setTitle("CROSSFIT"))
                .addItem(new Element().setTitle("HIDROGINÁSTICA"))
                .addItem(new Element().setTitle("PILATES"))
                .addItem(new Element().setTitle("CATRACA"))
                .addGroup("PISOS")
                .addItem(new Element().setTitle("VINÍLICO"))
                .addItem(new Element().setTitle("RESINADO"))
                .addItem(new Element().setTitle("EMBORRACHADO"))
                .addItem(new Element().setTitle("GRAMA SINTÉTICA: DECORATIVA E ESPORTIVA"))
                .addItem(new Element().setTitle("TÁTIL"))
                .addGroup("REVESTIMENTOS")
                .addItem(new Element().setTitle("PAREDE"))
                .addItem(new Element().setTitle("AZULEJO"))
                .addGroup("BRINQUEDOS")
                .addItem(new Element().setTitle("ÁREA DE LAZER"))
                .addItem(new Element().setTitle("PLAYGROUND"))
                .addGroup("Além disso, contamos com profissionais qualificados e constantementes treinados, para oferecer uma mão de obra especializada na realização dos seguintes serviços:")
                .addItem(new Element().setTitle("ASSESSORIA PARA IMPLEMENTAÇÃO DE PROJETOS"))
                .addItem(new Element().setTitle("SERVIÇOS DE ENGENHARIA"))
                .addItem(new Element().setTitle("MANUTENÇÃO PREVENTIVA E CORRETIVA"))
                .addGroup("ATENDEMOS:")
                .addItem(new Element().setTitle("ACADEMIA"))
                .addItem(new Element().setTitle("CONDOMÍNIO"))
                .addItem(new Element().setTitle("CLÍNICA"))
                .addItem(new Element().setTitle("CLUBE"))
                .addItem(new Element().setTitle("HOTEL"))
                .addItem(new Element().setTitle("ÓRGÃO: PÚBLICO E PRIVADO"))
                .addItem(new Element().setTitle("RESIDÊNCIA"))*/
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
