package com.example.NoteProject.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.NoteProject.Database.DBNote;
import com.example.NoteProject.Listeners.OnListItemClickListeners;
import com.example.NoteProject.Modals.Subject;
import com.example.NoteProject.R;

import java.util.ArrayList;


public class ListDescriptionAdapter extends RecyclerView.Adapter<ListDescriptionAdapter.ListDescriptionVieHolder> {
    private Context mContext;

    OnListItemClickListeners onListItemClickListeners;
    ArrayList<Subject> categories;
    private boolean isSelectedAll;
    private int checkCount;
    DBNote dbNote;



    public ListDescriptionAdapter(Context mContext, ArrayList subjectList, OnListItemClickListeners onListItemClickListeners) {
        this.categories = subjectList;
        this.mContext = mContext;
        this.onListItemClickListeners = onListItemClickListeners;
        dbNote = new DBNote(mContext);
    }

    @NonNull
    @Override
    public ListDescriptionAdapter.ListDescriptionVieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.custom_list_desc_row, parent, false);
        return new ListDescriptionAdapter.ListDescriptionVieHolder(rootView);
    }


    @Override
    public void onBindViewHolder(@NonNull final ListDescriptionAdapter.ListDescriptionVieHolder holder, final int position) {
        holder.title_tv.setText(categories.get(position).getSubjectName());
        holder.date_tv.setText(""+dbNote.getNoteOfSubject(mContext,categories.get(position).getSubjectName()).size());


    }

    @Override
    public int getItemCount() {
        return categories.size();
    }




    public class ListDescriptionVieHolder extends RecyclerView.ViewHolder {
        private TextView title_tv, date_tv;
        private CheckBox checkBox;
        private ImageView delete_iv, edit_iv;

        public ListDescriptionVieHolder(final View itemView) {
            super(itemView);
            title_tv = itemView.findViewById(R.id.title_tv);
            checkBox = itemView.findViewById(R.id.desc_check_box);
            delete_iv = itemView.findViewById(R.id.delete_iv);
            edit_iv = itemView.findViewById(R.id.edit_iv);
            date_tv = itemView.findViewById(R.id.date_tv);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onListItemClickListeners.onItemClicked("",getAdapterPosition());
                }
            });

            delete_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onListItemClickListeners.onListItemDelted("" + categories.get(getAdapterPosition()).getSubjectId(), getAdapterPosition());
                }
            });

            edit_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onListItemClickListeners.onListItemEdited("" + categories.get(getAdapterPosition()).getSubjectId(), getAdapterPosition(), "");
                }
            });
        }
    }
}