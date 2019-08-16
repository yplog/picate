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
import kotlinx.android.synthetic.main.image_card.view.*
import site.picate.picate.R
import site.picate.picate.models.ImageData

class ImageDataAdapter(imageDataList: MutableList<ImageData>) : RecyclerView.Adapter<ImageDataAdapter.ImageDataViewHolder>() {
    private var imageDataList = imageDataList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageDataViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val imageCard = inflater.inflate(R.layout.image_card, parent, false)

        return ImageDataViewHolder(imageCard)
    }

    override fun getItemCount(): Int {
        return imageDataList.size
    }

    override fun onBindViewHolder(holder: ImageDataViewHolder, position: Int) {
        val image = imageDataList[position]
        holder.setData(image)
    }

    inner class ImageDataViewHolder(imageCard: View) : RecyclerView.ViewHolder(imageCard) {

        private var imageCard = imageCard as CardView
        private var textViewUserName = imageCard.textViewUserName
        private var textViewUserLink = imageCard.textViewUserLink
        private var imageView = imageCard.imageViewImage
        private var favButton = imageCard.floatingActionButtonAddFav

        fun setData(image: ImageData) {
            textViewUserLink.text = image.userLink
            textViewUserName.text = image.userName
            Picasso.get().load(image.imgUrl).into(imageView)

            favButton.setOnClickListener{
                saveImageFav(image, imageCard)
            }
        }

    }

    private fun saveImageFav(imageData: ImageData, imageCard: View){
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")
        val key = usersRef.child(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .child("fav_list")
            .push().key

        imageData.uuid = key

        usersRef.child(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .child("fav_list")
            .child(key.toString())
            .setValue(imageData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(imageCard.context, "Added to favorites.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(imageCard.context, "Failed to add to favorites.", Toast.LENGTH_SHORT).show()
                }
             }
    }
}