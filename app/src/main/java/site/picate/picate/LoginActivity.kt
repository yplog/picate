package site.picate.picate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var uAuthStateListener : FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initUAuthStateListener()

        buttonGoRegister.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        buttonLogin.setOnClickListener {
            if (editTextEmail.text.isNotEmpty() && editTextPassword.text.isNotEmpty()) {
                userLogin(editTextEmail.text.toString(), editTextPassword.text.toString())
            } else {
                Toast.makeText(this@LoginActivity, "Please fill in the blank fields.",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun userLogin(email: String, password: String) {
        progressBarShow()

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@LoginActivity, "Login successful.",
                        Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@LoginActivity, "Login failed, ${task.exception?.message}.",
                        Toast.LENGTH_SHORT).show()
                }
            }

        progressBarHide()
    }

    private fun progressBarShow() {
        progressBarLogin.visibility = View.VISIBLE
    }

    private fun progressBarHide() {
        progressBarLogin.visibility = View.INVISIBLE
    }

    private fun initUAuthStateListener(){
        uAuthStateListener = FirebaseAuth.AuthStateListener { p0 ->
            val user = p0.currentUser

            if (user != null) {
                if (user.isEmailVerified) {
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Please confirm the mail you received.",
                        Toast.LENGTH_SHORT).show()

                    FirebaseAuth.getInstance().signOut()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(uAuthStateListener)
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(uAuthStateListener)
    }
}
