package com.example.ayu_do

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class Activity_Register : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity__register)
        setup()
    }
    private fun setup(){

        singUpButton.setOnClickListener{
            if(emailText.text.isNotEmpty()&&passwordText.text.isNotEmpty()){
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(emailText.text.toString(),
                        passwordText.text.toString()).addOnCompleteListener{
                        if(it.isSuccessful){
                            showHome(it.result?.user?.email ?:"")
                        } else{
                            showAlert()
                        }
                    }
            }
        }
        loginButton.setOnClickListener{
            showLogin()
        }
    }
    private fun showAlert(){
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Error")
            builder.setMessage("Se a producido un error en la autentificación del usuario")
            builder.setPositiveButton("Aceptar", null)
            val dialog: AlertDialog =builder.create()
            dialog.show()
    }

    private fun showHome(email: String){
        val homeIntent=Intent(this,HomeActivity::class.java).apply {
            putExtra("email", email)
        }
        startActivity(homeIntent)
    }

    private fun showLogin(){
            val loginIntent= Intent(this,Activity_Login::class.java)
            startActivity(loginIntent)
    }
}
