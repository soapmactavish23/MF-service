package com.example.mfservice.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mfservice.R;
import com.example.mfservice.model.Produto;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class GaleryActivity extends AppCompatActivity {

    private PhotoView pv_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galery);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        pv_image = findViewById(R.id.pv_image);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            Uri uri = Uri.parse(bundle.getString("foto"));
            toolbar.setTitle(bundle.getString("titulo"));
            Glide.with(getApplicationContext()).load(uri).into(pv_image);
            //Picasso.get().load(uri).into(pv_image);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
