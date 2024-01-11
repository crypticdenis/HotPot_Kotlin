import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.hotpot.Friend
import com.example.hotpot.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FriendAdapter(private val friendList: List<Friend>) :
    RecyclerView.Adapter<FriendAdapter.FriendViewHolder>() {

    inner class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivFriendImage: ImageView = itemView.findViewById(R.id.friendImageButton)
        val nameTextView: TextView = itemView.findViewById(R.id.friendNameStories)
        // Add other views as needed
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friend, parent, false)

        return FriendViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val currentFriend = friendList[position]

        holder.nameTextView.text = currentFriend.name

        val storageReference: StorageReference =
            FirebaseStorage.getInstance().getReference("profilePictures")
                .child(currentFriend.friendUID)

        // Convert StorageReference to Uri
        storageReference.downloadUrl.addOnSuccessListener { uri ->
            // Use Coil for image loading
            holder.ivFriendImage.load(uri)
        }.addOnFailureListener {
            // Handle failure to get download URL
        }
    }

    override fun getItemCount(): Int {
        return friendList.size
    }
}
