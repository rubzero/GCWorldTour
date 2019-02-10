package com.example.rubzer.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    private Button btnSubmit;
    private CheckBox cbxShowPassword;
    private TextInputLayout txtEmail, txtUser, txtPassword, txtPassword2;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build();
    private DocumentReference reference;

    private ArrayList<EditText> editTextList;
    private ArrayList<Integer> iconIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        componentsDeclaration();
        disableErrorsOnTextChanged();
    }

    private void componentsDeclaration(){
        buttonDeclare();
        checkBoxDeclare();
        textImputLayoutDeclare();
    }

    private void textImputLayoutDeclare(){
        txtEmail = findViewById(R.id.txt_email_register);
        txtUser = findViewById(R.id.txt_user_register);
        txtPassword = findViewById(R.id.txt_password_register);
        txtPassword2 = findViewById(R.id.txt_password2_register);

        txtEmail.setHint(getResources().getString(R.string.email));
        txtUser.setHint(getResources().getString(R.string.user));
        txtPassword.setHint(getResources().getString(R.string.password));
        txtPassword2.setHint(getResources().getString(R.string.password2));

        Utilities.hideShowPassword(txtPassword.getEditText(), true);
        Utilities.hideShowPassword(txtPassword2.getEditText(), true);

        txtEmail.getEditText().setOnFocusChangeListener(this);
        txtUser.getEditText().setOnFocusChangeListener(this);
        txtPassword.getEditText().setOnFocusChangeListener(this);
        txtPassword2.getEditText().setOnFocusChangeListener(this);

        createTextInputLayoutLists();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        EditText editText = (EditText) v;
        Utilities.setHintIconSelected(editText, editTextList, iconIdList);
    }

    private void createTextInputLayoutLists(){
        editTextList = new ArrayList<>();
        iconIdList = new ArrayList<>();

        iconIdList.add(R.drawable.ic_email_24dp);
        iconIdList.add(R.drawable.ic_account_box_24dp);
        iconIdList.add(R.drawable.ic_lock_24dp);
        iconIdList.add(R.drawable.ic_lock_24dp);

        iconIdList.add(R.drawable.ic_email_selected_24dp);
        iconIdList.add(R.drawable.ic_account_box_selected_24dp);
        iconIdList.add(R.drawable.ic_lock_selected_24dp);
        iconIdList.add(R.drawable.ic_lock_selected_24dp);

        editTextList.add(txtEmail.getEditText());
        editTextList.add(txtUser.getEditText());
        editTextList.add(txtPassword.getEditText());
        editTextList.add(txtPassword2.getEditText());

        Utilities.setHintIconSelected(null, editTextList, iconIdList);
    }

    private void registerWithEmail(String email, String password){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()) {
                            try {
                                throw task.getException();
                            }
                            catch (FirebaseAuthWeakPasswordException e){
                                txtPassword.setError(getString(R.string.weakPasswordError));
                                txtPassword.requestFocus();
                            }
                            catch(FirebaseAuthInvalidCredentialsException e){
                                txtEmail.setError(getString(R.string.wrongEmail));
                                txtEmail.requestFocus();
                            }
                            catch(FirebaseAuthUserCollisionException e) {
                                txtEmail.setError(getString(R.string.duplicatedEmail));
                            }
                            catch(Exception e){};
                        }
                        else{
                            createUserDatabase();
                            startActivity(new Intent(RegisterActivity.this
                                    , LoginActivity.class));
                        }
                    }
                });
    }

    private void createUserDatabase(){
        firestore.setFirestoreSettings(settings);
        reference = firestore.collection("users").document();
        String id = reference.getId();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            String name = txtUser.getEditText().getText().toString().trim();
            String email= txtEmail.getEditText().getText().toString().trim();

            User user = new User(id, name, email, "0", null, null);
            reference.set(user);
        }
    }

    private void buttonDeclare(){
        btnSubmit = findViewById(R.id.btn_submit_register);
        btnSubmit.setOnClickListener(this);
    }

    private void checkBoxDeclare(){
        cbxShowPassword = findViewById(R.id.cbx_showPassword_register);
        cbxShowPassword.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                Utilities.hideShowPassword(txtPassword.getEditText(), true);
                Utilities.hideShowPassword(txtPassword2.getEditText(), true);
                if(isChecked) {
                    Utilities.hideShowPassword(txtPassword.getEditText(), false);
                    Utilities.hideShowPassword(txtPassword2.getEditText(), false);
                }
            }
        });
    }


    private boolean errorFound()
    {
        String passwordPattern = "^(?=.*[0-9])(?=.*[A-Z])(?=\\S+$).{8,}$";
        String userNamePattern = "^[a-z0-9_-]{3,15}$";

        String pass1 = txtPassword.getEditText().getText().toString();
        String pass2 = txtPassword2.getEditText().getText().toString();
        String user = txtUser.getEditText().getText().toString().trim();

        TextInputLayout[] parameters = {txtEmail, txtUser, txtPassword, txtPassword2};

        if(findEmptyParameterPosition(parameters) != -1){
            parameters[findEmptyParameterPosition(parameters)]
                    .setError(getString(R.string.emptyField));
            parameters[findEmptyParameterPosition(parameters)]
                    .getEditText().requestFocus();
            return true;
        }

        else if(!user.matches(userNamePattern)) {
            txtUser.setError(getString(R.string.userNamePattern));
            return true;
        }

        else if(!pass1.matches(passwordPattern)){
            txtPassword.setError(getString(R.string.weakPasswordError));
            return true;
        }

        else if(!pass1.equals(pass2)) {
            txtPassword2.setError(getString(R.string.notCollisionPassword));
            return true;
        }

        return false;
    }

    private int findEmptyParameterPosition(TextInputLayout[] parameters){
        for(int i=0; i<parameters.length; i++){
            if(parameters[i].getEditText().getText().toString().equals(""))
                return i;
        }
        return -1;
    }

    private void disableErrors()
    {
        txtPassword.setError(null);
        txtPassword.setErrorEnabled(false);
        txtPassword2.setError(null);
        txtPassword2.setErrorEnabled(false);
        txtUser.setError(null);
        txtUser.setErrorEnabled(false);
        txtEmail.setError(null);
        txtEmail.setErrorEnabled(false);
    }

    private void disableErrorsOnTextChanged()
    {
        txtEmail.getEditText().addTextChangedListener(new myTextWatcher());
        txtUser.getEditText().addTextChangedListener(new myTextWatcher());
        txtPassword.getEditText().addTextChangedListener(new myTextWatcher());
        txtPassword2.getEditText().addTextChangedListener(new myTextWatcher());
    }

    class myTextWatcher implements TextWatcher
    {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            disableErrors();
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }
        @Override
        public void afterTextChanged(Editable s) {}
    }

    @Override
    public void onClick(View v) {
        if(!Utilities.isConnected(this)){
            Toast.makeText(RegisterActivity.this, getResources().getString(R.string.notConnected)
                    , Toast.LENGTH_LONG).show();
            return;
        }
        switch(v.getId()){
            case R.id.btn_submit_register:
                disableErrors();
                if(!errorFound()) {
                    registerWithEmail(txtEmail.getEditText().getText().toString()
                            , txtPassword.getEditText().getText().toString());
                }
                break;
        }
        Utilities.hideSoftKeyboard(this, v);
    }
}
