package com.example.NoteProject.Modals;

public class Image {

    private int imageId;
    private String imageLocation;
    private int noteId;

    public Image() {
    }

    public Image(int imageId, String imageLocation, int noteId) {
        this.imageId = imageId;
        this.imageLocation = imageLocation;
        this.noteId = noteId;
    }

    public String getImageLocation() {
        return imageLocation;
    }

    public void setImageLocation(String imageLocation) {
        this.imageLocation = imageLocation;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

}
