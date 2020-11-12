package com.example.mfservice.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.mfservice.R;
import com.example.mfservice.fragment.QuemSomosFragment;

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

        getSupportFragmentManager().beginTransaction().add(R.id.LinearQuemSomos, new QuemSomosFragment()).commit();
    }
}
