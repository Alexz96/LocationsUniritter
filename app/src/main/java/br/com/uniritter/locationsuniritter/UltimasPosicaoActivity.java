package br.com.uniritter.locationsuniritter;

import androidx.annotation.NonNull;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.LocationRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.core.app.ActivityCompat;
import br.com.uniritter.locationsuniritter.configs.ConfigFirebase;
import br.com.uniritter.locationsuniritter.configs.ConfigGPS;

public class UltimasPosicaoActivity extends Activity {

    // Atributos para gerenciamento de dados no Firebase
    private FirebaseAuth firebaseAuth = ConfigFirebase.getFirebaseAutenticacao();
    private DatabaseReference databaseRef = ConfigFirebase.getFirebaseDatabase();

    private ArrayList<String> datapos = new ArrayList<String>();
    private ArrayList<String> valores = new ArrayList<String>();

    // Componentes do Android
    private FirebaseAuth auth = ConfigFirebase.getFirebaseAutenticacao();
    private TextView dataPos1, posicao1;
    private TextView dataPos2, posicao2;
    private TextView dataPos3, posicao3;
    private TextView dataPos4, posicao4;
    private TextView dataPos5, posicao5;
    private TextView logoutUltimaPos;
    private LocationRequest mLocationRequest;
    private DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ultimas_posicao);

        // Localiza os componentes do Android da tela
        dataPos1 = findViewById(R.id.datapos1);
        posicao1 = findViewById(R.id.pos1);
        dataPos2 = findViewById(R.id.datapos2);
        posicao2 = findViewById(R.id.pos2);
        dataPos3 = findViewById(R.id.datapos3);
        posicao3 = findViewById(R.id.pos3);
        dataPos4 = findViewById(R.id.datapos4);
        posicao4 = findViewById(R.id.pos4);
        dataPos5 = findViewById(R.id.datapos5);
        posicao5 = findViewById(R.id.pos5);
        logoutUltimaPos = findViewById(R.id.id_logoutultimaposicao);

        createLocationRequest();
        pedirPermissoes();

        logoutUltimaPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UltimasPosicaoActivity.this, LoginActivity.class);

                //Verifica usuario logado
                if( auth.getCurrentUser() != null ){
                    auth.signOut();
                }

                startActivity(intent);
                finish();
            }
        });

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    Map<String, Object> dados = (Map<String, Object>) dataSnapshot.getValue();
                    int index = 0;

                    //Ordena as datas
                    for(String item : dados.keySet()){
                        datapos.add(item);
                    }

                    Collections.sort(datapos);

                    //Pega o indice das datas para achar a longitude e latitude
                    for(int i = datapos.size(); i > 0; i--){
                        if(index >= 5){
                            break;
                        }
                        valores.add(datapos.get(i-1));
                        valores.add(dados.get(datapos.get(i-1)).toString().toUpperCase()
                                .replace("{", "")
                                .replace("}", "")
                                .replace("=", " ")
                                .replace(",", "\n")
                        );
                        index++;
                    }

                    visibilidadeCampos(View.VISIBLE);

                    if(valores.size() > 1){
                        posicao1.setText(valores.get(0));
                        dataPos1.setText(valores.get(1));
                    }else{
                        posicao1.setVisibility(View.INVISIBLE);
                        dataPos1.setVisibility(View.INVISIBLE);
                    }
                    if(valores.size() > 3){
                        posicao2.setText(valores.get(2));
                        dataPos2.setText(valores.get(3));
                    }else{
                        posicao2.setVisibility(View.INVISIBLE);
                        dataPos2.setVisibility(View.INVISIBLE);
                    }
                    if(valores.size() > 5){
                        posicao3.setText(valores.get(4));
                        dataPos3.setText(valores.get(5));
                    }else{
                        posicao3.setVisibility(View.INVISIBLE);
                        dataPos3.setVisibility(View.INVISIBLE);
                    }
                    if(valores.size() > 7){
                        posicao4.setText(valores.get(6));
                        dataPos4.setText(valores.get(7));
                    }else{
                        posicao4.setVisibility(View.INVISIBLE);
                        dataPos4.setVisibility(View.INVISIBLE);
                    }
                    if(valores.size() > 9){
                        posicao5.setText(valores.get(8));
                        dataPos5.setText(valores.get(9));
                    }else{
                        posicao5.setVisibility(View.INVISIBLE);
                        dataPos5.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("buscaUsuarios", "ERRO AO BUSCAR USUÁRIOS!");
            }
        };

        databaseRef.child("localizacao").child(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(valueEventListener);

        visibilidadeCampos(View.INVISIBLE);
        posicao1.setText("Nenhuma localização no banco de dados!");

    }

    private void visibilidadeCampos(int v){
        dataPos1.setVisibility(v);
        posicao2.setVisibility(v);
        dataPos2.setVisibility(v);
        posicao3.setVisibility(v);
        dataPos3.setVisibility(v);
        posicao4.setVisibility(v);
        dataPos4.setVisibility(v);
        posicao5.setVisibility(v);
        dataPos5.setVisibility(v);
    }

    public void pedirPermissoes() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else
            new ConfigGPS(referencia, this).configurarServico(false);
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
}
