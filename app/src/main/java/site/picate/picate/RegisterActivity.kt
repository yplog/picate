package site.picate.picate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*
import site.picate.picate.models.AccountData
import site.picate.picate.models.AccountOptionsData
import site.picate.picate.models.ImageData

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        buttonRegister.setOnClickListener{
            if (editTextEmail.text.isNotEmpty() && editTextPassword.text.isNotEmpty() && editTextConfirmPassword.text.isNotEmpty()) {

                if (editTextPassword.text.toString() == editTextConfirmPassword.text.toString()) {
                    newUserRegister(editTextEmail.text.toString(), editTextPassword.text.toString())
                } else {
                    Toast.makeText(this, "Password fields do not match.",
                        Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(this@RegisterActivity, "Please fill in the blank fields.",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun newUserRegister(email: String, password: String) {
        progressBarShow()

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val newFavList: HashMap<String, ImageData> = hashMapOf()
                    val newAccountOptionsData = AccountOptionsData()
                    val newAccountData = AccountData(
                        FirebaseAuth.getInstance().currentUser?.uid.toString(),
                        newFavList,
                        newAccountOptionsData
                    )
                    val database = FirebaseDatabase.getInstance()
                    val usersRef = database.getReference("users")
                    usersRef.child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                        .setValue(newAccountData)

                    sendConfirmMail()
                    FirebaseAuth.getInstance().signOut()
                } else {
                    Toast.makeText(this@RegisterActivity, "Registration failed, ${task.exception?.message}.",
                        Toast.LENGTH_SHORT).show()
                }
            }

        progressBarHide()

    }

    private fun sendConfirmMail() {
        val user = FirebaseAuth.getInstance().currentUser

        user?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this@RegisterActivity, "Registration is successful, please check your email address.",
                    Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@RegisterActivity, "Please try again later, ${task.exception?.message}.",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun progressBarShow(){
        progressBarRegister.visibility = View.VISIBLE
    }

    private fun progressBarHide() {
        progressBarRegister.visibility = View.INVISIBLE
    }
}
