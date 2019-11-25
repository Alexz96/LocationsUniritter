package br.com.uniritter.locationsuniritter.configs;

import com.google.firebase.auth.FirebaseAuth;

public final class ConfigFirebase {

    private static FirebaseAuth firebaseAutenticacao;

    public static FirebaseAuth getFirebaseAutenticacao() {
        if (firebaseAutenticacao == null) {
            firebaseAutenticacao = FirebaseAuth.getInstance();
        }

        return firebaseAutenticacao;
    }

}
