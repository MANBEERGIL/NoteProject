package com.example.NoteProject.Singelton;

public class SubjectSingleton
{

    private static SubjectSingleton single_instance = null;
    public String subjectName;

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public static SubjectSingleton getInstance()
    {
        if (single_instance == null)
            single_instance = new SubjectSingleton();

        return single_instance;
    }
}