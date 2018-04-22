package com.example.orbitalnotes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditNoteActivity extends AppCompatActivity {
    private EditText mEditTextTitle;
    private EditText mEditTextMessage;
    private Button mBtnDel;
    private String noteId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        // Initialise Widgets
        mEditTextTitle = findViewById(R.id.editTextTitle);
        mEditTextMessage = findViewById(R.id.editTextMessage);
        mBtnDel = findViewById(R.id.buttonDelete);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Get Note from bundle
        Bundle b = this.getIntent().getExtras();
        if (b != null){
            Note note = b.getParcelable("note");
            mEditTextTitle.setText(note.getTitle());
            mEditTextMessage.setText(note.getMessage());
            noteId = note.getId();

        } else {
            new NullPointerException();
        }

        mBtnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNote();
                backToMainActivity();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_note, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        backToMainActivity();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addnote:
                // Add Note to FireBase
                updateNote();
                // Back to MainActivity
                backToMainActivity();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateNote() {
        Note note = new Note();
        note.setTitle(mEditTextTitle.getText().toString());
        note.setMessage(mEditTextMessage.getText().toString());
        note.setId(noteId);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("notes");
        mDatabase.child(noteId).setValue(note);

    }

    private void deleteNote() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("notes");
        mDatabase.child(noteId).removeValue();
    }

    private void backToMainActivity() {
        Intent intent = new Intent(EditNoteActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
