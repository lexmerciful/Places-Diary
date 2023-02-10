package com.lex.placesdiary.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.lex.placesdiary.models.PlacesDiaryModel

class DatabaseHelper(context: Context):SQLiteOpenHelper(context,DATABASE_NAME, null, DATABASE_VERSION) {

    companion object{
        private const val DATABASE_NAME = "PLACES_DIARY_DATABASE"
        private const val DATABASE_VERSION = 1
        private const val TABLE_PLACES_DIARY = "PLACES_DIARY_TABLE"

        //All the column entries
        private val KEY_ID = "id"
        private val KEY_TITLE = "title"
        private val KEY_DESCRIPTION = "description"
        private val KEY_IMAGE = "image"
        private val KEY_DATE = "date"
        private val KEY_LOCATION = "location"
        private val KEY_LATITUDE = "latitude"
        private val KEY_LONGITUDE = "longitude"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        // below is the sqlite query, where column names
        // along with their data types is given
        val createTable = ("CREATE TABLE " + TABLE_PLACES_DIARY + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_IMAGE + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_LOCATION + " TEXT,"
                + KEY_LATITUDE + " TEXT,"
                + KEY_LONGITUDE + " TEXT" + ")")
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        // this method checks if table already exists
       db!!.execSQL("DROP TABLE IF EXISTS $TABLE_PLACES_DIARY")
        onCreate(db)
    }

    fun addPlaceDiary(placesDiaryModel: PlacesDiaryModel):Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_TITLE, placesDiaryModel.title)
        contentValues.put(KEY_DESCRIPTION, placesDiaryModel.description)
        contentValues.put(KEY_IMAGE, placesDiaryModel.image)
        contentValues.put(KEY_DATE, placesDiaryModel.date)
        contentValues.put(KEY_LOCATION, placesDiaryModel.location)
        contentValues.put(KEY_LATITUDE, placesDiaryModel.latitude)
        contentValues.put(KEY_LONGITUDE, placesDiaryModel.longitude)

        // Inserting Row
        val success = db.insert(TABLE_PLACES_DIARY, null, contentValues)

        db.close()
        return success
    }

    fun updatePlaceDiary(placesDiaryModel: PlacesDiaryModel): Int {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_TITLE, placesDiaryModel.title)
        contentValues.put(KEY_DESCRIPTION, placesDiaryModel.description)
        contentValues.put(KEY_IMAGE, placesDiaryModel.image)
        contentValues.put(KEY_DATE, placesDiaryModel.date)
        contentValues.put(KEY_LOCATION, placesDiaryModel.location)
        contentValues.put(KEY_LATITUDE, placesDiaryModel.latitude)
        contentValues.put(KEY_LONGITUDE, placesDiaryModel.longitude)

        // Updating Row
        val success = db.update(TABLE_PLACES_DIARY, contentValues,
            KEY_ID + "=" + placesDiaryModel.id, null)

        db.close()
        return success
    }

    fun deletePlaceDiary(placesDiaryModel: PlacesDiaryModel): Int {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_ID, placesDiaryModel.id)

        // Deleting Row
        val success = db.delete(TABLE_PLACES_DIARY,
            KEY_ID + "=" + placesDiaryModel.id, null)

        db.close()
        return success
    }

    fun getPlaceDiaryList():ArrayList<PlacesDiaryModel>{
        val placesDiaryList:ArrayList<PlacesDiaryModel> = ArrayList<PlacesDiaryModel>()
        val selectQuery = "SELECT * FROM $TABLE_PLACES_DIARY"
        val db = this.readableDatabase
        var cursor : Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery, null)

            if (cursor.moveToFirst()){
                do {
                    val place = PlacesDiaryModel(
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_IMAGE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_LOCATION)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_LONGITUDE))
                    )
                    placesDiaryList.add(place)

                }while (cursor.moveToNext())
            }
            cursor.close()
        }catch (e:SQLException){
            db.execSQL(selectQuery)
            return ArrayList()
        }

        return placesDiaryList
    }
}