package com.example.rubzer.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    private static final int RC_SIGN_IN = 1;

    private FirebaseAuth mAuth;

    private Button btnSubmit, btnRegister;
    private TextInputLayout txtUser, txtPassword;
    private TextView charCount;
    private Utilities utilities;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build();
    private DocumentReference reference;

    private GoogleApiClient mGoogleApiClient;

    private ArrayList<EditText> editTextList;
    private ArrayList<Integer> iconIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        utilities = new Utilities();
        mAuth = FirebaseAuth.getInstance();

        textImputLayoutDeclare();
        buttonDeclare();
        textViewDeclare();
        setGoogleApiClient();
        disableErrorsOnTextChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null && Utilities.isConnected(LoginActivity.this)) {
                    rightParametersAnimation();
                    startActivity(new Intent(LoginActivity.this
                            , HomeActivity.class));
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        });
    }

    private void rightParametersAnimation()
    {
        LinearLayout llMain = findViewById(R.id.ll_mainContent_login);
        LinearLayout llProgressBar = findViewById(R.id.ll_progressBar_login);

        llMain.setVisibility(View.GONE);
        llProgressBar.setVisibility(View.VISIBLE);
    }

    private void textImputLayoutDeclare(){
        txtUser = findViewById(R.id.txt_user_login);
        txtPassword = findViewById(R.id.txt_password_login);

        txtUser.setHint(getResources().getString(R.string.user));
        txtPassword.setHint(getResources().getString(R.string.password));

        utilities.hideShowPassword(txtPassword.getEditText(), true);
        countCharactersOnTextChanged();

        txtUser.getEditText().setOnFocusChangeListener(this);
        txtPassword.getEditText().setOnFocusChangeListener(this);

        createTextInputLayoutLists();
    }

    private void createTextInputLayoutLists(){
        editTextList = new ArrayList<>();
        iconIdList = new ArrayList<>();

        iconIdList.add(R.drawable.ic_account_box_24dp);
        iconIdList.add(R.drawable.ic_lock_24dp);

        iconIdList.add(R.drawable.ic_account_box_selected_24dp);
        iconIdList.add(R.drawable.ic_lock_selected_24dp);

        editTextList.add(txtUser.getEditText());
        editTextList.add(txtPassword.getEditText());

        Utilities.setHintIconSelected(null, editTextList, iconIdList);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        charCount.setTextColor(getResources().getColor(R.color.colorMenu));
        EditText editText = (EditText) v;
        if(editText == txtPassword.getEditText())
            charCount.setTextColor(getResources().getColor(R.color.colorIndicatorSelected));
        Utilities.setHintIconSelected(editText, editTextList, iconIdList);
    }

    private void buttonDeclare(){
        btnSubmit = findViewById(R.id.btn_submit_login);
        btnRegister = findViewById(R.id.btn_register_login);
        SignInButton btnGoogle = findViewById(R.id.google_submit_login);

        btnSubmit.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        btnGoogle.setOnClickListener(this);
    }

    private void textViewDeclare(){
        charCount = findViewById(R.id.lbl_charCount_login);
    }

    private void countCharactersOnTextChanged()
    {
        charCount = findViewById(R.id.lbl_charCount_login);
        txtPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String lenght = (s.length()==0) ? "" : s.length()+"";
                charCount.setText(lenght);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private String defaultName(String email){
        String name = "";
        for(int i=0; i<email.length(); i++){
            if(email.charAt(i)=='@')
                break;
            name+=email.charAt(i);
        }
        return name;
    }

    private void loginWithEmail(String email, String password){
        if(!email.equals("") && !password.equals(""))
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).
                    addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task){
                            if(task.isSuccessful()){
                                rightParametersAnimation();
                                startActivity(new Intent(LoginActivity.this
                                        , HomeActivity.class));
                            }
                            else
                                Toast.makeText(LoginActivity.this
                                        , getResources().getString(R.string.wrongCredentials)
                                        , Toast.LENGTH_LONG).show();
                        }
                    });
    }

    private void disableErrors()
    {
        txtPassword.setError(null);
        txtPassword.setErrorEnabled(false);
        txtUser.setError(null);
        txtUser.setErrorEnabled(false);
    }

    private void disableErrorsOnTextChanged()
    {
        txtUser.getEditText().addTextChangedListener(new LoginActivity.myTextWatcher());
        txtPassword.getEditText().addTextChangedListener(new LoginActivity.myTextWatcher());
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

    private boolean errorFound()
    {
        TextInputLayout[] parameters = {txtUser, txtPassword};

        if(findEmptyParameterPosition(parameters) != -1){
            parameters[findEmptyParameterPosition(parameters)]
                    .setError(getString(R.string.emptyField));
            parameters[findEmptyParameterPosition(parameters)]
                    .getEditText().requestFocus();
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

    @Override
    public void onClick(View v) {
        if(!Utilities.isConnected(this)){
            Toast.makeText(LoginActivity.this, getResources().getString(R.string.notConnected)
                    , Toast.LENGTH_LONG).show();
            return;
        }
        switch(v.getId()){
            case R.id.btn_submit_login:
                if (!errorFound())
                loginWithEmail(txtUser.getEditText().getText().toString()
                        , txtPassword.getEditText().getText().toString());
                break;

            case R.id.btn_register_login:
                startActivity(new Intent(LoginActivity.this
                        , RegisterActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;

            case R.id.google_submit_login:
                loginWithGoogle();
                break;
        }
        Utilities.hideSoftKeyboard(this, v);
    }

    private void setGoogleApiClient(){
        GoogleSignInOptions mGoogleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.webClientId))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient
                        .OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(LoginActivity.this, "Error de conexión."
                                , Toast.LENGTH_LONG).show();
                    }
                }).addApi(Auth.GOOGLE_SIGN_IN_API, mGoogleSignInOptions).build();
    }

    private void loginWithGoogle(){
        checkLastConnection();
        startActivityForResult(new Intent(Auth
                .GoogleSignInApi
                .getSignInIntent(mGoogleApiClient)),RC_SIGN_IN);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void checkLastConnection(){
        firestore.setFirestoreSettings(settings);
        reference = firestore.collection("users").document();
        String id = reference.getId();
        if(GoogleSignIn.getLastSignedInAccount(this)!=null) {
            String email= GoogleSignIn.getLastSignedInAccount(this).getEmail();
            String name = defaultName(email);
            User user = new User(id, name, email, "0", null, null);
            createGoogleUser(user);
        }
    }

    private void createGoogleUser(final User user){
        final CollectionReference reference = firestore.collection("users");
        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        User dbUser = document.toObject(User.class);
                        if(dbUser.getEmail().equals(user.getEmail()))
                            return;
                    }
                    createGoogleUserData(user.getEmail(), user);
                }
            }
        });
    }

    private void createGoogleUserData(String email, final User user){
        mAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        reference.set(user);
                    }
                });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful())
                            Toast.makeText(LoginActivity.this, "Error de credenciales."
                                    , Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
        }
    }

    @Override
    public void onBackPressed() {
    }
}
