package com.example.task;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task.adapters.ListsAdapter;
import com.example.task.database.PostsDatabaseHelper;
import com.example.task.models.Lists;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import java.util.ArrayList;

public class ListsActivity extends AppCompatActivity implements View.OnClickListener, LifecycleOwner {


    ArrayList<Lists> models;

    private TextView _listsTextView;
    FloatingActionButton _addListButton;
    RecyclerView _listRecycleView;

    private static final String TAG_USER_ID = "UserId";
    private static final String TAG_LIST_ID = "ListId";
    private static final String TAG = "ListsActivity";

    ListsAdapter listsAdapter;
    ListsActivity context;

    int position;
    int userId;
    int currentListId;
    PostsDatabaseHelper databaseHelper;
    Toolbar _toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);
        setTitle("MyTasks");

        context = this;
        userId = Integer.valueOf(getIntent().getStringExtra(TAG_USER_ID));
        System.out.println("UserId" + userId);

        // Get singleton instance of database
        databaseHelper = PostsDatabaseHelper.getInstance(getApplicationContext());

        _addListButton = findViewById(R.id.addListButton);
        _listsTextView = findViewById(R.id.listTitle);
        _listRecycleView = findViewById(R.id.listsRecycleView);

        _toolbar = (Toolbar) findViewById(R.id.listsToolbar);

        models = new ArrayList<Lists>();

        listsAdapter = new ListsAdapter(getApplicationContext(), models, new ListsAdapter.Onclick() {
            @Override
            public void onEvent(Lists modelRep, int pos) {
                position = pos;
                Intent intent = new Intent(getApplicationContext(), TaskActivity.class);
                intent.putExtra(TAG_USER_ID, String.valueOf(userId));
                intent.putExtra(TAG_LIST_ID, String.valueOf(models.get(pos).getId()));
                startActivity(intent);
            }
        });


        _addListButton.setOnClickListener(this);

        _listRecycleView.setHasFixedSize(true);
        _listRecycleView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        _listRecycleView.setAdapter(listsAdapter);

        ArrayList<Lists> tmpLists = databaseHelper.getLists(this.userId);
        models.addAll(tmpLists);
        listsAdapter.notifyDataSetChanged();


    }

    private void createNewList() {
        final EditText editTextName1 = new EditText(ListsActivity.this);
        AlertDialog.Builder alertName = new AlertDialog.Builder(this);

        alertName.setTitle("Create New List");
        alertName.setMessage("Enter the title:");
        alertName.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String listTitle = String.valueOf(editTextName1.getText());

                if (listTitle == null || listTitle.trim().equals("")) {
                    Toast.makeText(getBaseContext(), "Please add a group name", Toast.LENGTH_LONG).show();
                }
                currentListId = databaseHelper.addList(listTitle, userId);
                insertMethod(listTitle, currentListId);
            }
        });
        alertName.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        alertName.setView(editTextName1);
        LinearLayout layoutName = new LinearLayout(this);
        layoutName.setOrientation(LinearLayout.VERTICAL);
        layoutName.addView(editTextName1); // displays the user input bar

        alertName.setView(layoutName);
        alertName.show();// display the dialog

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addListButton: {
                createNewList();
            }
            break;
        }

    }

    private void insertMethod(String _title, int _id) {

        Gson gson = new Gson();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", _id);
            jsonObject.put("title", _title);
            jsonObject.put("userId", this.userId);
            Lists modelRep = gson.fromJson(String.valueOf(jsonObject), Lists.class);

            models.add(modelRep);
            listsAdapter.notifyDataSetChanged();

            Toast.makeText(getApplicationContext(), "Added succesfully!", Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.lists_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            }

            case R.id.log_out_item_list_toolbar: {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                this.finish();
                break;
            }
            default:
                return super.onOptionsItemSelected(item);

        }
        return true;
    }


}