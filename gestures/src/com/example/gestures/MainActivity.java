package com.example.gestures;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.view.MotionEvent;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;
public class MainActivity extends Activity implements 
OnGestureListener,OnDoubleTapListener{
	private static final int EXIT_CMD=-1;
	private static final int REQUEST_ENABLE_BT=1;
	private static final int MOUSE_LEFT = 2;
	private static final int MOUSE_RIGHT=3;
	private static final int MOUSE_MOVE=4;
	private static final int KEY_LEFT=5;
	private static final int KEY_RIGHT=6;
	private static final int SCROLL_ON=7;
	private static final int SCROLL_OFF=8;
	private static final int LEFT_PRESS=9;
	private static final int LEFT_RELEASE=10;
	private BluetoothAdapter mBluetoothAdapter; //umo�liwia dost�p do modu�u bluetooth urz�dzenia
	// unikalny identyfikator dzi�ki kt�remu aplikacja na androida po��czy si� z  serwerem
	private static final UUID myUUID=UUID.fromString("04c6093b-0000-1000-8000-00805f9b34fb");
	 // Adres MAC urz�dzenia bluetooth na komputerze
	private static String MACAddress = "";
	//Reprezetuje zdalne urz�dzenie bluetooth (do kt�rego si� chcemy po��czy�)
	private BluetoothDevice device;
	//gniazdo przez kt�re b�d� przesy�ane dane do serwera
	private BluetoothSocket bluetoothSocket;
	//strumien wyj�ciowy do kt�rego b�d� pakowane dane
	private OutputStream outStream = null;
	private GestureDetectorCompat mDetector; 
	private Button leftButton;
	private Button rightButton;
	private ToggleButton scrollButton;
	private ToggleButton leftDragButton;
	private Integer x1=0;
	private Integer x2=0;
// Called when the activity is first created. 
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		leftButton=(Button)this.findViewById(R.id.left);
		rightButton=(Button)this.findViewById(R.id.right);
		scrollButton=(ToggleButton)this.findViewById(R.id.scrollbutton);
		leftDragButton=(ToggleButton)this.findViewById(R.id.leftDragButton);
		leftButton.setOnClickListener(new OnClickListener(){
			public void onClick(View view)
			{
				new BluetoothThread().execute(MOUSE_LEFT);
			}
		});
		rightButton.setOnClickListener(new OnClickListener(){
			public void onClick(View view)
			{
				new BluetoothThread().execute(MOUSE_RIGHT);
			}
		});
		mDetector = new GestureDetectorCompat(this,this);

		mDetector.setOnDoubleTapListener(this);
		mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
		//w przypadku gdy urz�dzenie nie posiada modu�u bluetooth
		if(mBluetoothAdapter==null)
		{
			this.finish();//zako�cz dzia�anie 
		}
		//sprawdzanie czy bluetooth jest w��czony
		if(!mBluetoothAdapter.isEnabled())
		{
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent,REQUEST_ENABLE_BT);
		}
		device = mBluetoothAdapter.getRemoteDevice(MACAddress);
		try
		{
			bluetoothSocket=device.createRfcommSocketToServiceRecord(myUUID);
			bluetoothSocket.connect();
			outStream=bluetoothSocket.getOutputStream();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		mBluetoothAdapter.cancelDiscovery();
	}
	public void onBackPressed() 
	{
		Toast.makeText(this, "onback", Toast.LENGTH_LONG).show();
		try
		{
			new BluetoothThread().execute(EXIT_CMD);
			bluetoothSocket.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		this.finish();
	}
	private class BluetoothThread extends AsyncTask<Integer,Void,Void>
	{
		@Override
		protected Void doInBackground(Integer... arg0) {
			try
			{
				if(arg0[0]==MOUSE_MOVE)
				{
					outStream.write(arg0[0].byteValue());
					outStream.write(arg0[1].byteValue());
					outStream.write(arg0[2].byteValue());
				}
				else
				{
					outStream.write(arg0[0].byteValue());
			
				}
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			return null;
		}
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
		{
		    try
		    {
		       outStream.write(KEY_LEFT);
		    }
		    catch(IOException e){}
		    return true;
		}
		else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
		{
		    try
		    {
		        outStream.write(KEY_RIGHT);
		    }
		    catch(IOException e){}
		    return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override 
	public boolean onTouchEvent(MotionEvent event)
	{ 
		this.mDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent event)
	{ 
		return true;
	}
	@Override
	public boolean onFling(MotionEvent event1, MotionEvent event2,float velocityX, float velocityY) 
	{
		return true;
	}
	@Override
	public void onLongPress(MotionEvent event) 
	{
		new BluetoothThread().execute(MOUSE_RIGHT);		
	}
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,float distanceY)
	{
		if(distanceX<0)distanceX=Math.abs(distanceX);
		else distanceX=-distanceX;

		if(distanceY<0)distanceY=Math.abs(distanceY);
		else distanceY=-distanceY;
		x1+=(int)distanceX;
		x2+=(int)distanceY;
		new BluetoothThread().execute(MOUSE_MOVE,x1,x2);
		return true;
	}
	@Override
	public void onShowPress(MotionEvent event) 
	{}
	@Override
	public boolean onSingleTapUp(MotionEvent event) 
	{
		return true;
	}
	@Override
	public boolean onDoubleTap(MotionEvent event) 
	{
		new BluetoothThread().execute(MOUSE_LEFT);
		new BluetoothThread().execute(MOUSE_LEFT);
		return true;
	}
	@Override
	public boolean onDoubleTapEvent(MotionEvent event) 
	{
		return true;
	}
	@Override
	public boolean onSingleTapConfirmed(MotionEvent event) 
	{
		new BluetoothThread().execute(MOUSE_LEFT);
		return true;
	}
	public void onToggleClicked(View view) 
	{
	   
	    boolean on = scrollButton.isChecked();
	    
	    if (on)
	    {
	        new BluetoothThread().execute(SCROLL_ON);
	    } 
	    else
	    {
	        new BluetoothThread().execute(SCROLL_OFF);
	    }
	}
	public void onLeftToggleClicked(View view) 
	{
	   
	    boolean on = leftDragButton.isChecked();
	    
	    if (on)
	    {
	        new BluetoothThread().execute(LEFT_PRESS);
	    } 
	    else
	    {
	        new BluetoothThread().execute(LEFT_RELEASE);
	    }
	}
}
