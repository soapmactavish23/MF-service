package com.example.mfservice.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.service.controls.actions.FloatAction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mfservice.R;
import com.example.mfservice.adapter.AdapterItem;
import com.example.mfservice.adapter.AdapterServicos;
import com.example.mfservice.config.ConfiguracaoFirebase;
import com.example.mfservice.config.UsuarioFirebase;
import com.example.mfservice.helper.RecyclerItemClickListener;
import com.example.mfservice.model.ItemServico;
import com.example.mfservice.model.ServicoOrcamento;
import com.example.mfservice.model.Usuario;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ServicosFragment extends Fragment {

    private CircleImageView foto;
    private TextView txtNome, txtEndereco, txtEmail, txtContato, txtL, txtNoneServico;
    private Usuario usuarioLogado;
    private DatabaseReference usuarioRef, servicosRef, firebaseRef, servicoOrcamentoRef;
    private ValueEventListener valueEventListener;
    private List<ItemServico> itemServicos = new ArrayList<>();
    private AdapterServicos adapterServicos;
    private RecyclerView recyclerView;
    private FloatingActionButton btnExcluir;

    public ServicosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_servicos, container, false);

        //Inicializando componentes visuais
        foto = view.findViewById(R.id.imgFotoCliente);
        txtNome = view.findViewById(R.id.txtNome);
        txtEndereco = view.findViewById(R.id.txtEndereco);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtContato = view.findViewById(R.id.txtContato);
        txtNoneServico = view.findViewById(R.id.txtNoneServico);
        recyclerView = view.findViewById(R.id.recyclerServicos);
        btnExcluir = view.findViewById(R.id.btnExcluir);

        //Inicializando componentes firebase
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        usuarioRef = firebaseRef.child("usuarios").child(UsuarioFirebase.getIdentificadorUsuario());
        servicosRef = firebaseRef.child("itensServico").child(UsuarioFirebase.getIdentificadorUsuario());
        servicoOrcamentoRef = firebaseRef.child("servicoOrcamento").child(UsuarioFirebase.getIdentificadorUsuario());

        //Carregar dados do usuario
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usuarioLogado = snapshot.getValue(Usuario.class);
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.padrao);
                Glide.with(getActivity()).applyDefaultRequestOptions(requestOptions).load(usuarioLogado.getFoto()).into(foto);
                txtNome.setText(usuarioLogado.getNome());
                if(usuarioLogado.getEndereco().equals("")){
                    txtEndereco.setText("Endereço: SALVE SEU ENDEREÇO");
                }else{
                    txtEndereco.setText("Endereço: "+usuarioLogado.getEndereco());
                }
                txtEmail.setText("E-mail" + usuarioLogado.getEmail());
                txtContato.setText("Contato: " + usuarioLogado.getContato());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Configurar o recycler
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        adapterServicos = new AdapterServicos(getActivity(), itemServicos);
        recyclerView.setAdapter(adapterServicos);

        //Toque no recycler
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(),
                recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));

        btnExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                excluirOrcamento();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarOrcamentos();
    }

    @Override
    public void onStop() {
        super.onStop();
        itemServicos.clear();
        servicosRef.removeEventListener(valueEventListener);
    }

    private void recuperarOrcamentos(){
        itemServicos.clear();
        valueEventListener = servicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    ItemServico itemServico = ds.getValue(ItemServico.class);
                    itemServicos.add(itemServico);
                }
                if(itemServicos.size() > 0){
                    txtNoneServico.setVisibility(View.GONE);
                }
                adapterServicos.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void excluirOrcamento(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Excluir");
        alertDialog.setMessage("Tem certeza que deseja excluir esse orçamento? Após isso não todos os itens serão removidos");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                servicosRef.removeValue();
                servicoOrcamentoRef.removeValue();
                getActivity().finish();
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

}
