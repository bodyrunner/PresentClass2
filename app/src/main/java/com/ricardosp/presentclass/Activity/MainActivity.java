package com.ricardosp.presentclass.Activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ricardosp.presentclass.Classes.Usuario;
import com.ricardosp.presentclass.DAO.ConfiguracaoFirebase;
import com.ricardosp.presentclass.Helper.Preferencias;
import com.ricardosp.presentclass.R;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private BootstrapEditText edtEmailLogin;
    private BootstrapEditText edtSenhaLogin;
    private BootstrapButton btnLogin;
    private Usuario usuario;
    private TextView txtRecuperarSenha;
    private AlertDialog alerta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtEmailLogin = (BootstrapEditText) findViewById(R.id.edtEmail);
        edtSenhaLogin = (BootstrapEditText) findViewById(R.id.edtSenha);
        btnLogin = (BootstrapButton) findViewById(R.id.btnLogin);
        txtRecuperarSenha = (TextView) findViewById(R.id.txtRecuperarSenha);

        final EditText editTextEmail = new EditText(MainActivity.this);
        editTextEmail.setHint("exemplo@exemplo.com");


        permissoes();

        if (usuarioLogado()){
            Intent intentMinhaConta = new Intent(MainActivity.this, PrincipalActivity.class);
            abrirNovaActivity(intentMinhaConta);

        }
        else {

            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!edtEmailLogin.getText().toString().equals("") && !edtSenhaLogin.getText().toString().equals("")) {

                        usuario = new Usuario();

                        usuario.setEmail(edtEmailLogin.getText().toString());
                        usuario.setSenha(edtSenhaLogin.getText().toString());

                        validarLogin();


                    } else {
                        Toast.makeText(MainActivity.this, "Preencha os campos de E-mail e senha", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        txtRecuperarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setCancelable(false);

                builder.setTitle("Recuperar senha");

                builder.setMessage("Informe o seu e-mail");

                builder.setView(editTextEmail);

                if (!editTextEmail.getText().equals("")){
                    builder.setPositiveButton("Recuperar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            autenticacao = FirebaseAuth.getInstance();

                            String emailRecuperar = editTextEmail.getText().toString();



                            autenticacao.sendPasswordResetEmail(emailRecuperar).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){

                                        Toast.makeText(MainActivity.this, "Logo você receberá uma mensagem em seu email!", Toast.LENGTH_SHORT).show();

                                        Intent intent =  getIntent();
                                        finish();
                                        startActivity(intent);

                                    }else{
                                        Toast.makeText(MainActivity.this, "Falha ao enviar mensagem ao e-mail!", Toast.LENGTH_SHORT).show();

                                        Intent intent =  getIntent();
                                        finish();
                                        startActivity(intent);
                                    }

                                }
                            });

                        }
                    });

                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            Intent intent =  getIntent();
                            finish();
                            startActivity(intent);

                        }
                    });



                }else{
                    Toast.makeText(MainActivity.this, "Preencha o campo de e-mail", Toast.LENGTH_SHORT).show();
                }

                alerta =  builder.create();

                alerta.show();
            }
        });
    }

    private void validarLogin() {
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        autenticacao.signInWithEmailAndPassword(usuario.getEmail().toString(), usuario.getSenha().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    abrirTelaPrincipal();

                    Preferencias preferencias = new Preferencias(MainActivity.this);
                    preferencias.salvarUsuarioPreferencias(usuario.getEmail(), usuario.getSenha());
                    Toast.makeText(MainActivity.this, "Login efetuado com sucesso!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Usuário ou senha inválidos! Tente novamente!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void abrirTelaPrincipal() {

        Intent intent = new Intent(MainActivity.this, PrincipalActivity.class);
        finish();
        startActivity(intent);
    }

    public boolean usuarioLogado (){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user!=null){
            return true;
        }
        else{
            return false;
        }


    }

    public void abrirNovaActivity(Intent intent){

        startActivity(intent);
    }

    public  void permissoes(){
        int PERMISSION_ALL = 1;

        String [] PERMISSION = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this, PERMISSION, PERMISSION_ALL);
    }


}
