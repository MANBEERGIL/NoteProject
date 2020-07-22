package com.example.NoteProject.Modals;

import java.io.Serializable;

public class Note implements Serializable {

    private String subjectName;
    private int noteId;
    private String noteTitle;
    private String noteContent;
    private String audio;
    private String dateTime;
    private double latitude;
    private double longitude;
    private int imageId;
    private String image1;
    private String image2;
    private String image3;



    private String toDoItems;



    public Note(){

    }

    public Note(String noteTitle, String noteContent) {
        this.noteTitle = noteTitle;
        this.noteContent = noteContent;
    }

    public Note(String subjectName, int noteId, String noteTitle, String noteContent, String audio, String dateTime, double latitude, double longitude, int imageId, String toDoItems) {
        this.subjectName = subjectName;
        this.noteId = noteId;
        this.noteTitle = noteTitle;
        this.noteContent = noteContent;
        this.audio = audio;
        this.dateTime = dateTime; // sonia change
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageId = imageId;
        this.toDoItems = toDoItems;
    }


    public String getToDoItems() {
        return toDoItems;
    }

    public void setToDoItems(String toDoItems) {
        this.toDoItems = toDoItems;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {

        this.dateTime = dateTime;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getImage3() {
        return image3;
    }

    public void setImage3(String image3) {
        this.image3 = image3;
    }
}
