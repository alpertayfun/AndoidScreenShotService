/**
 * @author alpertayfun
 *
 */

package com.example.listening;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.nio.charset.Charset;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.microedition.khronos.opengles.GL10;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.WindowManager;

@SuppressLint({ "WorldReadableFiles", "SimpleDateFormat" })
@SuppressWarnings("unused")
public class ScreenShotService extends Service {

	private final String firm_name = "ASD";
	private final String TAG = firm_name + " ScreenShot Listen Service";
	private final int transmissionPort = 34321;
	private final int transmissionDPort = 34322;
	private final int PanelRefreshPeriod = 600000;
 	private final int exPanelRefreshPeriod = 1;
 	
 	public static String SS_listener_status = "OFF";

 	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		Log.d(TAG, "ScreenShot Service onCreate");
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "ScreenShot Service onDestroy");
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		Log.d(TAG, "ScreenShot Service onStart");
		
    	String client_ip = "192.168.2.1";
    	/*
        final Handler handlers = new Handler(); 
        Timer tt = new Timer(); 
        tt.schedule(new TimerTask() { 
                public void run() { 
                		handlers.post(new Runnable() { 
                                public void run() { 
                                	WLEStarter();
                                } 
                        }); 
                } 
        }, 0,PanelRefreshPeriod);
        */
	}

	
	public void WLEStarter() {
		SS_listener_status = "ON";
		
			ScreenShot();
			
			Log.d(TAG,"ScreenShot Runtime Exec completed");
			SS_listener_status = "OFF";
		                
	}
	
	public void ScreenShot()
	{
	    Process sh;
		try {
			String filepath = "" + this.getApplicationContext().getFilesDir();
			
			sh = Runtime.getRuntime().exec("su", null,null);
			OutputStream  os = sh.getOutputStream();
        	Calendar c = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
            String formattedDate = df.format(c.getTime());
            
            os.write(("/system/bin/screencap -p " + filepath + "/files/ss_" + formattedDate + ".png").getBytes("ASCII"));
            os.flush();
            os.close();
            sh.waitFor();
            
            try {
 
                FileOutputStream fos = openFileOutput(filepath + "/files/ss_" + formattedDate + ".jpg", Context.MODE_PRIVATE);
                
                BitmapFactory.Options options=new BitmapFactory.Options();
                InputStream in=openFileInput(filepath + "/files/ss_" + formattedDate + ".png");
                options.inSampleSize=2;
                options.inPurgeable=true;
                Bitmap bitmap=BitmapFactory.decodeStream(in, null, options);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 30, fos);
                fos.close();
                
         } catch (Exception e) {
                e.printStackTrace();
         }
            
            Runtime.getRuntime().exec("su", null,null);
            Runtime.getRuntime().exec("rm -rf " + filepath + "/files/ss_" + formattedDate + ".png");
            
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public boolean saveImageToInternalStorage(Bitmap image,String name) {

		try {

		FileOutputStream fos = openFileOutput(name, Context.MODE_PRIVATE);

		// Writing the bitmap to the output stream
		image.compress(Bitmap.CompressFormat.PNG, 100, fos);
		fos.close();

		return true;
		} catch (Exception e) {
		Log.e("saveToInternalStorage()", e.getMessage());
		return false;
		}
		
	}
         
	public String Client_Info_Read(String filename) {
		String xmlread_d = "";
		try {
		      InputStream in=openFileInput(filename);

		        Reader read = new InputStreamReader(in);
		        StringWriter write = new StringWriter();

		        int c = -1;
		        while ((c = read.read()) != -1)
		        {
		            write.write(c);
		        }
		        write.flush();
		        xmlread_d = write.toString();
	
		    }
		    catch (java.io.FileNotFoundException e) {
		    	Log.e(TAG,e.getMessage());
		    } catch (IOException e) {
		    	Log.e(TAG,e.getMessage());
			}
		return xmlread_d;
	
	}
		private static boolean CheckPort(String host,int port) {
				
			boolean isAvailable = false;
			try {
			    isAvailable = InetAddress.getByName(host).isReachable(port);
			    if (isAvailable == true) {
			    	isAvailable = true;
			    }else {
			    	isAvailable = false;
			    }
			} catch (Exception e) {

			}
			
			return isAvailable;
			    
		}
}