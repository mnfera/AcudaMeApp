package com.mgtech.acudame.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mgtech.acudame.R;
import com.mgtech.acudame.helper.ConfiguracaoFirebase;
import com.mgtech.acudame.helper.UsuarioFirebase;
import com.mgtech.acudame.model.Usuario;
import com.santalu.maskedittext.MaskEditText;

import dmax.dialog.SpotsDialog;

public class ConfiguracoesUsuarioActivity extends AppCompatActivity {

    private EditText editUsuarioNome, editUsuarioEndereco,
                    editTextUsuarioNumero, editTextUsuarioReferencia;
    private MaskEditText editUsuarioTelefone;
    private Button buttonSalvar;
    private String idUsuario;
    private DatabaseReference firebaseRef;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes_usuario);

        // conf iniciais
        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuario = UsuarioFirebase.getIdUsuario();

        // configurações toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        buttonSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarDadosUsuario();
            }
        });

        // Recuperar dados da empresa
        recuperarDadosUsuario();
    }

    private void recuperarDadosUsuario() {

        dialog = new SpotsDialog.Builder()
                .setContext(ConfiguracoesUsuarioActivity.this)
                .setMessage("Carregando Dados")
                .setCancelable(false)
                .build();
        dialog.show();

        DatabaseReference usuarioRef = firebaseRef
                .child("usuarios")
                .child(idUsuario);
        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    Usuario usuario = dataSnapshot.getValue(Usuario.class);
                    editUsuarioNome.setText(usuario.getNome());
                    editUsuarioEndereco.setText(usuario.getEndereco());
                    editTextUsuarioNumero.setText(usuario.getNumero());
                    editTextUsuarioReferencia.setText(usuario.getReferencia());
                    editUsuarioTelefone.setText(usuario.getTelefone());
                }
                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void validarDadosUsuario() {

        // verifica se os campos foram preenchidos
        String nome = editUsuarioNome.getText().toString();
        String endereco = editUsuarioEndereco.getText().toString();
        String telefone = editUsuarioTelefone.getText().toString();
        String numero = editTextUsuarioNumero.getText().toString();
        String referencia = editTextUsuarioReferencia.getText().toString();

        if(!nome.isEmpty()) {
            if(!endereco.isEmpty()) {
                if(!numero.isEmpty()) {
                    if (!referencia.isEmpty()) {

                        if (!telefone.isEmpty()) {

                            Usuario usuario = new Usuario();
                            usuario.setIdUsuario(idUsuario);
                            usuario.setNome(nome);
                            usuario.setEndereco(endereco);
                            usuario.setNumero(numero);
                            usuario.setReferencia(referencia);
                            usuario.setTelefone(telefone);
                            usuario.salvar();

                            exibirMensagem("Dados atualizados com sucesso!");
                            finish();

                        } else {
                            exibirMensagem("Digite seu telefone");
                        }
                    } else {
                        exibirMensagem("Digite um ponto de referência ou característica");
                    }
                }else {
                    exibirMensagem("Digite um número ou zero para sem número");
                }
            }else {
                exibirMensagem("Digite o nome da rua");
            }
        }else {
            exibirMensagem("Digite seu nome!");
        }

    }

    private void exibirMensagem(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    private void inicializarComponentes() {
        editUsuarioNome = findViewById(R.id.editUsuarioNome);
        editUsuarioEndereco = findViewById(R.id.editUsuarioEndereco);
        buttonSalvar = findViewById(R.id.buttonSalvar);
        editUsuarioTelefone = findViewById(R.id.editUsuarioTelefone);
        editTextUsuarioNumero = findViewById(R.id.editUsuarioNumero);
        editTextUsuarioReferencia = findViewById(R.id.editUsuarioReferencia);
    }
}
