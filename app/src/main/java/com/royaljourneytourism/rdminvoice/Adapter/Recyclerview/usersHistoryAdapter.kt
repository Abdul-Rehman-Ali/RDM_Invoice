package com.royaljourneytourism.rdminvoice.Adapter.Recyclerview

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.royaljourneytourism.rdminvoice.EditUserDetailActivity
import com.royaljourneytourism.rdminvoice.Model.userHistory
import com.royaljourneytourism.rdminvoice.R
import com.royaljourneytourism.rdminvoice.UserHistoryInvoicesActivity

class UsersHistoryAdapter(
    private val context: Context,
    private val userList: ArrayList<userHistory>
) : RecyclerView.Adapter<UsersHistoryAdapter.UserViewHolder>() {

    private val firestore = FirebaseFirestore.getInstance()

    // ViewHolder class to bind views
    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvWebName: TextView = itemView.findViewById(R.id.tv_webName)
        val tvUsername: TextView = itemView.findViewById(R.id.tv_username)
        val cvUserHistory: CardView = itemView.findViewById(R.id.cv_userHistory)
    }

    // Inflating the layout for each item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_item_user_history, parent, false)
        return UserViewHolder(view)
    }

    // Binding data to the views
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.tvWebName.text = user.webName
        holder.tvUsername.text = user.username

        // Handle edit icon click
        holder.cvUserHistory.setOnClickListener {
            val intent = Intent(context, UserHistoryInvoicesActivity::class.java)
            intent.putExtra("DOCUMENT_ID", user.userId) // Pass document ID
            context.startActivity(intent)
        }
    }

    // Returns the size of the list
    override fun getItemCount(): Int {
        return userList.size
    }
}
