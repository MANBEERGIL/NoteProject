package com.example.NoteProject.Modals;

public class Map {

    private int mapId;
    private String lat;
    private String lng;
    private int noteId;

    public Map() {
    }

    public Map(int mapId, String lat,String lng, int noteId) {
        this.mapId = mapId;
        this.lat = lat;
        this.lng = lng;
        this.noteId = noteId;
    }

    public int getMapId() {
        return mapId;
    }

    public void setMapId(int mapId) {
        this.mapId = mapId;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }
}
