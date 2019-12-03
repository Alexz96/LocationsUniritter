package br.com.uniritter.locationsuniritter.configs;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ConfigGPS {

    // Objeto para salvar os dados no Firebase
    private Map<String, Object> user = new HashMap<>();
    // Atributos para manuseio do Firebase
    private DatabaseReference databaseReference = ConfigFirebase.getFirebaseDatabase();
    private DatabaseReference referencia;
    private FirebaseAuth auth = ConfigFirebase.getFirebaseAutenticacao();
    private Activity activity;

    public ConfigGPS(DatabaseReference referencia, Activity activity) {
        this.referencia = referencia;
        this.activity = activity;
    }


    public void configurarServico(){
        try {
            LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    atualizar(location);
                }

                public void onStatusChanged(String provider, int status, Bundle extras) { }

                public void onProviderEnabled(String provider) { }

                public void onProviderDisabled(String provider) { }
            };

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        }catch(SecurityException ex){
            Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void atualizar(Location location)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-HH:mm:ss");
        SimpleDateFormat dateFormat_minutos = new SimpleDateFormat("mm");
        SimpleDateFormat dateFormat_seg = new SimpleDateFormat("ss");

        Date data = new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        Date data_atual = cal.getTime();

        String data_completa = dateFormat.format(data_atual);
        String min_atual = dateFormat_minutos.format(data_atual);
        String segs = dateFormat_seg.format(data_atual);

        Double latPoint = location.getLatitude();
        Double lngPoint = location.getLongitude();

        String latitude = latPoint.toString();
        String longitude = lngPoint.toString();

        int min = Integer.parseInt(String.valueOf(min_atual));
        int ss =  Integer.parseInt(String.valueOf(segs));


        if((min%5) == 0 && (ss==00)) {
            //TODO: SALVA A CADA 5 min.

            user.put("latitude", latitude);
            user.put("longitude", longitude);

            databaseReference
                    .child("localizacao")
                    .child(auth.getUid())
                    .child(data_completa).setValue(user);
        }
    }
}
