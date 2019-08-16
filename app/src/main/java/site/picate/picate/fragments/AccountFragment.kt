package site.picate.picate.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import site.picate.picate.R
import site.picate.picate.models.AccountData


class AccountFragment : Fragment() {

    private var results = arrayOf("Unselected", "10", "20", "30", "50")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)


        val user = FirebaseAuth.getInstance().currentUser
        val databaseReadRef = FirebaseDatabase.getInstance().reference
        var userAccountData: AccountData? = null
        val query = databaseReadRef.child("users")
            .orderByKey()
            .equalTo(user?.uid)

        query.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (singleDataSnapshot in p0.children){
                    userAccountData = singleDataSnapshot.getValue(AccountData::class.java)!!

                    val result = view.findViewById<TextView>(R.id.textViewResult)
                    result.text = "Number of results: ${userAccountData?.options?.results}"
                    val favCount = view.findViewById<TextView>(R.id.textViewFavCount)
                    favCount.text = userAccountData?.fav_list?.size.toString()
                }
            }
        })

        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")

        val spinner = view?.findViewById<Spinner>(R.id.spinnerResults)
        val spinnerAdapter = ArrayAdapter(view?.context!!, android.R.layout.simple_spinner_item, results)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        spinner?.adapter = spinnerAdapter


        val email = view.findViewById<TextView>(R.id.textViewEmail)
        email.text = user?.email.toString()

        spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (spinner?.selectedItemId!! > 0){
                    usersRef.child(user?.uid.toString())
                        .child("options")
                        .child("results")
                        .setValue(spinner.selectedItem.toString())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(view.context, "The change has been saved.",
                                    Toast.LENGTH_SHORT).show()
                                userAccountData?.options?.results = spinner.selectedItem.toString()
                                val result = view.findViewById<TextView>(R.id.textViewResult)
                                result.text = "Number of results: ${userAccountData?.options?.results}"
                            } else {
                                Toast.makeText(view.context, "Failed to save the change.",
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }

        }

        return view
    }

}
