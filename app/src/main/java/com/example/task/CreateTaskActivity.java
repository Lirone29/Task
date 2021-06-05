package com.example.task;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;

import android.app.Dialog;
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

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleOwner;

import com.example.task.database.MyNotificationPublisher;
import com.example.task.database.PostsDatabaseHelper;
import com.example.task.models.Notification;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class CreateTaskActivity extends Activity implements View.OnClickListener {

    PostsDatabaseHelper databaseHelper;

    private static final String TAG = "CreateTaskActivity";
    private static final String TAG_USER_ID = "UserId";
    private static final String TAG_LIST_ID = "ListId";
    public static String NOTIFICATION_ID = "notification-id" ;
    public static String NOTIFICATION = "notification" ;
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;
    final Calendar myCalendar = Calendar.getInstance();

    Button _notificationButton;
    Button _createButton;
    Button _returnButton;

    int userId;
    int listId;

    EditText _date_time;
    private EditText _contentTextView;
    Context context;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

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

            new TimePickerDialog(CreateTaskActivity.this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        setTitle("MyTasks");

        context = getApplicationContext();
        databaseHelper = PostsDatabaseHelper.getInstance(getApplicationContext());

        userId = Integer.valueOf(getIntent().getStringExtra(TAG_USER_ID));
        listId = Integer.valueOf(getIntent().getStringExtra(TAG_LIST_ID));

        _date_time = findViewById(R.id.date_time_input);
        _date_time.setEnabled(false);

        _contentTextView = findViewById(R.id.content);
        _notificationButton = findViewById(R.id.addNotification);
        _createButton = findViewById(R.id.createButton);
        _returnButton = findViewById(R.id.returnButton);


        _notificationButton.setOnClickListener(this);
        _createButton.setOnClickListener(this);
        _returnButton.setOnClickListener(this);

    }


    private void showDateTimeDialog() {
        new DatePickerDialog(CreateTaskActivity.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.createButton: {
                createTask();
            }
            break;
            case R.id.returnButton: {
                returnToTask();
            }
            break;

            case R.id.addNotification: {
                addNotification();
            }
            break;
        }

    }

    public void createTask() {
        String dateTimeString = String.valueOf(_date_time.getText());

        if (String.valueOf(dateTimeString).isEmpty()) {

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            dateTimeString = String.valueOf(now.format(formatter));
        }

        String[] parts = dateTimeString.split(" ");
        String content = String.valueOf(_contentTextView.getText());

        if (content.isEmpty()) {
            _contentTextView.setError("At least 4 characters");
        } else {
            _contentTextView.setError(null);
        }

        int tmpTaskId = databaseHelper.createTask(content, this.listId, false);
        int tmpNotificationId = databaseHelper.createNotification(new Notification(tmpTaskId, parts[0], parts[1]));

        if (tmpTaskId < 0) _contentTextView.setError("Task cant be created!!");
        if (tmpNotificationId < 0) _contentTextView.setError("Notification!!");

        Toast.makeText(getBaseContext(), "The Task was created successfully!", Toast.LENGTH_LONG).show();

        android.app.Notification notification = getNotification(parts[0]);
        scheduleNotification(notification,Long.valueOf(parts[1]), tmpNotificationId);
        returnToTask();
    }


    public void returnToTask() {
        Intent intent = new Intent(getApplicationContext(), TaskActivity.class);
        intent.putExtra(TAG_USER_ID, String.valueOf(userId));
        intent.putExtra(TAG_LIST_ID, String.valueOf(listId));
        startActivity(intent);
        finish();
    }

    public void addNotification() {
        _date_time.setEnabled(true);
        showDateTimeDialog();
    }

    public void scheduleNotification (android.app.Notification notification , long delay, int notificationId) {
        Intent notificationIntent = new Intent( this, MyNotificationPublisher. class ) ;
        notificationIntent.putExtra(NOTIFICATION_ID , notificationId ) ;
        notificationIntent.putExtra(NOTIFICATION , (Parcelable) notification) ;
        PendingIntent pendingIntent = PendingIntent. getBroadcast ( this, 0 , notificationIntent , PendingIntent. FLAG_UPDATE_CURRENT ) ;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context. ALARM_SERVICE ) ;
        assert alarmManager != null;
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP , delay , pendingIntent) ;
        System.out.println(" In scheduleNotification");
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
