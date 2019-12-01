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
import br.com.uniritter.locationsuniritter.configs.ConfigGPS;

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
                // Utiliza classe criada para facilitar salvamento da posição no banco
                ConfigGPS configGPS = new ConfigGPS(referencia, SalvarPosicaoActivity.this);
                configGPS.configurarServico();
            }
        });

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        for(String provider : locationManager.getAllProviders()){
            Toast.makeText(getApplicationContext(), provider, Toast.LENGTH_LONG).show();
        }

        createLocationRequest();
        pedirPermissoes();
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

    public void pedirPermissoes() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else
            new ConfigGPS(referencia, this).configurarServico();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
}
