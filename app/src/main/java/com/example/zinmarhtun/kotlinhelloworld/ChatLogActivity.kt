package com.example.zinmarhtun.kotlinhelloworld

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.zinmarhtun.kotlinhelloworld.models.ChatMessage
import com.example.zinmarhtun.kotlinhelloworld.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatLogActivity : AppCompatActivity() {

    companion object {
        val TAG = "ChatLogActivity"
    }

    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

//        val username = intent.getStringExtra(NewMessageActivity.USER_KEY)
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        Log.d("Chat", "UserName : ${user.uid}")
        supportActionBar?.title = user.username

        recyclerview_chat_log.adapter = adapter

//        setupDummyData()

        ListenForMessages()

        send_btn_chat_log.setOnClickListener {
            Log.d(TAG, "Attempt to send message...")
            performSendMessage()
        }
    }

    private fun ListenForMessages() {
        val ref = FirebaseDatabase.getInstance().getReference("/messages")
        ref.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)
                if (chatMessage != null) {
                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid)
                        adapter.add(ChatFromItem(chatMessage.text))
                    else
                        adapter.add(ChatToItem(chatMessage.text))
                }
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
    }

    private fun performSendMessage() {
        val text = edittext_chat_log.text.toString()
        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user.uid

        if (fromId == null) return
        val ref = FirebaseDatabase.getInstance().getReference("/messages").push()
        val chatMessage = ChatMessage(ref.key!!, text, fromId, toId, System.currentTimeMillis()/1000)
        ref.setValue(chatMessage)
                .addOnSuccessListener {
                    Log.d(TAG, "Saved our message: ${ref.key}")
                }
    }

    private fun setupDummyData() {
        val adapter = GroupAdapter<ViewHolder>()
        adapter.add(ChatFromItem("From message .....  ..... ..... "))
        adapter.add(ChatToItem("To message .....  ..... ..... "))
        adapter.add(ChatFromItem("From message .....  ..... ..... "))
        adapter.add(ChatToItem("To message .....  ..... ..... "))
        adapter.add(ChatFromItem("From message .....  ..... ..... "))
        adapter.add(ChatToItem("To message .....  ..... ..... "))
        adapter.add(ChatFromItem("From message .....  ..... ..... "))
        adapter.add(ChatToItem("To message .....  ..... ..... "))
        adapter.add(ChatFromItem("From message .....  ..... ..... "))
        adapter.add(ChatToItem("To message .....  ..... ..... "))
        adapter.add(ChatFromItem("From message .....  ..... ..... "))
        adapter.add(ChatToItem("To message .....  ..... ..... "))

        recyclerview_chat_log.adapter = adapter
    }
}

class ChatFromItem(val text: String): Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_from_row.text = text
    }
}

class ChatToItem(val text: String): Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_to_row.text = text
    }
}