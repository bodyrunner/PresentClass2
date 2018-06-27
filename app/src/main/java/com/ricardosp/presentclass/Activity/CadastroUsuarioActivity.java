package com.ricardosp.presentclass.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ricardosp.presentclass.Classes.Usuario;
import com.ricardosp.presentclass.DAO.ConfiguracaoFirebase;
import com.ricardosp.presentclass.Helper.Preferencias;
import com.ricardosp.presentclass.R;


public class CadastroUsuarioActivity extends AppCompatActivity {

    private BootstrapEditText email;
    private BootstrapEditText senha1;
    private BootstrapEditText senha2;
    private BootstrapEditText nome;
    private RadioButton rbProfessor;
    private RadioButton rbAluno;
    private BootstrapButton btnCadastrar;
    private BootstrapButton btnCancelar;
    private FirebaseAuth autenticacao;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private Usuario usuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);

        email = (BootstrapEditText)findViewById(R.id.edtCadEmail);
        senha1 = (BootstrapEditText)findViewById(R.id.edtCadSenha1);
        senha2 = (BootstrapEditText)findViewById(R.id.edtCadSenha2);
        nome = (BootstrapEditText)findViewById(R.id.edtCadNome);
        rbAluno = (RadioButton) findViewById(R.id.rbAluno);
        rbProfessor = (RadioButton) findViewById(R.id.rbProfessor);
        btnCadastrar = (BootstrapButton) findViewById(R.id.btnCadastrar);
        btnCancelar = (BootstrapButton) findViewById(R.id.btnCancelar);


        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (senha1.getText().toString().equals(senha2.getText().toString())){
                    usuario = new Usuario();

                    usuario.setEmail(email.getText().toString());
                    usuario.setSenha(senha1.getText().toString());
                    usuario.setNome(nome.getText().toString());

                    if (rbProfessor.isChecked()){
                        usuario.setTipoUsuario("Professor");
                    }else if (rbAluno.isChecked()){
                        usuario.setTipoUsuario("Aluno");
                    }
                    cadastrarUsuario();
                }else{
                    Toast.makeText(CadastroUsuarioActivity.this, "As senhas devem ser iguais!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chamaTelaPrincipal();
            }
        });

    }

    private void cadastrarUsuario(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        autenticacao.createUserWithEmailAndPassword(

                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(CadastroUsuarioActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    insereUsuario(usuario);
                    finish();

                    autenticacao.signOut();
                    abreTelaPrincipal();



                }else{
                    String erroExcecao = "";
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        erroExcecao = " Digite pelo menos 8 caracteres com letras e números";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        erroExcecao = " O e-mail digitado é inválido!";
                    }catch (FirebaseAuthUserCollisionException e){
                        erroExcecao = " E-mail já cadastrado!";
                    } catch (Exception e) {
                        erroExcecao = " Erro ao efetuar o cadastro!";

                        e.printStackTrace();
                    }
                    Toast.makeText(CadastroUsuarioActivity.this, "Erro:" + erroExcecao, Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    private boolean insereUsuario(Usuario usuario){
        try{
            reference = ConfiguracaoFirebase.getFirebase().child("usuarios");
            reference.push().setValue(usuario);
            Toast.makeText(CadastroUsuarioActivity.this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
            return true;

        }catch (Exception e){
            Toast.makeText(CadastroUsuarioActivity.this, "Erro ao salvar o usuário!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }
    }

    private void abreTelaPrincipal(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();

        Preferencias preferencias = new Preferencias(CadastroUsuarioActivity.this);

        autenticacao.signInWithEmailAndPassword(preferencias.getEmailUsuarioLogado(), preferencias.getSenhaUsuarioLogado()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    Intent intent = new Intent(CadastroUsuarioActivity.this, PrincipalActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(CadastroUsuarioActivity.this, "Falha!", Toast.LENGTH_LONG).show();
                    autenticacao.signOut();
                    Intent intent = new Intent(CadastroUsuarioActivity.this, MainActivity.class);
                    finish();
                    startActivity(intent);
                }

            }
        });
    }

    private void chamaTelaPrincipal(){
        Intent intent = new Intent(CadastroUsuarioActivity.this, PrincipalActivity.class);
        startActivity(intent);
        finish();
    }


}
