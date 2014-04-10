package edu.buffalo.cse.cse486586.groupmessenger;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

/**
 * GroupMessengerProvider is a key-value table. Once again, please note that we do not implement
 * full support for SQL as a usual ContentProvider does. We re-purpose ContentProvider's interface
 * to use it as a key-value table.
 * 
 * Please read:
 * 
 * http://developer.android.com/guide/topics/providers/content-providers.html
 * http://developer.android.com/reference/android/content/ContentProvider.html
 * 
 * before you start to get yourself familiarized with ContentProvider.
 * 
 * There are two methods you need to implement---insert() and query(). Others are optional and
 * will not be tested.
 * 
 * @author stevko
 *
 */
public class GroupMessengerProvider extends ContentProvider {
	private MainDatabaseHelper mOpenHelper;
	private static final String DBNAME = "mydb.db";
	private static final String TABLENAME = "main";
    private static final String KEY_FIELD = "key";
    private static final String VALUE_FIELD = "value";

	private static final String SQL_CREATE_MAIN = "CREATE TABLE " +
		    TABLENAME +                       // Table's name
		    "(" +                           // The columns in the table
		    " " + KEY_FIELD + " STRING, " +
		    " " + VALUE_FIELD + " STRING )";
	
	protected static final class MainDatabaseHelper extends SQLiteOpenHelper {

	    /*
	     * Instantiates an open helper for the provider's SQLite data repository
	     * Do not do database creation and upgrade here.
	     */
	    MainDatabaseHelper(Context context) {
	        super(context, DBNAME, null, 1);
	    }
	    
	    public MainDatabaseHelper(Context context, String DBNAME, Cursor cursor, int version) {
	    	super(context, DBNAME, null, 1);
		}
	    /*
	     * Creates the data repository. This is called when the provider attempts to open the
	     * repository and SQLite reports that it doesn't exist.
	     */
	    public void onCreate(SQLiteDatabase db) {

	        // Creates the main table
	        db.execSQL(SQL_CREATE_MAIN);
	    }

		@Override
		public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}
	}

	SQLiteDatabase db = null;
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // You do not need to implement this.
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // You do not need to implement this.
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        /*
         * TODO: You need to implement this method. Note that values will have two columns (a key
         * column and a value column) and one row that contains the actual (key, value) pair to be
         * inserted.
         * 
         * For actual storage, you can use any option. If you know how to use SQL, then you can use
         * SQLite. But this is not a requirement. You can use other storage options, such as the
         * internal storage option that I used in PA1. If you want to use that option, please
         * take a look at the code for PA1.
         */
    	
    	db.insert(TABLENAME, "", values);
        Log.d("insert", values.toString());
        return uri;
    }

    @Override
    public boolean onCreate() {
        // If you need to perform any one-time initialization task, please do it here.
    	 mOpenHelper = new MainDatabaseHelper(
    	            getContext(),        // the application context
    	            DBNAME,              // the name of the database)
    	            null,                // uses the default SQLite cursor
    	            1                    // the version number
    	        );
    	 db = mOpenHelper.getWritableDatabase();
         return (db == null)? false:true;      
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        /*
         * TODO: You need to implement this method. Note that you need to return a Cursor object
         * with the right format. If the formatting is not correct, then it is not going to work.
         * 
         * If you use SQLite, whatever is returned from SQLite is a Cursor object. However, you
         * still need to be careful because the formatting might still be incorrect.
         * 
         * If you use a file storage option, then it is your job to build a Cursor * object. I
         * recommend building a MatrixCursor described at:
         * http://developer.android.com/reference/android/database/MatrixCursor.html
         */
    	SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLENAME);
        
        qb.appendWhere( KEY_FIELD + " = '" + selection + "'");
        Cursor c = null;
        try
        {
	         c = qb.query(db,	null,	null, null, 
	                            null, null, sortOrder);
        }
        catch (Exception e)
        {
        	Log.e("error", e.getMessage());
        }
        Log.d("query", selection);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // You do not need to implement this.
        return 0;
    }
}
