package com.example.mfservice.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mfservice.R;
import com.example.mfservice.activity.QuemSomosActivity;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuemSomosFragment extends Fragment {

    public QuemSomosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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

        return new AboutPage(getActivity())
                .isRTL(false)
                .setImage(R.drawable.logo_about)
                .setDescription("A MF service, fundada em novembro de 2012, é uma empresa que procura a satisfação dos seus clientes aliando a oferta de produtos que são novidades no mercado nacional e internacional a um atendimento especializado e personalizado.\n" +
                        "Confira!")
                .addGroup("Entre em Contato")
                .addEmail("mfservicepa@gmail.com", "Envie um e-mail")
                .addItem(contato)
                .addGroup("Redes Sociais")
                .addItem(face)
                .addInstagram("mfservicepa", "Siga no Instagram")
                .create();


    }
}
