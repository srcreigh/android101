package com.hackthenorth.android101;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;


public class ChatroomActivity extends ActionBarActivity {

    private ListView mList;
    private MessagesAdapter mAdapter;
    private String mRoomName;

    private EditText mComposeView;
    private Button mSendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        // Get the room name that was passed as an extra in the intent
        mRoomName = getIntent().getStringExtra("text");

        // Pull out the views
        mList = (ListView)findViewById(R.id.messages);
        mComposeView = (EditText)findViewById(R.id.composeView);
        mSendButton = (Button)findViewById(R.id.sendButton);

        // Create the adapter and hook it up to the listview
        mAdapter = new MessagesAdapter(this, 0);
        mList.setAdapter(mAdapter);

        // When the user clicks the send button, we want to send the stuff in the text
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the text out of the edit text
                String message = mComposeView.getText().toString();

                // "Push" the message onto the Firebase
                // This creates a new random child on firebase that occurs *after* the previous
                // ones. For example, a push might add "aac":"hey there!" to this object:
                //
                // {
                //   "aaa": "I wonder where my friend is",
                //   "aab": "it will be good if he shows up..."
                // }
                //
                // Note that the keys are usually crazy things like "-Jsdajkljqwjdnalskdlkq". But
                // you can think of it like adding "aac" to "aaa", "aab".
                Firebase ref = new Firebase("https://android-101.firebaseio.com/" + mRoomName);
                ref.push().setValue(message);

                // Now just clear the text.
                mComposeView.setText("");
            }
        });

        // Show the messages that are in the chat room
        Firebase ref = new Firebase("https://android-101.firebaseio.com/" + mRoomName);
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // This method is called once for every key in the object:
                // {
                //   "foo1": "THIS FIRST",
                //   "foo2": "THIS SECOND",
                //   "foo2": "THIS THIRD",
                // }

                // Get the message out (we know they're strings)
                String message = dataSnapshot.getValue(String.class);

                // Add the message to the adapter
                mAdapter.addMessage(message);
            }

            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) { }
            @Override public void onChildRemoved(DataSnapshot dataSnapshot) { }
            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) { }
            @Override public void onCancelled(FirebaseError firebaseError) { }
        });
    }

    private static class MessagesAdapter extends ArrayAdapter<String> {
        private final Context mContext;
        private final ArrayList<String> mData;

        public MessagesAdapter(Context context, int resource) {
            super(context, resource);
            mContext = context;
            mData = new ArrayList<>();
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            String message = mData.get(position);
            if (convertView == null) {
                convertView = new TextView(mContext);
            }
            ((TextView)convertView).setText(message);
            return convertView;
        }

        public int getCount() {
            return mData.size();
        }

        public void addMessage(String message) {
            mData.add(message);
            notifyDataSetChanged();
        }
    }
}
