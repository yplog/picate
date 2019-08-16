package site.picate.picate

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import site.picate.picate.models.AccountData

class MainActivity : AppCompatActivity() {

    private lateinit var uAuthStatelistener : FirebaseAuth.AuthStateListener
    private val databaseReference = FirebaseDatabase.getInstance().reference

    var userAccountData: AccountData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!internetConnection()){
            goConnectionLost()
        }

        buttonSearchHide()
        progressBarShow()

        initUAuthStateListener()

        accountControl()

        buttonSearch.setOnClickListener{
            if (editTextSearch.text.isNotEmpty()) {
                val searchKey = editTextSearch.text.toString()
                goSearchActivity(searchKey)
            } else {
                Toast.makeText(this@MainActivity, searchForNothing(),
                    Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun initUAuthStateListener() {
        uAuthStatelistener = FirebaseAuth.AuthStateListener { task ->
            val user = task.currentUser

            if (user == null) goOut()

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settings_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.signout -> {
                signOut()
                return true
            }

            R.id.profile -> {
                goProfile()
                return true
            }

            R.id.support -> {
                goSupport()
                return true
            }

            R.id.favorites -> {
                goFavorites()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(uAuthStatelistener)
        accountControl()
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(uAuthStatelistener)
    }

    override fun onResume() {
        super.onResume()
        userControl()
        accountControl()
    }

    private fun goSearchActivity(searchKey: String) {
        val intent = Intent(this@MainActivity, SearchActivity::class.java)
        intent.putExtra("searchKey", searchKey)
        intent.putExtra("perPage", userAccountData?.options?.results)
        startActivity(intent)
    }

    private fun goConnectionLost(){
        val intent = Intent(this@MainActivity, InternetConnectionLostActivity::class.java)
        startActivity(intent)
    }

    private fun goFavorites() {
        val intent = Intent(this@MainActivity, FavoritesActivity::class.java)
        startActivity(intent)
    }

    private fun goProfile(){
        val intent = Intent(this@MainActivity, ProfileActivity::class.java)
        startActivity(intent)
    }

    private fun goSupport(){
        val intent = Intent(this@MainActivity, SupportActivity::class.java)
        startActivity(intent)
    }

    private fun userControl() {
        val user = FirebaseAuth.getInstance().currentUser

        if (user == null) {
            goOut()
        }
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }

    private fun goOut(){
        val intent = Intent(this@MainActivity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun searchForNothing() : String {
        val nothingList = listOf(
            "Oh, come on!",
            "We are faced with a genius.",
            "I did not call!",
            "You're a genius!",
            "I want to strike."
        )

        return nothingList.random()
    }

    private fun progressBarShow(){
        progressBarSearch.visibility = View.VISIBLE
    }

    private fun progressBarHide(){
        progressBarSearch.visibility = View.INVISIBLE
    }

    private fun buttonSearchShow() {
        buttonSearch.visibility = View.VISIBLE
    }

    private fun buttonSearchHide() {
        buttonSearch.visibility = View.INVISIBLE
    }

    private fun accountControl() {
        readUserAccountData(object : FirebaseCallback{
            override fun onCallback(accountData: AccountData) {
                userAccountData = accountData
                progressBarHide()
                buttonSearchShow()
            }
        })
    }

    private fun readUserAccountData(firebaseCallback: FirebaseCallback){
        val query = databaseReference.child("users")
            .orderByKey()
            .equalTo(FirebaseAuth.getInstance().currentUser?.uid)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(this@MainActivity, "We encountered a problem.", Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (singleDataSnapshot in p0.children) {
                    userAccountData = singleDataSnapshot.getValue(AccountData::class.java)
                }

                firebaseCallback.onCallback(userAccountData!!)
            }

        })
    }

    private interface FirebaseCallback {
        fun onCallback(accountData: AccountData)
    }

    private fun internetConnection(): Boolean {
        val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true

        return isConnected
    }
}