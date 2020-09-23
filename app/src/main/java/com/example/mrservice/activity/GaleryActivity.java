package com.example.mrservice.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.net.Uri;
import android.os.Bundle;

import com.example.mrservice.R;
import com.example.mrservice.model.Produto;
import com.veinhorn.scrollgalleryview.ScrollGalleryView;
import com.veinhorn.scrollgalleryview.builder.GallerySettings;

import java.util.List;

import static com.veinhorn.scrollgalleryview.loader.picasso.dsl.DSL.image;

public class GaleryActivity extends FragmentActivity {
    private ScrollGalleryView galleryView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galery);

        galleryView = ScrollGalleryView.from((ScrollGalleryView) findViewById(R.id.scroll_gallery_view))
                .settings(
                        GallerySettings
                                .from(getSupportFragmentManager())
                                .thumbnailSize(100)
                                .enableZoom(true)
                                .build()
                )
                .add(image("http://pirate-islands.com/wp-content/uploads/2018/07/07_Dom-Fernando-II_01-636x310.jpg"))
                .add(image("http://povodu.ru/wp-content/uploads/2016/04/pochemu-korabl-derzitsa-na-vode.jpg")).build();
    }
}
