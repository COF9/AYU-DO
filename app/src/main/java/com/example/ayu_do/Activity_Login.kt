package com.example.ayu_do

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*

class Activity_Login : AppCompatActivity() {

    private val GOOGLE_SIGN_IN = 100

    private val callbackManager = CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //Analytics Event
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("messeage","Integracion de Firebase completa")
        analytics.logEvent("InitScreen", bundle)

        //setup
        setup()
        session()
    }

    private fun session() {
        val prefs=getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)

        if (email!=null){
            showHome(email)
        }
    }
    private fun setup(){

        singUpButton.setOnClickListener{
                        showSing()
        }
        loginButton.setOnClickListener{
            if(emailText.text.isNotEmpty()&&passwordText.text.isNotEmpty()){
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(emailText.text.toString(),
                        passwordText.text.toString()).addOnCompleteListener{
                        if(it.isSuccessful){
                            showHome(it.result?.user?.email ?:"")
                        } else{
                            showAlert()
                        }
                    }
            }
        }
        googleButton.setOnClickListener{
            //Configuracion
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
            val googleClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut()
            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
        }
        facebookButton.setOnClickListener{
            LoginManager.getInstance().logInWithReadPermissions(this,listOf("email"))
            LoginManager.getInstance().registerCallback(callbackManager,object : FacebookCallback<LoginResult>{
                override fun onSuccess(result: LoginResult?) {
                    result?.let{
                        val token = it.accessToken
                        val credential = FacebookAuthProvider.getCredential(token.token)
                        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                            if (it.isSuccessful) {
                                showHome(it.result?.user?.email ?: "")
                            } else {
                                showAlert()
                            }
                        }
                    }
                }

                override fun onCancel() {
                }

                override fun onError(error: FacebookException?) {
                    showAlert()
                }
            })
        }
    }

    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se a producido un error en la autentificación del usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog:AlertDialog=builder.create()
        dialog.show()
    }

    private fun showHome(email: String){
        val homeIntent=Intent(this,HomeActivity::class.java).apply {
            putExtra("email", email)
        }
        startActivity(homeIntent)
    }

    private fun showSing(){
        val singIntent= Intent(this,Activity_Register::class.java)
        startActivity(singIntent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==GOOGLE_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val accont = task.getResult(ApiException::class.java)

                if(accont != null) {
                    val credential = GoogleAuthProvider.getCredential(accont.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                        if (it.isSuccessful) {
                            showHome(accont.email ?: "")
                        } else {
                            showAlert()
                        }
                    }
                }
            }catch (e: ApiException){
                showAlert()
            }
        }
    }
}
