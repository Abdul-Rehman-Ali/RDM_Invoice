package com.royaljourneytourism.rdminvoice.Adapter.Recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.royaljourneytourism.rdminvoice.R
import com.royaljourneytourism.rdminvoice.Model.editUser

class EditUserAdapter(private val userList: ArrayList<editUser>) : RecyclerView.Adapter<EditUserAdapter.UserViewHolder>() {

    // ViewHolder class to bind views
    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvWebName: TextView = itemView.findViewById(R.id.tv_webName)
        val tvUsername: TextView = itemView.findViewById(R.id.tv_username)
    }

    // Inflating the layout for each item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_item_edit_user, parent, false)
        return UserViewHolder(view)
    }

    // Binding data to the views
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.tvWebName.text = user.webName
        holder.tvUsername.text = user.username
    }

    // Returns the size of the list
    override fun getItemCount(): Int {
        return userList.size
    }

    // Method to update the list
    fun updateList(newList: ArrayList<editUser>) {
        userList.clear()
        userList.addAll(newList)
        notifyDataSetChanged()
    }
}
