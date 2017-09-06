package lveapp.fr.contenprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.widget.Toast;

import java.util.HashMap;


public class ContactProvider extends ContentProvider {

    static final String PROVIDER_NAME = "lveapp.fr.contentprovider.ContactProvider";
    static final String CP_CONTACTS = "cpcontacts";
    static final String URL = "content://"+PROVIDER_NAME+"/"+CP_CONTACTS;
    static final Uri CONTENT_URL = Uri.parse(URL);

    static final String id = "id";
    static final String name = "name";
    static final int uriCode = 1;

    private static HashMap<String, String> mValues;

    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, CP_CONTACTS, uriCode);
    }

    private static SQLiteDatabase sqlDB;
    static final String DATABASE_NAME = "myContacts";
    static final String TABLE_NAME = "names";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DB_TABLE = "CREATE TABLE "+TABLE_NAME+
            " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "name TEXT NOT NULL);";

    public ContactProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted = 0;
        switch (uriMatcher.match(uri)){
            case uriCode:
                rowsDeleted = sqlDB.delete(TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("UNKNOWN Uri " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            case uriCode:
                return "vnd.android.cursor.dir/"+CP_CONTACTS;
            default:
                throw new IllegalArgumentException("Unsupported Uri " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID = sqlDB.insert(TABLE_NAME, null, values);
        if(rowID > 0){
            Uri _uri = ContentUris.withAppendedId(CONTENT_URL, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        else{
            Toast.makeText(getContext(), "Row Insert Failed", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @Override
    public boolean onCreate() {
        DataBaseHelper dbHelper = new DataBaseHelper(getContext());
        sqlDB = dbHelper.getWritableDatabase();
        if(sqlDB != null) {
            return true;
        }
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(TABLE_NAME);
        switch (uriMatcher.match(uri)){
            case uriCode:
                queryBuilder.setProjectionMap(mValues);
                break;
            default:
                throw new IllegalArgumentException("UNKNOWN Uri " + uri);
        }
        Cursor cursor = queryBuilder.query(sqlDB, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int rowsUpdate = 0;
        switch (uriMatcher.match(uri)){
            case uriCode:
                rowsUpdate = sqlDB.update(TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("UNKNOWN Uri " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdate;
    }

    private static class DataBaseHelper extends SQLiteOpenHelper{

        public DataBaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
            onCreate(sqLiteDatabase);
        }
    }
}
