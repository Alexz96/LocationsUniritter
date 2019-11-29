package br.com.uniritter.locationsuniritter.configs;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public final class ConfigFirebase {

    private static FirebaseAuth firebaseAutenticacao;
    private static DatabaseReference databaseReference;

    public static FirebaseAuth getFirebaseAutenticacao() {
        if (firebaseAutenticacao == null) {
            firebaseAutenticacao = FirebaseAuth.getInstance();
        }

        return firebaseAutenticacao;
    }

    public static DatabaseReference getFirebaseDatabase() {
        if(databaseReference == null) {
            databaseReference = FirebaseDatabase.getInstance().getReference();
        }

        return databaseReference;
    }

}
