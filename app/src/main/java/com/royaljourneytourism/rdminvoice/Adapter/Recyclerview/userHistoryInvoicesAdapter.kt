package com.royaljourneytourism.rdminvoice.Adapter.Recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.royaljourneytourism.rdminvoice.Model.userHistoryInvoice
import com.royaljourneytourism.rdminvoice.R

class UsersHistoryInvoicesAdapter(
    private val context: Context,
    private var userList: ArrayList<userHistoryInvoice> // Changed to var for dynamic updates
) : RecyclerView.Adapter<UsersHistoryInvoicesAdapter.UserViewHolder>() {

    // ViewHolder class to bind views
    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val packageName: TextView = itemView.findViewById(R.id.tv_packageTitle)
        val name: TextView = itemView.findViewById(R.id.tv_name)
        val currentDate: TextView = itemView.findViewById(R.id.tv_date)
        val totalPrice: TextView = itemView.findViewById(R.id.tv_price)
    }

    // Inflating the layout for each item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_item_user_history_invoices, parent, false)
        return UserViewHolder(view)
    }

    // Binding data to the views
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.packageName.text = user.packageName
        holder.name.text = user.name
        holder.currentDate.text = user.timeStamp
        holder.totalPrice.text = "${user.totalPrice}" // Formatting totalPrice
    }

    // Returns the size of the list
    override fun getItemCount(): Int {
        return userList.size
    }

    // Method to update the list dynamically
    fun updateList(newList: List<userHistoryInvoice>) {
        userList.clear() // Clear the old list
        userList.addAll(newList) // Add the new filtered data
        notifyDataSetChanged() // Notify the adapter to refresh the RecyclerView
    }
}
