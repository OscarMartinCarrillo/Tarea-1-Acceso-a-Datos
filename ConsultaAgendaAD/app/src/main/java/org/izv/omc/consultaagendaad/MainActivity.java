package org.izv.omc.consultaagendaad;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.izv.omc.consultaagendaad.settings.SettingsActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final int CONTACTS_PERMISSION = 1;
    private Button btSearch;
    private EditText etPhone;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_ajustes) {
            viewSettings();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CONTACTS_PERMISSION:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permiso
                    search();
                } else {
                    //sin permiso
                }
                break;
        }
        //request
        //permissons
        //grantResult

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void explain() {
        showRationaleDialog(getString(R.string.title),
                getString(R.string.message),
                Manifest.permission.READ_CONTACTS,
                CONTACTS_PERMISSION);
    }

    private void initialize() {
        btSearch = findViewById(R.id.btSearch);
        etPhone = findViewById(R.id.etPhone);
        tvResult = findViewById(R.id.tvResult);

        //Cargamos el contenido de las preferencias compartidas, si no hay se pone ""
        SharedPreferences prefe=getSharedPreferences("datos",Context.MODE_PRIVATE);
        tvResult.setText(prefe.getString("msg", ""));

        btSearch.setOnClickListener(view -> searchIfPermitted());

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermission() {
        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, CONTACTS_PERMISSION);
    }

    private void search() {
        //Para ocultar el teclado
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(btSearch.getWindowToken(), 0);

        //Sacamos los contactos del telefono con una consulta
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String proyeccion[] = new String[] {ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
        String orden = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
        Cursor cursor = getContentResolver().query(uri, proyeccion, null, null, orden);

        //Sacamos el numero de donde estan situados los datos que queremos
        int columnaNombre = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int columnaNumero = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

        //Sacamos todos los telefonos con el nombre, formamos el numero y lo comparamos con el introducido en el EditText
        String nombre, numero;
        ArrayList<String> match = new ArrayList<String>();
        while (cursor.moveToNext()){
            nombre=cursor.getString(columnaNombre);
            numero=cursor.getString(columnaNumero);
            if (etPhone.getText().toString().equals(soloNumero(numero))){
                match.add(nombre);
            }
        }

        //Creamos le mensaje del TextView
        String msg="";
        //Si hay mas de 0 coincidencias
        if (match.toArray().length > 0) {
            //Recorremos cada coincidencia y la concatenamos al string
            for (String s:match) {
                msg=msg.concat(" "+s);
            }
        }else{
            //Mensaje de que no hay coincidencias
            msg="No se ha encontrado el contacto";
        }

        //Preferencias compartidas para almacenar el ultimo registro
        SharedPreferences prefe=getSharedPreferences("datos",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=prefe.edit();
        editor.putString("msg", msg);
        //editor.commit(); me recomienda poner el apply en vez del commit
        editor.apply();

        //Seteamos el mensaje de salida al TextView
        tvResult.setText(msg);
    }

    private void searchIfPermitted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Android es posterior o igual que la 6.0 incluida
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_CONTACTS) ==
                    PackageManager.PERMISSION_GRANTED) {
                //Ya tengo el permiso
                search();

            } else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                explain();  //2º Ejecución
            } else {
                requestPermission(); //1º Ejecución
            }
        } else {
            //Android es anterior que la version 6.0
            //Ya tengo el permiso
            search();
        }
    }

    private void showRationaleDialog(String title, String message, String permission, int requestCode) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(title)
                .setMessage(message)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int wich) {
                        //Si dice que no, no hago nada
                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int wich) {
                        requestPermission();
                    }
                });

        builder.create().show();

    }
    
    public String soloNumero(String numero) {
        char [] cadena_div = numero.toCharArray();
        String n="";
        for (int i = 0; i < cadena_div.length; i++) {
            if (Character.isDigit(cadena_div[i])){
                n+=cadena_div[i];
            }
        }
        return n;
    }

    private void viewSettings() {
        //Intencion
        //intenciones explicitas o implicitas
        //explicita: ir del contexto actual a un contexto que se crea con la clase SettingsActivity
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
