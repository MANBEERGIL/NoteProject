package com.example.NoteProject.Adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.NoteProject.Listeners.OnListItemClickListeners;
import com.example.NoteProject.Modals.Note;
import com.example.NoteProject.NoteActivity;
import com.example.NoteProject.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class NotesListAdapter extends RecyclerView.Adapter<NotesListAdapter.NotesListViewHolder> {
    private Context mContext;
    OnListItemClickListeners onListItemClickListeners;
    ArrayList<Note> notesArray;
    private boolean isSelectedAll;
    private int checkCount;
    private NoteActivity notesActivity;
    ArrayList<Note> allNotes;



    public NotesListAdapter(Context mContext, ArrayList subjectList, OnListItemClickListeners onListItemClickListeners) {
        this.notesArray = subjectList;
        allNotes =  new ArrayList<>();
        allNotes.addAll(notesArray);
        notesActivity = (NoteActivity) mContext;
        this.mContext = mContext;
        this.onListItemClickListeners = onListItemClickListeners;
    }

    @NonNull
    @Override
    public NotesListAdapter.NotesListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.custom_notescell, parent, false);
        return new NotesListAdapter.NotesListViewHolder(rootView);
    }



    @Override
    public void onBindViewHolder(@NonNull final NotesListAdapter.NotesListViewHolder holder, final int position) {

        holder.title_tv.setText(notesArray.get(position).getNoteTitle());


        try {
            String dateStr = notesArray.get(position).getDateTime();
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            java.util.Date date = formatter.parse(dateStr);

            if (isTomorrow(date)) {
                holder.date_tv.setText("Tomorrow");
            } else if (isToday(date)) {
                holder.date_tv.setText("Today");
            } else {
                SimpleDateFormat month_date = new SimpleDateFormat("MMMM dd ", Locale.getDefault());
                String month_name = month_date.format(date);
                holder.date_tv.setText(month_name);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        if (position + 1 == getItemCount()) {
            setBottomMargin(holder.itemView, (int) (72 * Resources.getSystem().getDisplayMetrics().density));
        } else {
            setBottomMargin(holder.itemView, 0);
        }

    }

    public static boolean isTomorrow(Date d) {
        return DateUtils.isToday(d.getTime() - DateUtils.DAY_IN_MILLIS);
    }

    public static boolean isToday(Date d) {
        return DateUtils.isToday(d.getTime());
    }

    @Override
    public int getItemCount() {
        return notesArray.size();
    }

    public void deleteItem(int index) {
        notesArray.remove(index);
        notifyItemRemoved(index);
    }

    public void addItem(int index) {
        notifyItemInserted(index);
    }


    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        notesActivity.savedNoteArrayList.clear();
        if (charText.length() == 0) {
            notesActivity.savedNoteArrayList.addAll(allNotes);
        } else {
            for (Note wp: allNotes){
                if (wp.getNoteTitle().toLowerCase(Locale.getDefault()).contains(charText)) {
                    notesActivity.savedNoteArrayList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

    public static void setBottomMargin(View view, int bottomMargin) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, bottomMargin);
            view.requestLayout();
        }
    }

    public class NotesListViewHolder extends RecyclerView.ViewHolder {
        private TextView title_tv,date_tv;
        private ImageView delete_iv, edit_iv;

        public NotesListViewHolder(final View itemView) {
            super(itemView);
            title_tv = itemView.findViewById(R.id.title_tv);
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
                    onListItemClickListeners.onListItemDelted("" + notesArray.get(getAdapterPosition()).getNoteId(), getAdapterPosition());
                }
            });

            edit_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onListItemClickListeners.onListItemEdited("" + notesArray.get(getAdapterPosition()).getNoteId(), getAdapterPosition(), "");
                }
            });
        }
    }
}
