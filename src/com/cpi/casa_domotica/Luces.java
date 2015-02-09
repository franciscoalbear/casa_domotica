package com.cpi.casa_domotica;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Luces extends Activity{
	private static final String TAG = "bluetooth1";
	
	private BluetoothAdapter btAdapter = null;
	private BluetoothSocket btSocket = null;
	private OutputStream outStream = null;
	
	// SPP UUID service 
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    
    private static String address = "20:14:04:15:23:21";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.luces);
		Button encendersala = (Button)findViewById(R.id.encendersala);
		Button apagarsala = (Button)findViewById(R.id.apagarprincipal);
		
		Button encendercochera = (Button)findViewById(R.id.encendercochera);
		Button apagarcochera = (Button)findViewById(R.id.apagarcochera);
		
		Button encenderhabit1 = (Button)findViewById(R.id.encenderhabit1);
		Button apagarhabit1 = (Button)findViewById(R.id.apagarhabit1);
		
		Button encenderhabit2 = (Button)findViewById(R.id.encenderhabit2);
		Button apagarhabit2 = (Button)findViewById(R.id.apagarhabit2);
		
		btAdapter = BluetoothAdapter.getDefaultAdapter();
	    checkBTState();
	    
	    encendersala.setOnClickListener(ejecutar);
	    apagarsala.setOnClickListener(ejecutar);
	    
	    encendercochera.setOnClickListener(ejecutar);
	    apagarcochera.setOnClickListener(ejecutar);
	    
	    encenderhabit1.setOnClickListener(ejecutar);
	    apagarhabit1.setOnClickListener(ejecutar);
	    
	    encenderhabit2.setOnClickListener(ejecutar);
	    apagarhabit2.setOnClickListener(ejecutar);
		
		
	}
	
	private OnClickListener ejecutar = new OnClickListener(){
		public void onClick(View view){
			switch(view.getId()){
			case R.id.encendersala:
				sendData("I");
				break;
			case R.id.apagarprincipal:
				sendData("J");
				break;
			case R.id.encendercochera:
				sendData("K");
				break;
			case R.id.apagarcochera:
				sendData("L");
				break;
			case R.id.encenderhabit1:
				sendData("M");
				break;
			case R.id.apagarhabit1:
				sendData("N");
				break;
			case R.id.encenderhabit2:
				sendData("O");
				break;
			case R.id.apagarhabit2:
				sendData("P");
				break;
			}
		}
	};
	
	private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
	      if(Build.VERSION.SDK_INT >= 10){
	          try {
	              final Method  m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
	              return (BluetoothSocket) m.invoke(device, MY_UUID);
	          } catch (Exception e) {
	              Log.e(TAG, "Could not create Insecure RFComm Connection",e);
	          }
	      }
	      return  device.createRfcommSocketToServiceRecord(MY_UUID);
	  }
	
	 @Override
	  public void onResume() {
	    super.onResume();
	  
	    Log.d(TAG, "...onResume - try connect...");
	    
	    // Set up a pointer to the remote node using it's address.
	    BluetoothDevice device = btAdapter.getRemoteDevice(address);
	    
	    // Two things are needed to make a connection:
	    //   A MAC address, which we got above.
	    //   A Service ID or UUID.  In this case we are using the
	    //     UUID for SPP.
	    
	    try {
	        btSocket = createBluetoothSocket(device);
	    } catch (IOException e1) {
	        errorExit("Fatal Error", "In onResume() and socket create failed: " + e1.getMessage() + ".");
	    }
	        
	    // Discovery is resource intensive.  Make sure it isn't going on
	    // when you attempt to connect and pass your message.
	    btAdapter.cancelDiscovery();
	    
	    // Establish the connection.  This will block until it connects.
	    Log.d(TAG, "...Connecting...");
	    try {
	      btSocket.connect();
	      Log.d(TAG, "...Connection ok...");
	    } catch (IOException e) {
	      try {
	        btSocket.close();
	      } catch (IOException e2) {
	        errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
	      }
	    }
	      
	    // Create a data stream so we can talk to server.
	    Log.d(TAG, "...Create Socket...");
	  
	    try {
	      outStream = btSocket.getOutputStream();
	    } catch (IOException e) {
	      errorExit("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
	    }
	  }
	 
	 @Override
	  public void onPause() {
	    super.onPause();
	  
	    Log.d(TAG, "...In onPause()...");
	  
	    if (outStream != null) {
	      try {
	        outStream.flush();
	      } catch (IOException e) {
	        errorExit("Fatal Error", "In onPause() and failed to flush output stream: " + e.getMessage() + ".");
	      }
	    }
	  
	    try     {
	      btSocket.close();
	    } catch (IOException e2) {
	      errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
	    }
	  }
	 
	 private void checkBTState() {
		    // Check for Bluetooth support and then check to make sure it is turned on
		    // Emulator doesn't support Bluetooth and will return null
		    if(btAdapter==null) { 
		      errorExit("Fatal Error", "Bluetooth not support");
		    } else {
		      if (btAdapter.isEnabled()) {
		        Log.d(TAG, "...Bluetooth ON...");
		      } else {
		        //Prompt user to turn on Bluetooth
		        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		        startActivityForResult(enableBtIntent, 1);
		      }
		    }
		  }
	 
	 private void errorExit(String title, String message){
		    Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
		    finish();
		  }
	
	 private void sendData(String message) {
		    byte[] msgBuffer = message.getBytes();
		  
		    Log.d(TAG, "...Send data: " + message + "...");
		  
		    try {
		      outStream.write(msgBuffer);
		    } catch (IOException e) {
		      String msg = "In onResume() and an exception occurred during write: " + e.getMessage();
		      if (address.equals("00:00:00:00:00:00")) 
		        msg = msg + ".\n\nUpdate your server address from 00:00:00:00:00:00 to the correct address on line 35 in the java code";
		        msg = msg +  ".\n\nCheck that the SPP UUID: " + MY_UUID.toString() + " exists on server.\n\n";
		        
		        errorExit("Fatal Error", msg);       
		    }
		  }
	 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


}
