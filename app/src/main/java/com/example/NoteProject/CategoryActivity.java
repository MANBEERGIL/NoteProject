package com.example.NoteProject;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.NoteProject.Adapters.ListDescriptionAdapter;
import com.example.NoteProject.Database.DBNote;
import com.example.NoteProject.Database.DBSubject;
import com.example.NoteProject.Listeners.OnListItemClickListeners;
import com.example.NoteProject.Modals.Subject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CategoryActivity extends AppCompatActivity implements OnListItemClickListeners {

    @BindView(R.id.categories_rv)
    RecyclerView categoriesRv;
    @BindView(R.id.dashboard_tv)
    TextView dashboardTv;
    @BindView(R.id.add_event_iv)
    ImageView addEventIv;
    DBSubject dbSubject = new DBSubject(CategoryActivity.this);
    DBNote dbNote = new DBNote(CategoryActivity.this);
    ArrayList<Subject> savedSubjectsArraylist = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private ListDescriptionAdapter listDescriptionAdapter;
    Context mContext;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        ButterKnife.bind(this);
        mContext = CategoryActivity.this;
        savedSubjectsArraylist = dbSubject.getAllSubject(mContext);
        setupAllCat();

    }

    private void setupAllCat() {
        categoriesRv.setVisibility(View.VISIBLE);
        setupDescriptionAdapter();
    }

    private void setupDescriptionAdapter() {
        savedSubjectsArraylist = dbSubject.getAllSubject(mContext);
        listDescriptionAdapter = new ListDescriptionAdapter(mContext, savedSubjectsArraylist, this);
        linearLayoutManager = new LinearLayoutManager(mContext);
        categoriesRv.setLayoutManager(linearLayoutManager);
        categoriesRv.setAdapter(listDescriptionAdapter);
    }


    @Override
    protected void onRestart() {
        super.onRestart();
            setupDescriptionAdapter();

    }
    public Subject addSubject(String subjectName) {
        Subject addSubject = new Subject();
        addSubject.setSubjectName(subjectName);
        return addSubject;
    }

    @OnClick({R.id.add_event_iv})
    public void onViewClicked(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Add Category");
                    final EditText input = new EditText(this);
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                    builder.setView(input);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        private String m_Text;

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            m_Text = input.getText().toString();
                            boolean flag = false;
                            if (m_Text != null && m_Text.length() > 0) {
                                if (savedSubjectsArraylist != null && savedSubjectsArraylist.size() > 0) {
                                    for (int i = 0; i < savedSubjectsArraylist.size(); i++) {
                                        if (m_Text.contains(savedSubjectsArraylist.get(i).getSubjectName())) {
                                            flag = true;
                                        }
                                    }
                                    if (flag == false) {
                                        dbSubject.insertSubject(addSubject(m_Text));
                                        setupDescriptionAdapter();
                                    }
                                } else {
                                    dbSubject.insertSubject(addSubject(m_Text));
                                    setupDescriptionAdapter();
                                }
                            }
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();


    }
    @Override
    public void onListItemDelted(String id, int adapterPos) {
        showDialogForDelete(adapterPos);
    }
    public void showDialogForDelete(final int i1) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle("Are you Sure to Delete");
        alertDialogBuilder.setCancelable(true);

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                    dbSubject.deleteSubject(savedSubjectsArraylist.get(i1));
                    dbNote.deleteNoteWithSubject((savedSubjectsArraylist.get(i1)));
                    setupDescriptionAdapter();
                    listDescriptionAdapter.notifyDataSetChanged();


            }
        });


        AlertDialog mAlertDialog = alertDialogBuilder.create();
        mAlertDialog.show();
    }

    @Override
    public void onListItemEdited(String id, final int adapterPos, String item_name) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle("Are You Sure to Delete");
        alertDialogBuilder.setCancelable(true);

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                    Subject subject = new Subject();
                    subject = savedSubjectsArraylist.get(adapterPos);

                    dbSubject.deleteSubject(savedSubjectsArraylist.get(adapterPos));
                    dbNote.deleteNoteWithSubject((savedSubjectsArraylist.get(adapterPos)));

                    setupDescriptionAdapter();
                    listDescriptionAdapter.notifyDataSetChanged();

            }
        });


        AlertDialog mAlertDialog = alertDialogBuilder.create();
        mAlertDialog.show();
    }


    @Override
    public void onItemClicked(String id, int adapterPos) {
        Intent intentToNote = new Intent(mContext, NoteActivity.class);
        intentToNote.putExtra("categoryName", savedSubjectsArraylist.get(adapterPos).getSubjectName());
        startActivity(intentToNote);

    }
}