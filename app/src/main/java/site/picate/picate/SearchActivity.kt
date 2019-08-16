package site.picate.picate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import kotlinx.android.synthetic.main.activity_search.*
import org.json.JSONObject
import site.picate.picate.adapters.ImageDataAdapter
import site.picate.picate.helper.VoSingleton
import site.picate.picate.models.ImageData

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val searchKey: String? = intent.getStringExtra("searchKey")
        val perPage: String? = intent.getStringExtra("perPage")

        val accessKey = ""
        val url = "https://api.unsplash.com/search/photos/?client_id=$accessKey&query=$searchKey&per_page=$perPage"
        val imageDataList: MutableList<ImageData> = mutableListOf()
        var imagesAdapter: ImageDataAdapter?

        val searchObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener<JSONObject> { response ->
                val results = response?.getJSONArray("results")
                for (i in 0..perPage?.toInt()!!.minus(1)){
                    val user = results?.getJSONObject(i)?.getJSONObject("user")
                    val userLinks = user?.getJSONObject("links")
                    val userLink = userLinks?.getString("html")
                    val name = user?.getString("name")
                    val urls = results?.getJSONObject(i)?.getJSONObject("urls")
                    val imgUrl = urls?.getString("regular")

                    val image =
                        ImageData("", imgUrl.toString(), name.toString(), userLink.toString())
                    imageDataList.add(image)
                }

                progressBarSearchHide()

                imagesAdapter = ImageDataAdapter(imageDataList)
                recyclerViewImages.adapter = imagesAdapter

                val layoutManager = LinearLayoutManager(this@SearchActivity, LinearLayoutManager.HORIZONTAL, false)
                layoutManager.orientation = androidx.recyclerview.widget.RecyclerView.VERTICAL
                recyclerViewImages.layoutManager = layoutManager

            }, Response.ErrorListener { error ->
                Toast.makeText(this, "Fail, ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        VoSingleton.getInstance(this).addToRequestQueue(searchObjectRequest)
    }

    private fun progressBarSearchHide() {
        progressBarSearch.visibility = View.INVISIBLE
    }
}
