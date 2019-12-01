package br.com.uniritter.locationsuniritter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import br.com.uniritter.locationsuniritter.configs.ConfigFirebase;

public class UltimasPosicaoActivity extends Activity {

    // Atributos para gerenciamento de dados no Firebase
    private FirebaseAuth firebaseAuth = ConfigFirebase.getFirebaseAutenticacao();
    private DatabaseReference databaseRef = ConfigFirebase.getFirebaseDatabase();

    // Componentes do Android
    private TextView posicao1, dataPos1;
    private TextView posicao2, dataPos2;
    private TextView posicao3, dataPos3;
    private TextView posicao4, dataPos4;
    private TextView posicao5, dataPos5;

    // Objeto Map para recebimento dos dados
    private Map<String, Object> dados = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ultimas_posicao);

        // Localiza os componentes do Android da tela
        posicao1 = findViewById(R.id.pos1);
        dataPos1 = findViewById(R.id.datapos1);
        posicao2 = findViewById(R.id.pos2);
        dataPos2 = findViewById(R.id.datapos2);
        posicao3 = findViewById(R.id.pos3);
        dataPos3 = findViewById(R.id.datapos3);
        posicao4 = findViewById(R.id.pos4);
        dataPos4 = findViewById(R.id.datapos4);
        posicao5 = findViewById(R.id.pos5);
        dataPos5 = findViewById(R.id.datapos5);

        // Define o listener para atualizações dos dados e busca deles
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //TODO: AJUSTAR CÓDIGO, POIS APP ESTÁ CRASHANDO AO BUSCAR OS DADOS
                //FALHA NA CONVERSÃO DO MAP PARA STRING
                // BUSCANDO DADOS PARA TESTE DE FORMA ESTÁTICA
                dados.put("datapos1", dataSnapshot.child("01-12-2019-15:55:00").getValue(String.class));
                dados.put("posicao1", dataSnapshot.child("01-12-2019-15:55:00").child("lati").getValue(String.class) +
                        ", " + dataSnapshot.child("01-12-2019-15:55:00").child("long").getValue(String.class));

                dataPos1.setText("Data da primeira posição: " + dados.get("datapos1"));
                posicao1.setText("Posição 1 (Latitude + Longitude): " + dados.get(posicao1));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Realtime", "Erro ao ler do banco" + databaseError.getMessage());
            }
        };

        if (firebaseAuth.getCurrentUser() != null) {
            databaseRef.child("localizacao")
                    .child(firebaseAuth.getUid())
                    .addValueEventListener(eventListener);
        } else {

        }
    }
}
