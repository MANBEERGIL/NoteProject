package com.example.NoteProject.Listeners;



public interface OnListItemClickListeners {

    void onListItemDelted(String id, int adapterPos);
    void onListItemEdited(String id, int adapterPos, String item_name);
    void onItemClicked(String id, int adapterPos);
}
