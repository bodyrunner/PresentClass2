package com.ricardosp.presentclass.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ricardosp.presentclass.Classes.Usuario;
import com.ricardosp.presentclass.R;

public class PrincipalActivity extends AppCompatActivity {
    private FirebaseAuth autenticacao;
    private DatabaseReference referenciaFirebase;
    private TextView tipoUsuario;
    private Usuario usuario;
    private String tipoUsuarioEmail;
    private Menu menu1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        tipoUsuario = (TextView)findViewById(R.id.txtTipoUsuario);


        autenticacao = FirebaseAuth.getInstance();


        referenciaFirebase = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.clear();

        this.menu1 = menu;

        String email = autenticacao.getCurrentUser().getEmail().toString();



        referenciaFirebase.child("usuarios").orderByChild("email").equalTo(email.toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    tipoUsuarioEmail = postSnapshot.child("tipoUsuario").getValue().toString();

                    tipoUsuario.setText(tipoUsuarioEmail);

                    menu1.clear();

                    if (tipoUsuarioEmail.equals("Professor")){
                        getMenuInflater().inflate(R.menu.menu_professor, menu1);

                    }else if (tipoUsuarioEmail.equals("Aluno")){
                        getMenuInflater().inflate(R.menu.menu_aluno, menu1);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add_usuario){
            abrirTelaCadastroUsuario();

        }else if (id == R.id.action_sair_professor){
            deslogarUsuario();
        }else if (id == R.id.action_sair_aluno){
            deslogarUsuario();
        }else if (id == R.id.action_cad_foto_perfil_aluno){
            uploadFotoPerfil();
        }else if (id == R.id.action_cadastro_sistema){
            abrirTelaCadastroSistema();
        }else if (id == R.id.action_efetuar_chamada){
            abrirTelaChamada();
        }




        return super.onOptionsItemSelected(item);
    }
    private void abrirTelaCadastroUsuario(){
        Intent intent = new Intent(PrincipalActivity.this, CadastroUsuarioActivity.class);
        startActivity(intent);
    }
    private void abrirTelaCadastroSistema(){
        Intent intent = new Intent(PrincipalActivity.this, CadastroActivity.class);
        startActivity(intent);
    }
    private void abrirTelaChamada(){
        Intent intent = new Intent(PrincipalActivity.this, EfetuarChamadaActivity.class);
        startActivity(intent);
    }
    private void deslogarUsuario(){
        autenticacao.signOut();

        Intent intent = new Intent(PrincipalActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

    }
    private void uploadFotoPerfil(){
        Intent intent = new Intent(PrincipalActivity.this, UploadFotoActivity.class);
        startActivity(intent);
    }

}
