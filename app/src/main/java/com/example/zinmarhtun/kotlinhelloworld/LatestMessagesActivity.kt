package com.example.zinmarhtun.kotlinhelloworld

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.example.zinmarhtun.kotlinhelloworld.models.ChatMessage
import com.example.zinmarhtun.kotlinhelloworld.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_messages.*
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessagesActivity : AppCompatActivity() {

    companion object {
        val TAG = "LatestMessagesActivity"
        var currentUser: User? = null
    }

    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)

        recyclerview_latest_message.adapter = adapter

//        setupDummyRows()
        ListenForLatestMessages()

        fetchCurrentUser()

        Log.d(TAG, "onCreate ...")
        verifyUserIsLoggedIn()
    }

    private fun ListenForLatestMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val latestMsgRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")

        latestMsgRef.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                adapter.add(LatestMessageRow(chatMessage))
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
    }

    class LatestMessageRow(val chatMessage: ChatMessage): Item<ViewHolder>() {
        override fun getLayout(): Int {
            Log.d("LatestMessageActivity", "create latest_message_row ...")
            return R.layout.latest_message_row
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.chatMsg_latest_msg_row.text = chatMessage.text
        }
    }

    /*private fun setupDummyRows() {

        Log.d("LatestMessageActivity", "fun of setupDummyRows")
        val adapter = GroupAdapter<ViewHolder>()

        adapter.add(LatestMessageRow())
        adapter.add(LatestMessageRow())
        adapter.add(LatestMessageRow())
        adapter.add(LatestMessageRow())

        recyclerview_latest_message.adapter = adapter
        Log.d("LatestMessageActivity", "working ...")
    }*/

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
                Log.d(TAG, "Get Current User >> ${currentUser.toString()}")
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId){
            R.id.menu_new_message -> {
                val intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_signout -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        Log.d(TAG, "uid is $uid ...")
        if (uid == null){
            Log.d(TAG, "uid is null ...")
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}
