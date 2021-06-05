package com.example.task;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task.adapters.TaskAdapter;
import com.example.task.database.MyNotificationPublisher;
import com.example.task.database.PostsDatabaseHelper;
import com.example.task.models.Notification;
import com.example.task.models.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONObject;

public class TaskActivity extends AppCompatActivity implements View.OnClickListener, LifecycleOwner {

    //TAG == TAG
    private static final String TAG = "TaskActivity";
    private static final String TAG_USER_ID = "UserId";
    private static final String TAG_LIST_ID = "ListId";
    private static final String TAG_TASK_ID = "TaskId";
    private static final String TAG_JSON_ARRAY = "JSONArray";

    public static String NOTIFICATION_ID = "notification-id" ;
    public static String NOTIFICATION = "notification" ;
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;
    final Calendar myCalendar = Calendar.getInstance();

    RecyclerView _taskRecycleView;
    TaskAdapter taskAdapter;
    ArrayList<Task> models;
    androidx.appcompat.widget.Toolbar _toolbar;

    TaskActivity context;
    int position;
    int userId;
    int listId;

    FloatingActionButton _addTaskButton;
    PostsDatabaseHelper databaseHelper;
    private boolean reloadNeed = true;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        setTitle("MyTasks");

        context = this;
        userId = Integer.valueOf(getIntent().getStringExtra(TAG_USER_ID));
        listId = Integer.valueOf(getIntent().getStringExtra(TAG_LIST_ID));

        // Get singleton instance of database
        databaseHelper = PostsDatabaseHelper.getInstance(getApplicationContext());


        _taskRecycleView = findViewById(R.id.taskRecycleView);
        _addTaskButton = findViewById(R.id.addTaskButton);


        models = new ArrayList<Task>();

        taskAdapter = new TaskAdapter(getApplicationContext(), models, new TaskAdapter.Onclick() {
            @Override
            public void onEvent(Task modelRep, int pos) {
                for (int i = 0; i < models.size(); i++)
                    System.out.println("ListId " + models.get(i).getId());
                position = pos;
                Intent intent = new Intent(getApplicationContext(), EditTaskActivity.class);
                intent.putExtra(TAG_USER_ID, String.valueOf(userId));
                intent.putExtra(TAG_LIST_ID, String.valueOf(listId));
                intent.putExtra(TAG_TASK_ID, String.valueOf(models.get(pos).getId()));
                startActivity(intent);
            }
        });

        _addTaskButton.setOnClickListener(this);

        _toolbar = (Toolbar) findViewById(R.id.tasksToolbar);

        _taskRecycleView.setHasFixedSize(true);
        _taskRecycleView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        _taskRecycleView.setAdapter(taskAdapter);

        ArrayList<Task> tmpTasks = databaseHelper.getAllTasks(this.listId);
        models.addAll(tmpTasks);
        taskAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addTaskButton: {
                Intent intent = new Intent(getApplicationContext(), CreateTaskActivity.class);
                intent.putExtra(TAG_USER_ID, String.valueOf(userId));
                intent.putExtra(TAG_LIST_ID, String.valueOf(listId));
                startActivity(intent);
            }
            break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.task_toolbar, menu);
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

            case R.id.log_out_item_task_toolbar: {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                this.finish();
                break;
            }

            case R.id.rename_list: {
                renameList();
                break;
            }
            case R.id.share_via_bluetooth: {

                JSONArray jsonArray = new JSONArray();
                JSONObject object= models.get(0).getJSONObject();
                jsonArray.put(object);
                Toast.makeText(getBaseContext(), "List " + jsonArray, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), BluetoothActivity.class);
                intent.putExtra(TAG_USER_ID, String.valueOf(userId));
                intent.putExtra(TAG_LIST_ID, String.valueOf(listId));
                intent.putExtra(TAG_JSON_ARRAY, jsonArray.toString());
                startActivity(intent);
                break;
            }
            default:
                return super.onOptionsItemSelected(item);

        }
        return true;
    }


    private void renameList() {
        String oldListName = databaseHelper.getListName(listId);
        final EditText editTextName1 = new EditText(TaskActivity.this);
        AlertDialog.Builder alertName = new AlertDialog.Builder(this);

        alertName.setTitle(" oldListName rename: ");
        alertName.setMessage("Enter new title:");
        alertName.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String listTitle = String.valueOf(editTextName1.getText());
                if (listTitle == null || listTitle.trim().equals("")) {
                    Toast.makeText(getBaseContext(), "Please add a list name", Toast.LENGTH_LONG).show();
                }
                databaseHelper.updateList(listTitle, listId);
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


    public void reloadData() {
        //for(int i = 0; i < models.size(); i++){
        //    scheduleNotification(getNotification( models.get(i).getDate()) , Long.valueOf(models.get(i).getTime()), models.get(i).getNotification().getId()) ;
        //}
        //scheduleNotification(getNotification( btnDate .getText().toString()) , date.getTime()) ;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (this.reloadNeed)
            this.reloadData();

        this.reloadNeed = false; // do not reload anymore, unless I tell you so...
    }






}
