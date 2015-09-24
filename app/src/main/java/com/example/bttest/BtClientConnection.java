package com.example.bttest;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by 啓介 on 2015/09/24.
 */
public class BtClientConnection extends Thread{
    private final BluetoothSocket mSocket;
    private final BluetoothDevice mDevice;

    private final InputStream mInput;
    private final OutputStream mOutput;

    private TextView mTextView;

    private enum State{CONNECT, CONNECTED, DISCONNECT}
    private State mState;

    public BtClientConnection(BluetoothDevice device, TextView textView){
        mDevice = device;
        BluetoothSocket socket = null;
        InputStream in = null;
        OutputStream out = null;

        try{
            socket = mDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            in = socket.getInputStream();
            out = socket.getOutputStream();
        }catch (Exception e){
            e.printStackTrace();
        }

        mSocket = socket;
        mInput = in;
        mOutput = out;
        mState = State.CONNECT;

        mTextView = textView;
        mTextView.setText("Start");
    }

    public void run(){
        Log.d("LOG", "Client Start");

        if(mSocket == null) return;

        while(true){
            switch(mState){
                case CONNECT:
                    try{
                        mSocket.connect();
                    }catch (IOException e){
                        e.printStackTrace();
                        mState = State.DISCONNECT;
                        return;
                    }
                    mState = State.CONNECTED;
                    Log.d("LOG", "Connected!");
                    break;
                case CONNECTED:
                    byte[] buffer = new byte[8];
                    int bytes = 0;
                    try {
                        bytes = mInput.read(buffer);
                        if(bytes != 0){
                            //DataInputStream in = new DataInputStream(new ByteArrayInputStream(buffer));
                            //float val = in.readFloat();
                            //Log.d("LOG", "byte :" + buffer[0] + " " + buffer[1] + " " + buffer[2] + " " + buffer[3]+ " " + buffer[4]+ " " + buffer[5]+ " " + buffer[6] + " " + buffer[7]);
                            Log.d("LOG", "data :" + buffer[0]);
                            mTextView.setText(String.valueOf(buffer[0]));
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    break;
                case DISCONNECT:
                    if(mSocket != null){
                        try {
                            mOutput.write('E');
                            TimeUnit.MILLISECONDS.sleep(100);
                            mSocket.close();
                            Log.d("LOG", "Disconnected");
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                    return;
            }
        }
    }

    public void send(byte sendData[]){
        if(!mState.equals(State.CONNECTED)) return;
        try{
            mOutput.write(sendData);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void disconnect(){
        mState = State.DISCONNECT;
    }
}
