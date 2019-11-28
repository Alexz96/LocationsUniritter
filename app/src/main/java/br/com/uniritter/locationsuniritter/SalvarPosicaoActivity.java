package br.com.uniritter.locationsuniritter;
import br.com.uniritter.locationsuniritter.configs.ConfigFirebase;


import android.util.Log;
import android.Manifest;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SalvarPosicaoActivity extends Activity {

    private FirebaseAuth auth = ConfigFirebase.getFirebaseAutenticacao();
    private DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();
    private static final int REQUEST_CHECK_SETTINGS = 613;
    private LocationRequest mLocationRequest;
    private Button btnSalvarPosicao;
    private Button btnMostrarPosicao;
    private TextView btnlogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salvar_posicao);

        btnMostrarPosicao = (Button) findViewById(R.id.id_ultimas_posicao);
        btnSalvarPosicao = (Button) findViewById(R.id.id_salvar_posicao);
        btnlogout = (TextView) findViewById(R.id.id_log_off);

        btnMostrarPosicao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SalvarPosicaoActivity.this, UltimasPosicaoActivity.class);
                startActivity(intent);
            }
        });

        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SalvarPosicaoActivity.this, LoginActivity.class);

                //Verifica usuario logado
                if( auth.getCurrentUser() != null ){
                    auth.signOut();
                }

                startActivity(intent);
                finish();
            }
        });

        btnSalvarPosicao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //fazer o salva mento
            }
        });

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        for(String provider : locationManager.getAllProviders()){
            Toast.makeText(getApplicationContext(), provider, Toast.LENGTH_LONG).show();
        }

        createLocationRequest();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SalvarPosicaoActivity.this, LoginActivity.class);

        //Verifica usuario logado
        if( auth.getCurrentUser() != null ){
            auth.signOut();
        }

        startActivity(intent);
        finish();
    }

    //////////////////
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(100000);
        mLocationRequest.setFastestInterval(50000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    private void pedirPermissoes() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else
            configurarServico();
    }


    ////////////////////////////////////////////////

    public void configurarServico(){
        try {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    atualizar(location);
                }

                public void onStatusChanged(String provider, int status, Bundle extras) { }

                public void onProviderEnabled(String provider) { }

                public void onProviderDisabled(String provider) { }
            };
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (android.location.LocationListener) locationListener);
        }catch(SecurityException ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void atualizar(Location location)
    {
        SimpleDateFormat dateFormatbra = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-HH:mm:ss");
        SimpleDateFormat dateFormat_hora = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat dateFormat_minutos = new SimpleDateFormat("mm");
        SimpleDateFormat dateFormat_seg = new SimpleDateFormat("ss");
        Date data = new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        Date data_atual = cal.getTime();

        String data_completa = dateFormat.format(data_atual);
        String hora_atual = dateFormat_hora.format(data_atual);
        String min_atual = dateFormat_minutos.format(data_atual);
        String segs = dateFormat_seg.format(data_atual);
        String databra = dateFormatbra.format(data_atual);


        Log.i("data_completa", data_completa);
        Log.i("data_atual", data_atual.toString());
        Log.i("hora_atual", hora_atual); // Esse é o que você quer


        Double latPoint = location.getLatitude();
        Double lngPoint = location.getLongitude();

        String latitude = latPoint.toString();
        // Log.i("GPSLATITUDE:", latitude);
        String longitude = lngPoint.toString();
        // Log.i("GPSLONGITUDE:", longitude);

        String coordenadas = latitude + "#" + longitude;

        int min = Integer.parseInt(String.valueOf(min_atual));
        int ss =  Integer.parseInt(String.valueOf(segs));
        Log.i("GPSLONGITUDE:", segs);

        if((min%2)==0 && (ss==59)) {
            DatabaseReference produtos = referencia.child(databra);
            produtos.child(data_completa).setValue(coordenadas);
            Log.i("GPSLONGITUDE:", coordenadas);
            Log.i("GPSLONGITUDE:", data_completa);
        }
    }
}
