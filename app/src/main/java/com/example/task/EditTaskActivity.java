package com.example.task;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleOwner;

import com.example.task.database.MyNotificationPublisher;
import com.example.task.database.PostsDatabaseHelper;
import com.example.task.models.Notification;
import com.example.task.models.Task;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class EditTaskActivity extends AppCompatActivity implements View.OnClickListener, LifecycleOwner {

    PostsDatabaseHelper databaseHelper;

    private static final String TAG = "EditTaskActivity";
    private static final String TAG_USER_ID = "UserId";
    private static final String TAG_LIST_ID = "ListId";
    private static final String TAG_TASK_ID = "TaskId";
    public static String NOTIFICATION_ID = "notification-id" ;
    public static String NOTIFICATION = "notification" ;
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;
    final Calendar myCalendar = Calendar.getInstance();

    int userId;
    int listId;
    int taskId;

    Button _notificationButton;
    Button _createButton;
    Button _returnButton;
    Button _deleteButton;

    EditText _date_time;
    private EditText _contentTextView;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    Task currentTask;

    final Calendar calendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    _date_time.setText(simpleDateFormat.format(calendar.getTime()));
                }
            };

            new TimePickerDialog(EditTaskActivity.this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        setTitle("MyTasks");

        // Get singleton instance of database
        databaseHelper = PostsDatabaseHelper.getInstance(getApplicationContext());
        userId = Integer.valueOf(getIntent().getStringExtra(TAG_USER_ID));
        listId = Integer.valueOf(getIntent().getStringExtra(TAG_LIST_ID));
        taskId = Integer.valueOf(getIntent().getStringExtra(TAG_TASK_ID));


        _notificationButton = findViewById(R.id.editAddNotification);
        _createButton = findViewById(R.id.saveEditButton);
        _returnButton = findViewById(R.id.returnEditButton);
        _deleteButton = findViewById(R.id.deleteEditButton);
        _contentTextView = findViewById(R.id.editContent);
        _date_time = findViewById(R.id.date_time_input_edit);
        _date_time.setEnabled(true);

        _notificationButton.setOnClickListener(this);
        _createButton.setOnClickListener(this);
        _returnButton.setOnClickListener(this);
        _deleteButton.setOnClickListener(this);

        currentTask = databaseHelper.getTask(taskId, listId);
        _contentTextView.setText(currentTask.getContent());
        _date_time.setText(currentTask.getDate() + " " + currentTask.getTime());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editAddNotification: {
                editNotification();
            }
            break;

            case R.id.deleteEditButton: {
                deleteTask();
            }
            break;
            case R.id.returnEditButton: {
                returnToTasks();
            }
            break;
            case R.id.saveEditButton: {
                updateTask();
            }
            break;
        }

    }

    public void updateTask() {

        String dateTimeString = String.valueOf(_date_time.getText());


        if (String.valueOf(dateTimeString).isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            dateTimeString = String.valueOf(now.format(formatter));
        }

        String[] parts = dateTimeString.split(" ");
        String date = parts[0];
        String time = parts[1];
        String content = String.valueOf(_contentTextView.getText());

        if (content.isEmpty()) {
            _contentTextView.setError("At least 4 characters");
        } else {
            _contentTextView.setError(null);
        }

        databaseHelper.updateTaskContent(content, currentTask.id);
        databaseHelper.updateNotification(date, time, currentTask.notification.id);

        Toast.makeText(getBaseContext(), "Task was updated successfully!", Toast.LENGTH_LONG).show();
    }

    public void returnToTasks() {
        Intent intent = new Intent(getApplicationContext(), TaskActivity.class);
        intent.putExtra(TAG_USER_ID, String.valueOf(userId));
        intent.putExtra(TAG_LIST_ID, String.valueOf(listId));
        startActivity(intent);
        finish();
    }

    public void editNotification() {
        new DatePickerDialog(EditTaskActivity.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void deleteTask() {

        boolean answerNotification = databaseHelper.deleteNotification(this.currentTask.notification.getId());
        boolean answerTask = databaseHelper.deleteTask(this.taskId);

       if(answerNotification) {
           Toast.makeText(getBaseContext(), "Task was successfully deleted", Toast.LENGTH_LONG).show();
           _contentTextView.setText("");
           _date_time.setText("");
       }
       else Toast.makeText(getBaseContext(), "Error while deleting task", Toast.LENGTH_LONG).show();

    }

    public void removeNotification(int notificationId) {
        NotificationManager nMgr = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancel(notificationId);
    }

    public void scheduleNotification (android.app.Notification notification , long delay, int notificationId) {
        Intent notificationIntent = new Intent( this, MyNotificationPublisher. class ) ;
        notificationIntent.putExtra(NOTIFICATION_ID , notificationId ) ;
        notificationIntent.putExtra(NOTIFICATION , (Parcelable) notification) ;
        PendingIntent pendingIntent = PendingIntent. getBroadcast ( this, 0 , notificationIntent , PendingIntent. FLAG_UPDATE_CURRENT ) ;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context. ALARM_SERVICE ) ;
        assert alarmManager != null;
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP , delay , pendingIntent) ;
    }
    public android.app.Notification getNotification (String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder( this, default_notification_channel_id ) ;
        builder.setContentTitle( "Scheduled Notification" ) ;
        builder.setContentText(content) ;
        builder.setSmallIcon(R.drawable. ic_launcher_foreground ) ;
        builder.setAutoCancel( true ) ;
        builder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
        return builder.build() ;
    }



}