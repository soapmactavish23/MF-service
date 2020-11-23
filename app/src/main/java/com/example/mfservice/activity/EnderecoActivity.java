package com.example.mfservice.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.bumptech.glide.load.resource.bitmap.BitmapDrawableResource;
import com.example.mfservice.helper.Helper;
import com.example.mfservice.model.ProdutoOrcamento;
import com.example.mfservice.model.Usuario;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mfservice.R;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class EnderecoActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng meuLocal;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private EditText editEndereco;
    private EditText editMeuLocal;
    private Usuario usuario;
    private Marker marcadorMeuLocal, marcadorMinhaCasa;
    private Helper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endereco);
        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        //Inicializar Componentes
        editEndereco = findViewById(R.id.editEndereco);
        editMeuLocal = findViewById(R.id.editMeuLocal);
        Bundle bundle = getIntent().getExtras();
        usuario = (Usuario) bundle.getSerializable("DadosUsuario");
        helper = new Helper(this);

        //Inicializar mapas
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        editMeuLocal.setText("Carregando...");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        recuperarLocalizacaoUsuario();
        mMap = googleMap;
    }

    public void salvarEndereco(View view){
        try{
            String txtEndereco = editEndereco.getText().toString();
            if(!txtEndereco.isEmpty() || !txtEndereco.equals("")){
                Address endereco = recuperarEndereco(txtEndereco);
                if(endereco != null){
                    alertar(endereco);
                }
            }else{
                Address endereco = recuperarMeuEndereco();
                if(endereco != null){
                    alertar(endereco);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void alertar(final Address endereco){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EnderecoActivity.this);
        alertDialog.setTitle("Salvar seu endereço");

        View viewInput = getLayoutInflater().inflate(R.layout.dialog_input, null);
        final TextInputEditText inputEndereco = viewInput.findViewById(R.id.editLocal);
        inputEndereco.setText(endereco.getAddressLine(0));

        alertDialog.setView(viewInput);
        alertDialog.setCancelable(true);
        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, final int i) {
                String end = inputEndereco.getText().toString();
                if(end.isEmpty() || end.equals("")){
                    helper.exibirMensagem("Peencha o campo de endereço");
                }else{
                    usuario.setEndereco(end);
                    usuario.atualizar();
                    helper.exibirMensagem("Endereço Salvo com Sucesso");
                }
            }
        });
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    private Address recuperarMeuEndereco(){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> listEnderecos = geocoder.getFromLocation(meuLocal.latitude, meuLocal.longitude, 1);
            Address address = listEnderecos.get(0);
            return address;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Address recuperarEndereco(String endereco){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> listEnderecos = geocoder.getFromLocationName(endereco, 1);
            if(listEnderecos != null && listEnderecos.size() > 0){
                Address address = listEnderecos.get(0);
                return address;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void recuperarLocalizacaoUsuario() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                meuLocal = new LatLng(latitude, longitude);
                mMap.clear();
                Address address = recuperarMeuEndereco();
                marcadorMeuLocal = mMap.addMarker(
                        new MarkerOptions().position(meuLocal)
                                .title("Meu Local")
                                .snippet(address.getAddressLine(0))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                );

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(meuLocal, 15));
                editMeuLocal.setText("Meu Local");

                if(!usuario.getEndereco().equals("")){
                    Address casa = recuperarEndereco(usuario.getEndereco());
                    LatLng minhaCasa = new LatLng(casa.getLatitude(), casa.getLongitude());
                    marcadorMinhaCasa = mMap.addMarker(
                            new MarkerOptions()
                            .position(minhaCasa)
                            .title("Minha Casa")
                            .snippet(casa.getAddressLine(0))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.home))
                    );
                    centralizarDoisMarcadores(marcadorMeuLocal, marcadorMinhaCasa);
                }
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    10000,
                    10,
                    locationListener
            );
        }
    }

    private void centralizarDoisMarcadores(Marker marker1, Marker marker2){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(marker1.getPosition());
        builder.include(marker2.getPosition());

        LatLngBounds bounds = builder.build();

        int largura = getResources().getDisplayMetrics().widthPixels;
        int altura = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (largura * 0.40);

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, largura, altura, padding));

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
