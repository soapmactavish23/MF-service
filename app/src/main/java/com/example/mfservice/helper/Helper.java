package com.example.mfservice.helper;

import android.content.Context;
import android.widget.Toast;

public class Helper {

    private Context context;

    public Helper(Context context) {
        this.context = context;
    }

    public void exibirMensagem(String msg){
        Toast.makeText(
                context,
                msg,
                Toast.LENGTH_SHORT
        ).show();
    }

}
