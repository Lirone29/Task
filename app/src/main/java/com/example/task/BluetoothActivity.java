package com.example.task;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.task.models.Task;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

public class BluetoothActivity extends AppCompatActivity {

    private static final String TAG = " BlueToothActivity";
    private static final String TAG_USER_ID = "UserId";
    private static final String TAG_LIST_ID = "ListId";
    private static final String TAG_TASK_ID = "TaskId";
    private static final String TAG_JSON_ARRAY = "JSONArray";
    private static final String APP_NAME = "Task";
    private static final UUID APP_UUID = UUID.fromString("8ce255c0-223a-11e0-ac64-0803450c9a66");

    String jsonArray;
    int userId;
    int listId;

    Button listenButton;
    Button sendButton;
    Button listDevicesButton;
    ListView listView;
    TextView msg_box, status;
    EditText writeMsg;
    Button returnButton;
    Button saveDataButton;

    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice[] bluetoothDevices;
    SendReceive sendReceive;

    static final int CASE_LISTENING = 1;
    static final int CASE_CONNECTING = 2;
    static final int CASE_CONNECTED = 3;
    static final int CASE_CONNECTION_ERROR = 4;
    static final int CASE_MESSAGE_RECEIVED = 5;

    int REQUEST_ENABLE_BLUETOOTH = 1;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case CASE_LISTENING:
                    status.setText("Listening");
                    break;
                case CASE_CONNECTING:
                    status.setText("Connecting");
                    break;
                case CASE_CONNECTED:
                    status.setText("Connected");
                    break;
                case CASE_CONNECTION_ERROR:
                    status.setText("Connection Error");
                    break;
                case CASE_MESSAGE_RECEIVED:
                    byte[] readBuff = (byte[]) msg.obj;
                    String tempMsg = new String(readBuff, 0, msg.arg1);
                    msg_box.setText(tempMsg);
                    break;
            }
            return true;
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        setTitle("MyTasks");

        userId = Integer.valueOf(getIntent().getStringExtra(TAG_USER_ID));
        listId = Integer.valueOf(getIntent().getStringExtra(TAG_LIST_ID));
        jsonArray = getIntent().getStringExtra(TAG_JSON_ARRAY);

        listenButton = (Button) findViewById(R.id.acceptButton);
        sendButton = (Button) findViewById(R.id.sendButton);
        listView = (ListView) findViewById(R.id.pairedDeviceListView);
        msg_box = (TextView) findViewById(R.id.messageTextView);
        status = (TextView) findViewById(R.id.statusLabel);
        writeMsg = (EditText) findViewById(R.id.writeMessageEditText);
        listDevicesButton = (Button) findViewById(R.id.listDevicesButton);
        returnButton = (Button) findViewById(R.id.bluetoothReturnButton);
        saveDataButton = (Button) findViewById(R.id.saveDataButton);

        msg_box.setText(jsonArray);
        writeMsg.setText(jsonArray);

        listDevicesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Set<BluetoothDevice> bluetoothDeviceSet = bluetoothAdapter.getBondedDevices();
                String[] strings = new String[bluetoothDeviceSet.size()];
                bluetoothDevices = new BluetoothDevice[bluetoothDeviceSet.size()];
                int index = 0;

                if (bluetoothDeviceSet.size() > 0) {
                    for (BluetoothDevice device : bluetoothDeviceSet) {
                        bluetoothDevices[index] = device;
                        strings[index] = device.getName();
                        index++;
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, strings);
                    listView.setAdapter(arrayAdapter);
                }
            }
        });

        listenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothServerClass serverClass = new BluetoothServerClass();
                serverClass.start();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BluetoothClientClass clientClass = new BluetoothClientClass(bluetoothDevices[i]);
                clientClass.start();

                status.setText("Connecting");
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String string = String.valueOf(writeMsg.getText());
                sendReceive.write(string.getBytes());
            }
        });

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToTasks();
            }
        });

        saveDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = msg_box.getText().toString();
                /*
                try {
                    JSONObject jsnobject = new JSONObject(text);
                    System.out.println(jsnobject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getBaseContext(), "Parsing array " , Toast.LENGTH_LONG).show();
                */
                try {
                    Toast.makeText(getBaseContext(), "Parsing array ", Toast.LENGTH_LONG).show();
                    String json = msg_box.getText().toString();
                    //JSONObject jsonObject = new JSONObject(msg_box.getText().toString());
                    final ObjectMapper objectMapper = new ObjectMapper();
                    List<Task> langList = objectMapper.readValue(json, new TypeReference<List<Task>>() {
                    });
                    System.out.println(langList);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        });


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
        }

        isBluetoothEnabled();
    }

    public static boolean isBluetoothEnabled() {
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return (bluetoothAdapter != null && bluetoothAdapter.isEnabled());
    }


    private class BluetoothServerClass extends Thread {
        private BluetoothServerSocket serverSocket;

        public BluetoothServerClass() {
            try {
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, APP_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            BluetoothSocket socket = null;

            while (socket == null) {
                try {
                    Message message = Message.obtain();
                    message.what = CASE_CONNECTING;
                    handler.sendMessage(message);

                    socket = serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    Message message = Message.obtain();
                    message.what = CASE_CONNECTION_ERROR;
                    handler.sendMessage(message);
                }

                if (socket != null) {
                    Message message = Message.obtain();
                    message.what = CASE_CONNECTED;
                    handler.sendMessage(message);
                    sendReceive = new SendReceive(socket);
                    sendReceive.start();
                    break;
                }
            }
        }
    }

    private class BluetoothClientClass extends Thread {
        private BluetoothDevice device;
        private BluetoothSocket socket;

        public BluetoothClientClass(BluetoothDevice _device) {
            device = _device;

            try {
                socket = device.createRfcommSocketToServiceRecord(APP_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                socket.connect();
                Message message = Message.obtain();
                message.what = CASE_CONNECTED;
                handler.sendMessage(message);

                sendReceive = new SendReceive(socket);
                sendReceive.start();

            } catch (IOException e) {
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = CASE_CONNECTION_ERROR;
                handler.sendMessage(message);
            }
        }
    }

    public void returnToTasks() {
        Intent intent = new Intent(getApplicationContext(), TaskActivity.class);
        intent.putExtra(TAG_USER_ID, String.valueOf(userId));
        intent.putExtra(TAG_LIST_ID, String.valueOf(listId));
        startActivity(intent);
        finish();
    }


    private class SendReceive extends Thread {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceive(BluetoothSocket _socket) {
            bluetoothSocket = _socket;
            InputStream tempIn = null;
            OutputStream tempOut = null;

            try {
                tempIn = bluetoothSocket.getInputStream();
                tempOut = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            inputStream = tempIn;
            outputStream = tempOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = inputStream.read(buffer);
                    handler.obtainMessage(CASE_MESSAGE_RECEIVED, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
