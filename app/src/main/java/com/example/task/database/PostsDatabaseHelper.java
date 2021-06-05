package com.example.task.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.task.models.Lists;
import com.example.task.models.Notification;
import com.example.task.models.Task;
import com.example.task.models.User;

import java.util.ArrayList;
import java.util.List;

public class PostsDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "";
    private static PostsDatabaseHelper sInstance;

    private static final String DATABASE_NAME = "Database1.db";
    private static final int DATABASE_VERSION = 3;

    // Table Names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_LISTS = "lists";
    private static final String TABLE_NOTIFICATIONS = "notifications";
    private static final String TABLE_ACTIVENESS = "activeness";
    private static final String TABLE_TASKS = "tasks";

    // Users Table Columns
    private static final String KEY_USER_ID = "id";
    private static final String KEY_USER_NAME = "username";
    private static final String KEY_USER_PASSWORD = "password";

    // Lists Table Columns
    private static final String KEY_LIST_ID = "id";
    private static final String KEY_LIST_TITLE = "title";
    private static final String KEY_LIST_USER_ID_FK = "userId";


    // Tasks Table Columns
    private static final String KEY_TASK_ID = "id";
    private static final String KEY_TASK_LIST_ID_FK = "listId";
    private static final String KEY_TASK_CONTENT = "content";
    private static final String KEY_TASK_ACCOMPLISHED = "accomplished";

    // Activeness Table Columns
    private static final String KEY_ACTIVENESS_ID = "id";
    private static final String KEY_ACTIVENESS_LIST_ID_FK = "listId";
    private static final String KEY_ACTIVENESS_TASK_PER_DAY = "taskPerDay";
    private static final String KEY_ACTIVENESS_TASK_PER_WEEK = "taskPerWeek";
    private static final String KEY_ACTIVENESS_TASK_PER_MONTH = "taskPerMonth";

    // Notification Table Columns
    private static final String KEY_NOTIFICATION_ID = "id";
    private static final String KEY_NOTIFICATION_TASK_ID_FK = "taskId";
    private static final String KEY_NOTIFICATION_DATE = "date";
    private static final String KEY_NOTIFICATION_TIME = "time";

    public static synchronized PostsDatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new PostsDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }


    private PostsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS +
                "(" +
                KEY_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + // Define a primary key
                KEY_USER_NAME + " TEXT," +
                KEY_USER_PASSWORD + " TEXT" +
                ")";

        String CREATE_LISTS_TABLE = "CREATE TABLE " + TABLE_LISTS +
                "(" +
                KEY_LIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + // Define a primary key
                KEY_LIST_TITLE + " TEXT," +
                KEY_LIST_USER_ID_FK + " INTEGER REFERENCES " + TABLE_USERS + // Define a foreign key
                ")";


        String CREATE_ACTIVENESS_TABLE = "CREATE TABLE " + TABLE_ACTIVENESS +
                "(" +
                KEY_ACTIVENESS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + // Define a primary key
                KEY_ACTIVENESS_TASK_PER_DAY + " TEXT," +
                KEY_ACTIVENESS_TASK_PER_WEEK + " TEXT," +
                KEY_ACTIVENESS_TASK_PER_MONTH + " TEXT," +
                KEY_ACTIVENESS_LIST_ID_FK + " INTEGER REFERENCES " + TABLE_LISTS +
                ")";


        String CREATE_NOTIFICATIONS_TABLE = "CREATE TABLE " + TABLE_NOTIFICATIONS +
                "(" +
                KEY_NOTIFICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + // Define a primary key
                KEY_NOTIFICATION_DATE + " TEXT," +
                KEY_NOTIFICATION_TIME + " TEXT," +
                KEY_NOTIFICATION_TASK_ID_FK + " INTEGER, CONSTRAINT fk_Task FOREIGN KEY(" + KEY_NOTIFICATION_TASK_ID_FK +
                " ) REFERENCES " + TABLE_TASKS + "( " + KEY_TASK_ID + ")" + "ON DELETE CASCADE" +
                ")";


        String CREATE_TASKS_TABLE = "CREATE TABLE " + TABLE_TASKS +
                "(" +
                KEY_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + // Define a primary key
                KEY_TASK_CONTENT + " TEXT," +
                KEY_TASK_ACCOMPLISHED + " BOOLEAN," +
                KEY_TASK_LIST_ID_FK + " INTEGER, CONSTRAINT fk_List FOREIGN KEY(" + KEY_TASK_LIST_ID_FK +
                " ) REFERENCES " + TABLE_LISTS + "( " + KEY_LIST_ID + ")" + "ON DELETE CASCADE" +
                ")";

        db.execSQL(CREATE_NOTIFICATIONS_TABLE);
        db.execSQL(CREATE_TASKS_TABLE);
        db.execSQL(CREATE_ACTIVENESS_TABLE);
        db.execSQL(CREATE_LISTS_TABLE);
        db.execSQL(CREATE_USERS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVENESS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_LISTS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

            onCreate(db);
        }
    }

    // Update the user's profile picture url
    public int updateUser(User user) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_NAME, user.username);
        values.put(KEY_USER_PASSWORD, user.password);

        // Updating profile picture url for user with that userName
        return db.update(TABLE_USERS, values, KEY_USER_ID + " = ?",
                new String[]{String.valueOf(user.id)});
    }


    public int deleteUser(User user) {

        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_USERS, KEY_USER_ID + " = ?", new String[]{String.valueOf(user.id)});
    }

    public int addUser(String username, String password) {

        SQLiteDatabase db = getWritableDatabase();
        int userId = -1;

        ContentValues values = new ContentValues();
        values.put(KEY_USER_NAME, username);
        values.put(KEY_USER_PASSWORD, password);

        userId = (int) db.insert(TABLE_USERS, null, values);

        if (userId > 0) {
            return userId;
        } else {
            return -1;
        }
    }

    public int getUserId(String name) {
        SQLiteDatabase database = getReadableDatabase();
        int id = 0;

        Cursor cursor = database.query(
                TABLE_USERS,
                new String[]{KEY_USER_ID},
                KEY_USER_NAME + "=?",
                new String[]{name},
                null,
                null, null
        );

        while (cursor.moveToNext()) {
            id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_USER_ID));
            break;
        }
        return id;

    }

    public int getUsername(String username) {
        SQLiteDatabase database = getReadableDatabase();
        int userId = -1;

        Cursor cursor = database.query(
                TABLE_USERS,
                new String[]{KEY_USER_ID},
                KEY_USER_NAME + "=?",
                new String[]{String.valueOf(username)},
                null,
                null, null
        );

        while (cursor.moveToNext()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_USER_ID));
            break;
        }
        return userId;

    }

    public String getUsername(int id) {
        SQLiteDatabase database = getReadableDatabase();
        String username = null;

        Cursor cursor = database.query(
                TABLE_USERS,
                new String[]{KEY_USER_NAME},
                KEY_USER_ID + "=?",
                new String[]{String.valueOf(id)},
                null,
                null, null
        );

        while (cursor.moveToNext()) {
            username = cursor.getString(cursor.getColumnIndexOrThrow(KEY_USER_NAME));
            break;
        }
        return username;

    }

    public String getPassword(String username) {
        SQLiteDatabase database = getReadableDatabase();
        String password = null;

        Cursor cursor = database.query(
                TABLE_USERS,
                new String[]{KEY_USER_PASSWORD},
                KEY_USER_NAME + "=?",
                new String[]{String.valueOf(username)},
                null,
                null, null
        );

        while (cursor.moveToNext()) {
            password = cursor.getString(cursor.getColumnIndexOrThrow(KEY_USER_PASSWORD));
            break;
        }
        return password;

    }

    public String getPassword(int id) {
        SQLiteDatabase database = getReadableDatabase();
        String password = null;

        Cursor cursor = database.query(
                TABLE_USERS,
                new String[]{KEY_USER_PASSWORD},
                KEY_USER_ID + "=?",
                new String[]{String.valueOf(id)},
                null,
                null, null
        );

        while (cursor.moveToNext()) {
            password = cursor.getString(cursor.getColumnIndexOrThrow(KEY_USER_PASSWORD));
            break;
        }
        return password;

    }


    //Task
    public int createTask(String content, int listId, Boolean accomplished) {
        SQLiteDatabase db = getWritableDatabase();
        int taskId = -1;

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_TASK_CONTENT, content);
            values.put(KEY_TASK_ACCOMPLISHED, accomplished);
            values.put(KEY_TASK_LIST_ID_FK, listId);
            taskId = (int) db.insert(TABLE_TASKS, null, values);

        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add task");
        }

        if (taskId > 0) {
            return taskId;
        } else {
            return -1;
        }
    }

    public Task getTask(int taskId, int listId) {
        SQLiteDatabase database = getReadableDatabase();
        String content;
        Boolean accomplished;
        Task task = null;

        Cursor cursor = database.query(
                TABLE_TASKS,
                new String[]{KEY_TASK_CONTENT, KEY_TASK_ACCOMPLISHED},
                KEY_TASK_ID + "=?",
                new String[]{String.valueOf(taskId)},
                null,
                null, null
        );

        while (cursor.moveToNext()) {
            content = cursor.getString(cursor.getColumnIndexOrThrow(KEY_TASK_CONTENT));
            accomplished = Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TASK_ACCOMPLISHED)));
            task = new Task(taskId, listId, content, accomplished);
            break;
        }

        Notification n = getNotification(taskId);
        task.setNotification(n);

        return task;

    }


    public ArrayList<Task> getAllTasks(int listId) {
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<Task> tasks = new ArrayList<>();

        ContentValues values = new ContentValues();
        values.put(KEY_TASK_LIST_ID_FK, listId);

        String TASKS_SELECT_QUERY = String.format("SELECT * FROM %s WHERE %s = %s",
                TABLE_TASKS,
                KEY_TASK_LIST_ID_FK,
                listId);

        Cursor cursor = db.rawQuery(TASKS_SELECT_QUERY, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    String taskContent = cursor.getString(cursor.getColumnIndexOrThrow(KEY_TASK_CONTENT));
                    int taskId = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_TASK_ID));
                    String taskAccomplished = cursor.getString(cursor.getColumnIndexOrThrow(KEY_TASK_ACCOMPLISHED));
                    Task sample = new Task(taskId, listId, taskContent, Boolean.valueOf(taskAccomplished));
                    //sample.setNotification(getNotification(taskId));
                    tasks.add(sample);

                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        for (int i = 0; i < tasks.size(); i++) {
            Notification m = getNotification(tasks.get(i).getId());
            tasks.get(i).setNotification(m);
        }


        return tasks;
    }

    public void updateTaskContent(String content, int taskId) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_TASK_CONTENT, content);


            db.update(TABLE_TASKS, values, KEY_TASK_ID + " = ?",
                    new String[]{String.valueOf(taskId)});

        } catch (Exception e) {
            Log.d(TAG, "Error while trying to update task content");
        }
    }

    public void accomplishTask(int taskId, boolean a) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_TASK_ACCOMPLISHED, a);


            db.update(TABLE_TASKS, values, KEY_TASK_ID + " = ?",
                    new String[]{String.valueOf(taskId)});

        } catch (Exception e) {
            Log.d(TAG, "Error while trying to accomplish task");
        }
    }

    public Boolean deleteTask(int taskId) {

        SQLiteDatabase db = getWritableDatabase();
        int id = db.delete(TABLE_TASKS, KEY_TASK_ID + " = ?", new String[]{String.valueOf(taskId)});

        if (id > 0) {
            return true;
        } else {
            return false;
        }
    }


    //Notification
    public Notification getNotification(int taskId) {
        SQLiteDatabase db = getWritableDatabase();
        int notificationId = -1;
        String notificationDate = "";
        String notificationTime = "";

        String TASKS_SELECT_QUERY = String.format("SELECT * FROM %s WHERE %s = %s",
                TABLE_NOTIFICATIONS,
                KEY_NOTIFICATION_TASK_ID_FK,
                taskId);

        try {
            Cursor cursor = db.rawQuery(TASKS_SELECT_QUERY, null);

            if (cursor.moveToFirst()) {
                do {
                    notificationId = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_NOTIFICATION_ID));
                    notificationDate = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NOTIFICATION_DATE));
                    notificationTime = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NOTIFICATION_TIME));
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add task");
        }

        return new Notification(notificationId, taskId, notificationDate, notificationTime);
    }

    public int createNotification(Notification notification) {
        SQLiteDatabase db = getWritableDatabase();
        int notificationId = -1;

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_NOTIFICATION_TASK_ID_FK, notification.getTaskId());
            values.put(KEY_NOTIFICATION_DATE, notification.date);
            values.put(KEY_NOTIFICATION_TIME, notification.time);
            notificationId = (int) db.insert(TABLE_NOTIFICATIONS, null, values);
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add notification");
        }

        if (notificationId > 0) {
            return notificationId;
        } else {
            return -1;
        }

    }

    //Notification
    public void updateNotification(String date, String time, int notificationId) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_NOTIFICATION_DATE, date);
            values.put(KEY_NOTIFICATION_TIME, time);

            db.update(TABLE_NOTIFICATIONS, values, KEY_NOTIFICATION_ID + " = ?",
                    new String[]{String.valueOf(notificationId)});
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add task");
        }

    }

    public Boolean deleteNotification(int notificationId) {
        SQLiteDatabase db = getWritableDatabase();
        int id = db.delete(TABLE_NOTIFICATIONS, KEY_NOTIFICATION_ID + " = ?", new String[]{String.valueOf(notificationId)});

        if (id > 0) {
            return true;
        } else {
            return false;
        }
    }


    //Lists
    public int addList(String title, int userId) {
        SQLiteDatabase db = getWritableDatabase();
        int listId = -1;

        ContentValues values = new ContentValues();
        values.put(KEY_LIST_TITLE, title);
        values.put(KEY_LIST_USER_ID_FK, String.valueOf(userId));

        listId = (int) db.insert(TABLE_LISTS, null, values);

        if (listId > 0) {
            return listId;
        } else {
            return -1;
        }
    }


    public String getListName(int listId) {
        SQLiteDatabase database = getReadableDatabase();
        String name = null;

        Cursor cursor = database.query(
                TABLE_LISTS,
                new String[]{KEY_LIST_TITLE},
                KEY_LIST_ID + "=?",
                new String[]{String.valueOf(listId)},
                null,
                null, null
        );

        while (cursor.moveToNext()) {
            name = cursor.getString(cursor.getColumnIndexOrThrow(KEY_LIST_TITLE));
            break;
        }
        return name;

    }

    public void updateList(String title, int listId) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_LIST_TITLE, title);


            db.update(TABLE_LISTS, values, KEY_LIST_ID + " = ?",
                    new String[]{String.valueOf(listId)});

        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add task");
        }
    }

    public ArrayList<Lists> getLists(int userId) {

        ArrayList<Lists> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String POSTS_SELECT_QUERY = String.format("SELECT * FROM %s WHERE %s = %s",
                TABLE_LISTS,
                KEY_LIST_USER_ID_FK,
                userId);

        Cursor cursor = db.rawQuery(POSTS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    String listTitle = cursor.getString(cursor.getColumnIndex(KEY_LIST_TITLE));
                    int listId = cursor.getInt(cursor.getColumnIndex(KEY_LIST_ID));
                    Lists sample = new Lists(listId, listTitle, userId);
                    list.add(sample);

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return list;
    }

    public Boolean deleteList(int listId) {

        SQLiteDatabase db = getWritableDatabase();
        int id = db.delete(TABLE_LISTS, KEY_LIST_ID + " = ?", new String[]{String.valueOf(listId)});
        if (id > 0) {
            return true;
        } else {
            return false;
        }
    }

    //Activeness
    public int createActiveness(long activeness) {
        SQLiteDatabase db = getWritableDatabase();
        int activenessId = -1;

        try {
            ContentValues values = new ContentValues();
            //values.put(KEY_TASK_CONTENT, task.content);
            //values.put(KEY_TASK_ACCOMPLISHED, task.accomplished);
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add activeness");
        }

        return activenessId;
    }

    public Boolean deleteActiveness(int activenessId) {
        SQLiteDatabase db = getWritableDatabase();
        int id = db.delete(TABLE_ACTIVENESS, KEY_ACTIVENESS_ID + " = ?", new String[]{String.valueOf(activenessId)});

        if (id > 0) {
            return true;
        } else {
            return false;
        }
    }

}
