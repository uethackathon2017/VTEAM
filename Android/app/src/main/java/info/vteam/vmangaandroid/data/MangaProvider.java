package info.vteam.vmangaandroid.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by YukiNoHara on 3/10/2017.
 */

public class MangaProvider extends ContentProvider {
    public static final int CODE_MANGA = 100;
    public static final int CODE_MANGAINFO = 200;
    public static final int CODE_MANGA_WITH_ID = 101;
    public static final int CODE_MANGAINFO_WITH_ID = 201;

    MangaDbHelper mOpenHelper;

    public static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MangaContract.AUTHORITY, MangaContract.PATH_MANGA, CODE_MANGA);
        uriMatcher.addURI(MangaContract.AUTHORITY, MangaContract.PATH_MANGA + "/#", CODE_MANGA_WITH_ID);
        uriMatcher.addURI(MangaContract.AUTHORITY, MangaContract.PATH_MANGA_INFO, CODE_MANGAINFO);
        uriMatcher.addURI(MangaContract.AUTHORITY, MangaContract.PATH_MANGA_INFO + "#", CODE_MANGAINFO_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MangaDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);

        switch (match){
            case CODE_MANGA:
                cursor = db.query(MangaContract.MangaEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_MANGAINFO:
                cursor = db.query(MangaContract.MangaInfoEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_MANGA_WITH_ID:
                String idManga = uri.getLastPathSegment();
                String[] mSelectionArgsManga = new String[]{idManga};
                cursor = db.query(MangaContract.MangaEntry.TABLE_NAME,
                        projection,
                        MangaContract.MangaEntry._ID + " =? ",
                        mSelectionArgsManga,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_MANGAINFO_WITH_ID:
                String idMangaInfo = uri.getLastPathSegment();
                String[] mSelectionArgsMangaInfo = new String[]{idMangaInfo};
                cursor = db.query(MangaContract.MangaEntry.TABLE_NAME,
                        projection,
                        MangaContract.MangaEntry._ID + " =? ",
                        mSelectionArgsMangaInfo,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        Uri returnUri = null;
        switch (match){
            case CODE_MANGA:
                long _id = db.insert(MangaContract.MangaEntry.TABLE_NAME, null, values);
                if (_id > 0){
                    returnUri = ContentUris.withAppendedId(MangaContract.MangaEntry.CONTENT_URI,_id);
                }
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        switch (match){
            case CODE_MANGA:
                db.beginTransaction();
                int rowMangaInserted = 0;
                try{
                    for (ContentValues cv : values){
                        long _id_manga = db.insert(MangaContract.MangaEntry.TABLE_NAME, null, cv);
                        if (_id_manga != -1) rowMangaInserted++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                if (rowMangaInserted > 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowMangaInserted;

            case CODE_MANGAINFO:
                db.beginTransaction();
                int rowMangaInfoInserted = 0;
                try {
                    for (ContentValues cv : values){
                        long _id_manga_info = db.insert(MangaContract.MangaInfoEntry.TABLE_NAME, null, cv);
                        if (_id_manga_info != -1) rowMangaInfoInserted++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                if (rowMangaInfoInserted > 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowMangaInfoInserted;

            default:
                return super.bulkInsert(uri, values);

        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowDeleted;
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        if (selection == null) selection = "1";

        switch (match){
            case CODE_MANGA:
                rowDeleted = db.delete(MangaContract.MangaEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;

            case CODE_MANGAINFO:
                rowDeleted = db.delete(MangaContract.MangaInfoEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            default:throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowDeleted > 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return -1;
    }
}
