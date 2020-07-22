package com.example.NoteProject.Singelton;

public class AudioSingleton
{
    private static AudioSingleton single_instance = null;

    // variable of type String
    public String audioUrl;

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    // static method to create instance of Singleton class
    public static AudioSingleton getInstance()
    {
        if (single_instance == null)
            single_instance = new AudioSingleton();

        return single_instance;
    }
}
