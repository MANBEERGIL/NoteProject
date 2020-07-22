package com.example.NoteProject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.NoteProject.Adapters.NotesListAdapter;
import com.example.NoteProject.Database.DBNote;
import com.example.NoteProject.Listeners.OnListItemClickListeners;
import com.example.NoteProject.Modals.Note;
import com.example.NoteProject.Singelton.SubjectSingleton;
import com.example.NoteProject.Utils.ShadowLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NoteActivity extends AppCompatActivity implements OnListItemClickListeners, SearchView.OnQueryTextListener {
    public ArrayList<Note> savedNoteArrayList = new ArrayList<>();
    @BindView(R.id.dashboard_tv)
    TextView dashboardTv;
    @BindView(R.id.dash_parent_rl)
    RelativeLayout dashParentRl;
    @BindView(R.id.etSearch)
    SearchView etSearch;
    @BindView(R.id.categories_rv)
    RecyclerView categoriesRv;
    @BindView(R.id.add_event_iv)
    ImageView addEventIv;
    @BindView(R.id.rel_ll)
    RelativeLayout relLl;
    @BindView(R.id.dash_parent_fl)
    FrameLayout dashParentFl;

    Context mContext;
    String subjectName;
    DBNote dbNote = new DBNote(this);
    LinearLayoutManager linearLayoutManager;
    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.mdw_view_tv)
    TextView mdwViewTv;
    @BindView(R.id.arrow_down_iv0)
    ImageView arrowDownIv0;
    @BindView(R.id.spinner_parent_rl1)
    RelativeLayout spinnerParentRl1;
    @BindView(R.id.arrow_down_iv)
    ImageView arrowDownIv;
    @BindView(R.id.spinner_parent_rl)
    RelativeLayout spinnerParentRl;
    @BindView(R.id.spinner_sl)
    ShadowLayout spinnerSl;

    private NotesListAdapter notesListAdapter;
    private int clickCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        ButterKnife.bind(this);
        mContext = NoteActivity.this;

        if (getIntent().getExtras() != null) {
            subjectName = getIntent().getExtras().get("categoryName").toString();
            dashboardTv.setText(subjectName);
        }

        if (subjectName != null) {
            SubjectSingleton.getInstance().setSubjectName(subjectName);
        } else {
            subjectName = SubjectSingleton.getInstance().getSubjectName();
        }
        etSearch.setOnQueryTextListener(this);
        setupDescAdapter();
    }


    private void setupDescAdapter() {
        savedNoteArrayList.clear();
        savedNoteArrayList = dbNote.getNoteOfSubject(this, subjectName);
        notesListAdapter = new NotesListAdapter(mContext, savedNoteArrayList, this);
        linearLayoutManager = new LinearLayoutManager(mContext);
        categoriesRv.setLayoutManager(linearLayoutManager);
        categoriesRv.setAdapter(notesListAdapter);
    }

    @OnClick({R.id.etSearch, R.id.add_event_iv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.etSearch:
                break;
            case R.id.add_event_iv:
                Intent intentToNote = new Intent(getApplicationContext(), AddNoteActivity.class);
                Bundle b = new Bundle();
                b.putString("categoryName", subjectName);
                intentToNote.putExtras(b);
                startActivityForResult(intentToNote, 45);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 45) {
            if (resultCode == Activity.RESULT_OK) {
                if (notesListAdapter != null) {
                    setupDescAdapter();
                }
            }
        }
    }

    @Override
    public void onListItemDelted(String id, final int adapterPos) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle("Are you Sure to Delete");
        alertDialogBuilder.setCancelable(true);

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dbNote.deleteNote(savedNoteArrayList.get(adapterPos));
                setupDescAdapter();
            }
        });

        AlertDialog mAlertDialog = alertDialogBuilder.create();
        mAlertDialog.show();


    }

    @Override
    public void onListItemEdited(String id, int adapterPos, String item_name) {
    }


    @Override
    public void onItemClicked(String id, int adapterPos) {
        Intent intentToEditNote = new Intent(getApplicationContext(), AddNoteActivity.class);
        Bundle editNoteBundle = new Bundle();
        editNoteBundle.putBoolean("isEdit", true);
        editNoteBundle.putSerializable("NoteData", savedNoteArrayList.get(adapterPos));
        intentToEditNote.putExtras(editNoteBundle);
        startActivityForResult(intentToEditNote, 45);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        String text = s;
        notesListAdapter.filter(text);
        return false;
    }
    @OnClick(R.id.spinner_sl)
    public void onViewClicked() {

        clickCount++;
        if (clickCount == 0) {
            setupOrderByDate();
        } else if (clickCount == 1) {
            setupOrderByTitle();
        } else if (clickCount == 2) {
            setupOrderByDesc();
            clickCount = -1;

        }
    }

    private void setupOrderByDesc() {
        spinnerParentRl1.setBackgroundResource(R.drawable.custom_daily_view_background);
        spinnerSl.setShadowColor(getResources().getColor(R.color.daily_view_shadow));
        mdwViewTv.setText("BY DESC");
        Collections.sort(savedNoteArrayList, new Comparator<Note>() {
            public int compare(Note o1, Note o2) {
                return o1.getNoteContent().compareTo(o2.getNoteContent());
            }
        });
        notesListAdapter.notifyDataSetChanged();
    }

    private void setupOrderByTitle() {
        spinnerParentRl1.setBackgroundResource(R.drawable.custom_daily_view_background);
        spinnerSl.setShadowColor(getResources().getColor(R.color.daily_view_shadow));
        mdwViewTv.setText("BY TITLE");

        Collections.sort(savedNoteArrayList, new Comparator<Note>() {
            public int compare(Note o1, Note o2) {
                return o1.getNoteTitle().compareTo(o2.getNoteTitle());
            }
        });
        notesListAdapter.notifyDataSetChanged();
    }

    private void setupOrderByDate() {
        spinnerParentRl1.setBackgroundResource(R.drawable.custom_daily_view_background);
        spinnerSl.setShadowColor(getResources().getColor(R.color.daily_view_shadow));
        mdwViewTv.setText("BY DATE");
        Collections.sort(savedNoteArrayList, new Comparator<Note>() {
            public int compare(Note o1, Note o2) {
                return o1.getDateTime().compareTo(o2.getDateTime());
            }
        });
        notesListAdapter.notifyDataSetChanged();
    }


}
