package edu.buffalo.cse.cse486586.groupmessenger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * GroupMessengerActivity is the main Activity for the assignment.
 * 
 * @author stevko
 *
 */
public class GroupMessengerActivity extends Activity {

    static final String[] REMOTE_PORT = {
	    	"11108",
	    	"11112",
	        "11116",
		    "11120",
		    "11124"
	    };
    static final String SEQUENCER_PORT = "11108";
    static final int SERVER_PORT = 10000;
    static final String SEQUENCE_REQUEST = "$$";
    private static final String KEY_FIELD = "key";
    private static final String VALUE_FIELD = "value";
    static int SEQ_NO = 0;
    static Uri mUri;
    Boolean isSequencer;
    
    
    static final String TAG = GroupMessengerActivity.class.getSimpleName();
    /**
     * buildUri() demonstrates how to build a URI for a ContentProvider.
     * 
     * @param scheme
     * @param authority
     * @return the URI
     */
    private Uri buildUri(String scheme, String authority) {
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.authority(authority);
        uriBuilder.scheme(scheme);
        return uriBuilder.build();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_messenger);

        /*
         * TODO: Use the TextView to display your messages. Though there is no grading component
         * on how you display the messages, if you implement it, it'll make your debugging easier.
         */
        TextView tv = (TextView) findViewById(R.id.textView1);
        tv.setMovementMethod(new ScrollingMovementMethod());
        
        /*
         * Registers OnPTestClickListener for "button1" in the layout, which is the "PTest" button.
         * OnPTestClickListener demonstrates how to access a ContentProvider.
         */
        findViewById(R.id.button1).setOnClickListener(
                new OnPTestClickListener(tv, getContentResolver()));
        
        TelephonyManager tel = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        final String myPort = String.valueOf((Integer.parseInt(portStr) * 2));
        if (myPort.compareTo(SEQUENCER_PORT) == 0)
        	isSequencer = true;
        else
        	isSequencer = false;
        
        try {
            /*
             * Create a server socket as well as a thread (AsyncTask) that listens on the server
             * port.
             * 
             * AsyncTask is a simplified thread construct that Android provides. Please make sure
             * you know how it works by reading
             * http://developer.android.com/reference/android/os/AsyncTask.html
             */
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);
        } catch (IOException e) {
            /*
             * Log is a good way to debug your code. LogCat prints out all the messages that
             * Log class writes.
             * 
             * Please read http://developer.android.com/tools/debugging/debugging-projects.html
             * and http://developer.android.com/tools/debugging/debugging-log.html
             * for more information on debugging.
             */
            Log.e(TAG, "Can't create a ServerSocket");
            return;
        }
        

        /*
         * Retrieve a pointer to the input box (EditText) defined in the layout
         * XML file (res/layout/main.xml).
         * 
         * This is another example of R class variables. R.id.edit_text refers to the EditText UI
         * element declared in res/layout/main.xml. The id of "edit_text" is given in that file by
         * the use of "android:id="@+id/edit_text""
         */
        final EditText editText = (EditText) findViewById(R.id.editText1);
        
        /*
         * TODO: You need to register and implement an OnClickListener for the "Send" button.
         * In your implementation you need to get the message from the input box (EditText)
         * and send it to other AVDs in a total-causal order.
         */
        findViewById(R.id.button4).setOnClickListener( new View.OnClickListener()
        {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
           
                    String msg = editText.getText().toString() + "\n";
                    editText.setText(""); // This is one way to reset the input box.
               
                    
                    /*
                     * Note that the following AsyncTask uses AsyncTask.SERIAL_EXECUTOR, not
                     * AsyncTask.THREAD_POOL_EXECUTOR as the above ServerTask does. To understand
                     * the difference, please take a look at
                     * http://developer.android.com/reference/android/os/AsyncTask.html
                     */
                    new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, msg, myPort);
                }
			});
        
               
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_group_messenger, menu);
        return true;
    }
    private class ServerTask extends AsyncTask<ServerSocket, String, Void> {

        @Override
        protected Void doInBackground(ServerSocket... sockets) {
            ServerSocket serverSocket = sockets[0];
            while (true)
            {
		        try {
					Socket socket = serverSocket.accept();
					BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					String msg = reader.readLine();
					reader.close();
					socket.close();
					if(msg.indexOf(SEQUENCE_REQUEST) == 0 && isSequencer)
					{
		            	//Multicast
		                for (int i = 0; i < REMOTE_PORT.length; i++)
		                {
		                	String remotePort = REMOTE_PORT[i];
			
			                 socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
			                        Integer.parseInt(remotePort));
			                
			                try {
			    				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			    				writer.write(msg.substring(2));
			    				writer.flush();
			    				writer.close();
			    			} catch (IOException e) {
			    				// TODO Auto-generated catch block
			    				e.printStackTrace();
			    			}
			                /*
			                 * TODO: Fill in your client code that sends out a message.
			                 */
			                socket.close();
		                }

					}
					else
					{
						ContentResolver mContentResolver = getContentResolver();
				        mUri = buildUri("content", "edu.buffalo.cse.cse486586.groupmessenger.provider");
				        ContentValues cv = new ContentValues();
			            cv.put(KEY_FIELD, SEQ_NO + "");
			            cv.put(VALUE_FIELD, msg);
				        mContentResolver.insert(mUri,  cv);
				        
				        publishProgress(msg + ", " + SEQ_NO);
				        SEQ_NO = SEQ_NO + 1;
					}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e("Servererror", e.getMessage());
				}
		        catch(Exception e) {
		        	e.printStackTrace();
		        	Log.e("error", e.getMessage());
		        }
            }
            /*
             * TODO: Fill in your server code that receives messages and passes them
             * to onProgressUpdate().
             */
            //return null;
        }

        protected void onProgressUpdate(String...strings) {
            /*
             * The following code displays what is received in doInBackground().
             */
            String strReceived = strings[0].trim();
            TextView textView = (TextView) findViewById(R.id.textView1);
            textView.append(strReceived + "\n");
            
            return;
        }
    }
    private class ClientTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... msgs) {
            try {
	            	//Get Sequence
	            	Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
	                        Integer.parseInt(SEQUENCER_PORT));

	            	try {
	            			
	    				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	    				writer.write(SEQUENCE_REQUEST + msgs[0]);
	    				writer.flush();
	    				writer.close();
						
	            		
	    			} catch (IOException e) {
	    				// TODO Auto-generated catch block
	    				e.printStackTrace();
	    				Log.e("Clienterror", e.getMessage());
	    			}
	                /*
	                 * TODO: Fill in your client code that sends out a message.
	                 */
	                socket.close();
            } catch (UnknownHostException e) {
                Log.e(TAG, "ClientTask UnknownHostException");
            } catch (IOException e) {
                Log.e(TAG, "ClientTask socket IOException");
            }

            return null;
        }
    }
}
