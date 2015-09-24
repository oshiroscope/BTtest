package com.example.bttest;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private final int REQUEST_ENABLE_BT = 1;

    private BluetoothAdapter bluetoothAdapter;
    private BtClientConnection connection;

    private TextView textView;
    private Handler guiHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView)findViewById(R.id.textView);
        textView.setText("Good bye world!");

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(bluetoothAdapter == null){
            Toast.makeText(this, "Bluetoothがサポートされていません", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //bluetoothが無効になっている時の処理
        if(!bluetoothAdapter.isEnabled()){
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableIntent);
        }else{
            connectServer();
        }

        guiHandler = new Handler();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            //bluetooth有効化
            case REQUEST_ENABLE_BT :
                if (resultCode == Activity.RESULT_OK) {
                    connectServer();
                } else {
                    Toast.makeText(this, "Bluetoothが有効になりませんでした", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void connectServer(){
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        BluetoothDevice selected = null;

        if(pairedDevices.size() > 0){
            for(BluetoothDevice device : pairedDevices){
                Log.d("LOG", "Devices:" + device.getName() + "/" + device.getAddress() + "/" + device.getBondState());
                selected = device;
            }
            connection = new BtClientConnection(selected, textView, guiHandler);
            connection.start();
        }else{
            Toast.makeText(this, "端末がありません", Toast.LENGTH_SHORT).show();
        }
    }

    public void setTextAsync(final String text){
        guiHandler.post(new Runnable() {
            @Override
            public void run() {
                textView.setText(text);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
