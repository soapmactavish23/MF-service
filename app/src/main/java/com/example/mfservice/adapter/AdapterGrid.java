package com.example.mfservice.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.icu.lang.UProperty;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mfservice.R;
import com.example.mfservice.model.Cliente;
import com.example.mfservice.model.Produto;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

import static com.example.mfservice.R.*;
import static com.example.mfservice.R.drawable.padrao;

public class AdapterGrid extends ArrayAdapter<Cliente> {

    private Context context;
    private int layoutResource;
    private List<Cliente> listaClientes;

    public AdapterGrid(@NonNull Context context, int resource, @NonNull List<Cliente> objects) {
        super(context, resource, objects);
        this.context = context;
        this.layoutResource = resource;
        this.listaClientes = objects;
    }

    public class ViewHolder{
        ImageView img;
        ProgressBar progressBar;
        TextView txtTitulo;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(layoutResource, parent, false);
            viewHolder.progressBar = convertView.findViewById(id.progressBar);
            viewHolder.img = convertView.findViewById(id.img);
            viewHolder.txtTitulo = convertView.findViewById(id.txtTitulo);
            viewHolder.txtTitulo.setText(listaClientes.get(position).getNome());

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ImageLoader imageLoader = ImageLoader.getInstance();
        if(!listaClientes.get(position).getFoto().equals("")){
            imageLoader.displayImage(listaClientes.get(position).getFoto(), viewHolder.img, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    viewHolder.progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    viewHolder.progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    viewHolder.progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    viewHolder.progressBar.setVisibility(View.GONE);
                }
            });
        }else{
            viewHolder.progressBar.setVisibility(View.GONE);
            viewHolder.img.setImageResource(R.drawable.padrao);
        }

        return convertView;
    }
}
