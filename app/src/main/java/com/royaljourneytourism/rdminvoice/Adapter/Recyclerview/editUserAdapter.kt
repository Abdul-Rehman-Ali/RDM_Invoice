package com.royaljourneytourism.rdminvoice.Adapter.Recyclerview

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.royaljourneytourism.rdminvoice.Model.editUser
import com.royaljourneytourism.rdminvoice.R
import com.royaljourneytourism.rdminvoice.EditUserDetailActivity

class EditUserAdapter(
    private val context: Context,
    private val userList: ArrayList<editUser>
) : RecyclerView.Adapter<EditUserAdapter.UserViewHolder>() {

    private val firestore = FirebaseFirestore.getInstance()

    // ViewHolder class to bind views
    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvWebName: TextView = itemView.findViewById(R.id.tv_webName)
        val tvUsername: TextView = itemView.findViewById(R.id.tv_username)
        val ivDelete: ImageView = itemView.findViewById(R.id.iv_delete) // Delete icon
        val ivEdit: ImageView = itemView.findViewById(R.id.iv_edit)     // Edit icon
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

        // Handle delete icon click
        holder.ivDelete.setOnClickListener {
            showDeleteConfirmationDialog(user, position)
        }

        // Handle edit icon click
        holder.ivEdit.setOnClickListener {
            val intent = Intent(context, EditUserDetailActivity::class.java)
            intent.putExtra("DOCUMENT_ID", user.userId) // Pass document ID
            context.startActivity(intent)
        }
    }

    // Returns the size of the list
    override fun getItemCount(): Int {
        return userList.size
    }

    // Show a confirmation dialog before deleting the user
    private fun showDeleteConfirmationDialog(user: editUser, position: Int) {
        val builder = android.app.AlertDialog.Builder(context)
        builder.setTitle("Delete User")
        builder.setMessage("Are you sure you want to delete the user?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            deleteUserFromDatabase(user, position)
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    // Delete the user from Firestore and update the list
    private fun deleteUserFromDatabase(user: editUser, position: Int) {
        firestore.collection("Users").document(user.userId)
            .delete()
            .addOnSuccessListener {
                // Remove the user from the list and notify the adapter
                userList.removeAt(position)
                notifyItemRemoved(position)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }
}
