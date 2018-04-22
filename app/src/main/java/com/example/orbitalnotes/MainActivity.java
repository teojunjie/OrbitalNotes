package com.example.orbitalnotes;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private FloatingActionButton fabAddNote;
    private RecyclerView mRvNotes;
    private ArrayList<Note> noteList;
    private NotesRvAdapter notesRvAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Check if user is logged in
        // If user is not logged in, direct user to login page
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        // Initialise widgets
        fabAddNote = findViewById(R.id.fabAddNote);
        fabAddNote.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                writeNewNote();
            }
        });
        // Part 2: CRUD display
        noteList = new ArrayList<>();
        mRvNotes = findViewById(R.id.recycleViewNotes);
        mRvNotes = findViewById(R.id.recycleViewNotes);
        mRvNotes.setHasFixedSize(true);
        notesRvAdapter = new NotesRvAdapter(this, noteList);
        mRvNotes.setAdapter(notesRvAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRvNotes.setLayoutManager(linearLayoutManager);

        getFirebaseData(new NotesCallback() {
            @Override
            public void onCallBack(Note note) {
                noteList.add(note);
                notesRvAdapter.notifyDataSetChanged();
            }
        });
    }

    private void getFirebaseData(final NotesCallback notesCallback) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference notesRef = reference.child("notes");
        notesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Result will be holded Here
                for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                    Note note = new Note();
                    String noteId = String.valueOf(dataSnap.child("id").getValue());
                    String title = String.valueOf(dataSnap.child("title").getValue());
                    String message = String.valueOf(dataSnap.child("message").getValue());
                    note.setId(noteId);
                    note.setTitle(title);
                    note.setMessage(message);
                    notesCallback.onCallBack(note);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle db error
            }
        });
    }

    private void writeNewNote() {
        Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                userLogout();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Signout User and return user to LoginActivity
    private void userLogout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
