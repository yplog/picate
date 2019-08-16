package site.picate.picate.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fav_image_card.view.*
import site.picate.picate.R
import site.picate.picate.models.ImageData

class FavImageDataAdapter(imageDataList: MutableList<ImageData>) : RecyclerView.Adapter<FavImageDataAdapter.FavImageDataViewHolder>() {

    private var imageDataList = imageDataList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavImageDataViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val favImageCard = inflater.inflate(R.layout.fav_image_card, parent, false)

        return FavImageDataViewHolder(favImageCard)
    }

    override fun getItemCount(): Int {
        return imageDataList.size
    }

    override fun onBindViewHolder(holder: FavImageDataViewHolder, position: Int) {
        val image = imageDataList[position]
        holder.setData(image)
    }

    inner class FavImageDataViewHolder(favImageCard: View) : RecyclerView.ViewHolder(favImageCard) {

        private var imageCard = favImageCard as CardView
        private var textViewUserName = imageCard.textViewFavUserName
        private var textViewUserLink = imageCard.textViewFavUserLink
        private var imageView = imageCard.imageViewFavImage
        private var favButton = imageCard.floatingActionButtonRemoveFav

        fun setData(image: ImageData){
            textViewUserName.text = image.userName
            textViewUserLink.text = image.userLink
            Picasso.get().load(image.imgUrl).into(imageView)

            favButton.setOnClickListener{
                removeImageFav(image, imageCard)
            }
        }

    }

    private fun removeImageFav(imageData: ImageData, imageCard: View){
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")
        usersRef.child(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .child("fav_list")
            .child(imageData.uuid.toString())
            .setValue(null)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(imageCard.context, "Remove to favorites.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(imageCard.context, "Failed to add to favorites.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}