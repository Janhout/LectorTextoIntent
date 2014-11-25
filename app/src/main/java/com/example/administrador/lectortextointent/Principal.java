package com.example.administrador.lectortextointent;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;


public class Principal extends Activity {

    private EditText etTexto;
    private String textoOriginal;
    private String ruta;
    private AlertDialog alerta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        etTexto = (EditText)findViewById(R.id.etTexto);
        textoOriginal = "";
        leerFichero();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        aceptar(null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(alerta != null){
            alerta.dismiss();
        }
    }

    public void aceptar(View v){
        String textoFinal = etTexto.getText().toString();
        if(!textoFinal.equals(textoOriginal)){
            confirmacion();
        }else{
            Toast.makeText(this, getString(R.string.sinCambios), Toast.LENGTH_SHORT).show();
        }
    }

    public void cancelar(View v){
        finish();
    }

    private void confirmacion(){
        AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
        dialogo.setTitle(getString(R.string.titulo_conf));
        dialogo.setMessage(getString(R.string.confirmacion));
        dialogo.setCancelable(true);
        dialogo.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo, int id) {
                FileWriter fichero = null;
                PrintWriter pw = null;
                try {
                    fichero = new FileWriter(ruta);
                    pw = new PrintWriter(fichero);
                    pw.println(Principal.this.etTexto.getText().toString());
                    pw.close();
                    fichero.close();
                    finish();
                } catch (IOException e){
                    Toast.makeText(Principal.this, getString(R.string.errorEscritura), Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialogo.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo, int id) {
               dialogo.dismiss();
            }
        });
        alerta = dialogo.create();
        alerta.show();
    }

    private void escribirContenido(URI datos){
        ruta = datos.getPath();
        try {
            FileInputStream fIn = new FileInputStream(ruta);
            InputStreamReader archivo = new InputStreamReader(fIn, getString(R.string.codi));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            StringBuilder todo = new StringBuilder("");
            while (linea != null) {
                todo.append(linea + "\n");
                linea = br.readLine();
            }
            br.close();
            archivo.close();
            etTexto.setText(todo);
            textoOriginal = todo.toString();
        } catch (IOException e) {
            Toast.makeText(this, getString(R.string.errorLectura), Toast.LENGTH_SHORT).show();
        }
    }

    private void leerFichero(){
        Intent intent = getIntent();
        Uri data = intent.getData();
        if(data!=null) {
            URI datos = null;
            try {
                datos = new URI(data.toString());
            } catch (URISyntaxException e) {
            }
            if (datos != null) {
                escribirContenido(datos);
            }
        }
    }

}
