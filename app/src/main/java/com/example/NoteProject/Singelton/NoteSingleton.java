package com.example.NoteProject.Singelton;


import com.example.NoteProject.Modals.Note;

public class NoteSingleton
{
    // static variable single_instance of type Singleton
    private static NoteSingleton single_instance = null;

    // variable of type String
    public Note note;

    public Note getNoteObject() {
        return note;
    }

    public void setNoteObject(Note note) {
        this.note = note;
    }

    // static method to create instance of Singleton class
    public static NoteSingleton getInstance()
    {
        if (single_instance == null)
            single_instance = new NoteSingleton();

        return single_instance;
    }
}
