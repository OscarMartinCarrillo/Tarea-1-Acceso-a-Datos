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
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.UserDictionary;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.izv.omc.consultaagendaad.settings.SettingsActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "xyzyx";
    /*

        No podemos hacer referencia a elementos que no se han creado

        private Button btSearch = findViewById(R.id.btSearch);
        private EditText etPhone = findViewById(R.id.etPhone);
        private TextView tvResult = findViewById(R.id.tvResult);
        */
    private final int CONTACTS_PERMISSION = 1;

    private Button btSearch;
    private EditText etPhone;
    private TextView tvResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.v(TAG, "onCreate");//verbose
        initialize();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy");//verbose

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
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause");//verbose

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.v(TAG, "onRequestPermissionsResult");//verbose
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

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v(TAG, "onRestart");//verbose
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");//verbose

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart");//verbose
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");//verbose
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

        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchIfPermitted();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermission() {
        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, CONTACTS_PERMISSION);
    }

    private void search() {
        tvResult.setText("A pelo ya si");
        //Buscar entre los contactos
        //ContentProvider -> Proveedor de contenidos
        /*
        Cursor cursor = getContentResolver().query(
                UserDictionary.Words.CONTENT_URI,       //Ruta de la URI que queremos obtener, nos devuelve una tabla
                new String[] {"projection"},            //Como en sql filtrar las columnas que obtenemos de la consulta, el select
                "selectionClause",             //El where preparado de sql(mirar debajo)
                new String[]{"selectionArgs"},          //El WHERE de SQL
                "sortOrder");                  //El SORT de SQL
        */
        /*
        Cursor cursor = getContentResolver().query(
                UserDictionary.Words.CONTENT_URI,       //Ruta de la URI que queremos obtener, nos devuelve una tabla
                new String[] {"projection"},            //Como en sql filtrar las columnas que obtenemos de la consulta
                "campo1 = ? and campo2 < ? or campo3 = ? ",              //Selection criteria
                new String[]{"pepe", "3", "23"},          //Selection criteria
                "campo1", "campo2");

         */

        /*
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String proyeccion[] = proyeccion=new String[] {ContactsContract.Contacts.DISPLAY_NAME};; //Quiero todos los campos
        String seleccion = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = ? and " +
                ContactsContract.Contacts.HAS_PHONE_NUMBER + "= ?";
        String argumentos[] = new String[]{"1","1"};
        seleccion=null; //Quitamos el where
        argumentos=null;//Quitamos los argumentos porque no hay WHERE
        String orden = ContactsContract.Contacts.DISPLAY_NAME + " collate localized asc";
        Cursor cursor = getContentResolver().query(uri, proyeccion, seleccion, argumentos, orden);
        String[] columnas = cursor.getColumnNames();
        for(String s: columnas){
            Log.v(TAG, s);
        }
        String displayName;
        int columna = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        while (cursor.moveToNext()){
            displayName = cursor.getString(columna);
            Log.v(TAG, displayName);
        }
        */

        Uri uri2 = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String proyeccion2[] = new String[] {ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
        String seleccion2 = null;//ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
        String argumentos2[] = null;//new String[]{id+""};
        String orden2 = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
        Cursor cursor2 = getContentResolver().query(uri2, proyeccion2, seleccion2, argumentos2, orden2);

        String[] columnas2 = cursor2.getColumnNames();
        for(String s: columnas2){
            Log.v(TAG, s);
        }
        int columnaNombre = cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int columnaNumero = cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        String nombre, numero;
        ArrayList<String> match = new ArrayList<String>();
        while (cursor2.moveToNext()){
            nombre=cursor2.getString(columnaNombre);
            numero=cursor2.getString(columnaNumero);
            Log.v(TAG, "Nombre: "+nombre + " Numero: " + soloNumero(numero));

            if (etPhone.getText().toString().equals(soloNumero(numero))){
                match.add(nombre);
            }
        }

        String msg="";
        if (match.toArray().length > 0) {
            for (String s:match) {
                msg+=" "+s;
            }
        }else{
            msg="No se ha encontrado el contacto";
        }
        //Para ocultar el teclado
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(btSearch.getWindowToken(), 0);

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
        //intenciones expliccitas o implicitas
        //explicita: ir del contexto actual a un contexto que se crea con la clase SettingsActivity
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
