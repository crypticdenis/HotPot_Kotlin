import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hotpot.Friend
import com.example.hotpot.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
@Suppress("DEPRECATION")

class FriendStoriesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val friendList = mutableListOf<Friend>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_friend_stories, container, false)

        recyclerView = view.findViewById(R.id.friendsRecyclerView)

        val databaseReference = FirebaseDatabase.getInstance().reference
        val user = FirebaseAuth.getInstance().currentUser
        val friendsReference = databaseReference.child("Users").child(user!!.uid).child("Friends")

        friendsReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Clear the existing friendList before populating it
                friendList.clear()

                Log.d("FriendStoriesFragment", "Snapshot value: ${snapshot.value}")

                // Ensure that the snapshot has a value and is a HashMap before attempting to retrieve data
                if (snapshot.value is Map<*, *>) {
                    val friendUIDs = (snapshot.value as Map<*, *>).values.toList()

                    friendUIDs.forEach { friendUID ->
                        // Cast the friendUID to String
                        val friendUIDString = friendUID.toString()

                        val friendReference = databaseReference.child("Users").child(friendUIDString)

                        friendReference.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(friendSnapshot: DataSnapshot) {
                                val friendName = friendSnapshot.child("name").getValue(String::class.java)

                                if (friendName != null) {
                                    Log.d("FriendStoriesFragment", "Friend name: $friendName")
                                    Log.d("FriendStoriesFragment", "Friend UID: $friendUIDString")

                                    val friend = Friend(friendUIDString, friendName)
                                    friendList.add(friend)

                                    // Check if the adapter is not attached and friendList is not empty
                                    if (recyclerView.adapter == null && friendList.isNotEmpty()) {
                                        // Set up the RecyclerView adapter after adding the first friend
                                        val adapter = FriendAdapter(friendList)
                                        recyclerView.layoutManager =
                                            LinearLayoutManager(
                                                requireContext(),
                                                LinearLayoutManager.HORIZONTAL,
                                                false
                                            )
                                        recyclerView.adapter = adapter
                                    } else {
                                        // Notify the adapter about the data change if it's already attached
                                        recyclerView.adapter?.notifyDataSetChanged()
                                    }
                                } else {
                                    Log.e(
                                        "FriendStoriesFragment",
                                        "Friend name is null for UID: $friendUIDString"
                                    )
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e(
                                    "FriendStoriesFragment",
                                    "Firebase database error: ${error.message}"
                                )

                                Toast.makeText(
                                    requireContext(),
                                    "An error occurred. Please try again later.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })

        // set background to transparent
        recyclerView.setBackgroundColor(resources.getColor(android.R.color.transparent))

        return view
    }
}
