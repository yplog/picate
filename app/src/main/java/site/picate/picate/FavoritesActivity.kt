package site.picate.picate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_favorites.*
import site.picate.picate.adapters.FavImageDataAdapter
import site.picate.picate.models.AccountData
import site.picate.picate.models.ImageData

class FavoritesActivity : AppCompatActivity() {

    var userAccountData : AccountData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        var favImagesAdapter: FavImageDataAdapter?

        val databaseReference = FirebaseDatabase.getInstance().reference

        val query = databaseReference.child("users")
            .orderByKey()
            .equalTo(FirebaseAuth.getInstance().currentUser?.uid)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(this@FavoritesActivity, "We encountered a problem.", Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (singleDataSnapshot in p0.children) {
                    userAccountData = singleDataSnapshot.getValue(AccountData::class.java)!!
                }

                progressBarSearchHide()

                val list:MutableList<ImageData> = mutableListOf()

                for (image in userAccountData?.fav_list!!){
                    list.add(image.value)
                }

                favImagesAdapter = FavImageDataAdapter(list)
                recyclerViewFav.adapter = favImagesAdapter

                val layoutManager = LinearLayoutManager(this@FavoritesActivity, LinearLayoutManager.HORIZONTAL, false)
                layoutManager.orientation = androidx.recyclerview.widget.RecyclerView.VERTICAL
                recyclerViewFav.layoutManager = layoutManager

            }

        })
    }

    private fun progressBarSearchHide() {
        progressBarFav.visibility = View.INVISIBLE
    }
}
