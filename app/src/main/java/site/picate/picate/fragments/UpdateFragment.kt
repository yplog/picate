package site.picate.picate.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_update.*
import site.picate.picate.LoginActivity
import site.picate.picate.R


class UpdateFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_update, container, false)

        val resetPassword = view.findViewById<Button>(R.id.buttonResetPassword)
        resetPassword.setOnClickListener {
            sendResetEmail()
        }

        val passwordTextEdit = view.findViewById<EditText>(R.id.editTextPassword)

        val confirm = view.findViewById<Button>(R.id.buttonConfirm)
        confirm.setOnClickListener {
            if (passwordTextEdit.text.isNotEmpty()) {
                confirmPassword(passwordTextEdit.text.toString())
            } else {
                Toast.makeText(activity, "You must fill in the Password field.",
                    Toast.LENGTH_SHORT).show()
            }
        }

        val delete = view.findViewById<Button>(R.id.buttonDeleteUser)
        delete.setOnClickListener {
            userDelete()
        }

        return view
    }

    private fun confirmPassword(password: String){
        val user = FirebaseAuth.getInstance().currentUser
        val credential = EmailAuthProvider.getCredential(user?.email.toString(), password)

        user?.reauthenticate(credential)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    greenPointShow()
                    layoutUpdateShow()
                } else {
                    Toast.makeText(activity, "Fail, " + task.exception?.message,
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun userDelete(){
        val user = FirebaseAuth.getInstance().currentUser

        user?.delete()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful){
                    FirebaseAuth.getInstance().signOut()

                    Toast.makeText(activity, "Your account has been deleted.",
                        Toast.LENGTH_SHORT).show()

                    val intent = Intent(activity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    activity?.finish()

                } else {
                    Toast.makeText(activity, "We encountered a problem. Try again later.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun sendResetEmail(){
        FirebaseAuth.getInstance().sendPasswordResetEmail(FirebaseAuth.getInstance().currentUser?.email.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(activity, "Please check your email.",
                        Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activity, "Fail, " + task.exception?.message,
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun greenPointShow(){
        imageViewGreenPoint.visibility = View.VISIBLE
    }

    private fun layoutUpdateShow() {
        constraintLayoutUpdate.visibility = View.VISIBLE
    }


}
