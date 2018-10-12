package com.example.zinmarhtun.kotlinhelloworld.viewModels

import android.util.Log
import com.example.zinmarhtun.kotlinhelloworld.R
import com.example.zinmarhtun.kotlinhelloworld.models.ChatMessage
import com.example.zinmarhtun.kotlinhelloworld.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessageRow(val chatMessage: ChatMessage): Item<ViewHolder>() {

    var chatPartnerUser: User? = null

    override fun getLayout(): Int {
        Log.d("LatestMessageActivity", "create latest_message_row ...")
        return R.layout.latest_message_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.chatMsg_latest_msg_row.text = chatMessage.text

        val chatPartnerId:String = if(chatMessage.fromId == FirebaseAuth.getInstance().uid)
        {chatMessage.toId}
        else chatMessage.fromId

        Log.d("LatestMessagesActivity", "Chat PartnerID : $chatPartnerId")

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                chatPartnerUser = p0.getValue(User::class.java)
                viewHolder.itemView.textView_latest_msg_row.text = chatPartnerUser?.username
                val targetImageView = viewHolder.itemView.imageView_latest_msg_row
                Picasso.get().load(chatPartnerUser?.profileImageUrl).into(targetImageView)
            }
        })
    }
}