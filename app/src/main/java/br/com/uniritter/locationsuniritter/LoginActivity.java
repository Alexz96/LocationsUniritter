package br.com.uniritter.locationsuniritter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import br.com.uniritter.locationsuniritter.configs.ConfigFirebase;

public class LoginActivity extends Activity {

    private EditText editEmail;
    private EditText editSenha;
    private Button btnLogin;

    // Objeto utilizado para instanciar o login no Firebase
    private FirebaseAuth auth = ConfigFirebase.getFirebaseAutenticacao();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Localização dos componentes da tela
        editEmail = (EditText) findViewById(R.id.edit_email);
        editSenha = (EditText) findViewById(R.id.edit_pass);
        btnLogin = (Button) findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validaCampos(editEmail.getText().toString(), editSenha.getText().toString());
            }
        });
    }

    public boolean validaCampos(String email, String senha) {

        boolean loginValido = false;

        if (TextUtils.isEmpty(email)) {
            editEmail.setError("Preencha o email!");
        } else if (TextUtils.isEmpty(senha)) {
            editSenha.setError("Por favor, preencha a senha!");
        } else {
            loginValido = true;
            efetuaLogin(email, senha);
        }

        return loginValido;
    }

    public void efetuaLogin(String emailP, String senhaP) {
        auth.signInWithEmailAndPassword(emailP, senhaP)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Login efetuado com sucesso!", Toast.LENGTH_SHORT).show();
                            Intent intentMain = new Intent(getApplicationContext(), SalvarPosicaoActivity.class);
                            startActivity(intentMain);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Falha ao se logar: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
