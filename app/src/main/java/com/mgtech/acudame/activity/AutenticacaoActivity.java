package com.mgtech.acudame.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.mgtech.acudame.R;
import com.mgtech.acudame.helper.ConfiguracaoFirebase;
import com.mgtech.acudame.helper.UsuarioFirebase;

import dmax.dialog.SpotsDialog;

public class AutenticacaoActivity extends AppCompatActivity {

    private Button botaoAcessar;
    private EditText campoEmail, campoSenha, campoConfirmarSenha;
    private Switch tipoAcesso;
    private LinearLayout linearTipoUsuario;
    private AlertDialog dialog;
    private boolean validarBotao = false;
    private int contador = 1;
    private FirebaseAuth autenticacao;
    final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autenticacao);

        inicializarComponentes();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        //autenticacao.signOut();

        // verificar se usuario está logado
        verificarUsuarioLogado();

        tipoAcesso.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    linearTipoUsuario.setVisibility(View.VISIBLE);
                }else {
                    linearTipoUsuario.setVisibility(View.GONE);
                }
            }
        });

        botaoAcessar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                botaoAcessar.setClickable(false);
                    dialog = new SpotsDialog.Builder()
                            .setContext(AutenticacaoActivity.this)
                            .setMessage("Carregando Dados")
                            .setCancelable(false)
                            .build();
                    dialog.show();


                    String email = campoEmail.getText().toString();
                    String senha = campoSenha.getText().toString();
                    String confirmarSenha = campoConfirmarSenha.getText().toString();

                    if (!email.isEmpty()) {
                        if (!senha.isEmpty()) {

                            // verifica estado do switch
                            if (tipoAcesso.isChecked()) { // cadastro

                                if (senha.equals(confirmarSenha)) {


                                    autenticacao.createUserWithEmailAndPassword(
                                            email, senha
                                    ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {

                                            if (task.isSuccessful()) {

                                                Toast.makeText(AutenticacaoActivity.this,
                                                        "Cadastro realizado com sucesso!",
                                                        Toast.LENGTH_SHORT).show();
                                                String tipoUsuario = "U";
                                                UsuarioFirebase.atualizarTipoUsuario(tipoUsuario);
                                                abrirTelaPrincipal(tipoUsuario);

                                            } else {
                                                String erroExcecao = "";

                                                try {
                                                    throw task.getException();
                                                } catch (FirebaseAuthWeakPasswordException e) {
                                                    erroExcecao = "Digite uma senha mais forte!";
                                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                                    erroExcecao = "E-mail inválido! Informe um e-mail válido!";
                                                } catch (FirebaseAuthUserCollisionException e) {
                                                    erroExcecao = "Esta conta já foi cadastrada";
                                                } catch (Exception e) {
                                                    erroExcecao = "ao cadastrar usuário: " + e.getMessage();
                                                    e.printStackTrace();
                                                }

                                                Toast.makeText(AutenticacaoActivity.this,
                                                        "Erro: " + erroExcecao,
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }else {

                                    Toast.makeText(AutenticacaoActivity.this,
                                            "As senhas não são iguais!",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }else { //login

                                    /*final ProgressDialog progressDialog = new ProgressDialog(AutenticacaoActivity.this,
                                            R.style.Theme_AppCompat_Light_DarkActionBar);
                                    progressDialog.setIndeterminate(true);
                                    progressDialog.setMessage("Autenticando...");
                                    progressDialog.show();*/


                                autenticacao.signInWithEmailAndPassword(
                                        email, senha
                                ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {

                                            //botaoAcessar.setEnabled(false);

                                            Toast.makeText(AutenticacaoActivity.this,
                                                    "Logado com sucesso!",
                                                    Toast.LENGTH_SHORT).show();
                                            String tipoUsuario = task.getResult().getUser().getDisplayName();
                                            abrirTelaPrincipal(tipoUsuario);

                                        } else {

                                            Toast.makeText(AutenticacaoActivity.this,
                                                    "Falha ao tentar logar: " + task.getException(),
                                                    Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });

                                //progressDialog.dismiss();
                            }

                        } else {
                            Toast.makeText(AutenticacaoActivity.this,
                                    "Senha é obrigatória!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AutenticacaoActivity.this,
                                "E-mail é obrigatório!",
                                Toast.LENGTH_SHORT).show();
                    }

                    dialog.dismiss();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms
                       botaoAcessar.setClickable(true);
                    }
                }, 1500);
            }
        });


    }

    private void verificarUsuarioLogado(){
        FirebaseUser usuarioAtual = autenticacao.getCurrentUser();
        if(usuarioAtual != null) {
            String tipoUsuario = usuarioAtual.getDisplayName();
            abrirTelaPrincipal(tipoUsuario);
        }

    }

    /*private String getTipoUsuario(){
        return tipoUsuario.isChecked() ? "E" : "U";
    }*/

    private void abrirTelaPrincipal(String tipoUsuario){
        if(tipoUsuario.equals("E")){ // empresa
            startActivity(new Intent(getApplicationContext(), EmpresaActivity.class));
        }else { // usuario
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }

    }

    private void inicializarComponentes(){
        campoEmail = findViewById(R.id.editCadastroEmail);
        campoSenha = findViewById(R.id.editCadastroSenha);
        campoConfirmarSenha = findViewById(R.id.editSenhaConfirmar);
        botaoAcessar = findViewById(R.id.buttonAcesso);
        tipoAcesso = findViewById(R.id.switchAcesso);
        linearTipoUsuario = findViewById(R.id.linearTipoUsuario);
        botaoAcessar.setClickable(true);
    }

}
