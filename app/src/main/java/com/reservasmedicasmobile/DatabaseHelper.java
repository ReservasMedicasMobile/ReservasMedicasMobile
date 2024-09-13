package com.reservasmedicasmobile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "contacts.db"; // Nombre de la base de datos
    private static final int DATABASE_VERSION = 1; // Versión de la base de datos

    private static final String TABLE_NAME = "contacts"; // Nombre de la tabla
    private static final String COLUMN_ID = "id"; // Columna ID
    private static final String COLUMN_FIRST_NAME = "first_name"; // Columna Nombre
    private static final String COLUMN_LAST_NAME = "last_name"; // Columna Apellido
    private static final String COLUMN_EMAIL = "email"; // Columna Email
    private static final String COLUMN_PHONE_NUMBER = "phone_number"; // Columna Teléfono
    private static final String COLUMN_MESSAGE = "message"; // Columna Mensaje

    // Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_FIRST_NAME + " TEXT NOT NULL, " +
                COLUMN_LAST_NAME + " TEXT NOT NULL, " +
                COLUMN_EMAIL + " TEXT NOT NULL, " +
                COLUMN_PHONE_NUMBER + " TEXT NOT NULL, " +
                COLUMN_MESSAGE + " TEXT NOT NULL)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

    // Método para añadir contacto
    public long addContact(String firstName, String lastName, String email, String phoneNumber, String message) {
        SQLiteDatabase db = null;
        long result = -1;

        try {
            db = this.getWritableDatabase(); // Abre la base de datos
            ContentValues values = new ContentValues();
            values.put(COLUMN_FIRST_NAME, firstName);
            values.put(COLUMN_LAST_NAME, lastName);
            values.put(COLUMN_EMAIL, email);
            values.put(COLUMN_PHONE_NUMBER, phoneNumber);
            values.put(COLUMN_MESSAGE, message);

            // Inserta los datos
            result = db.insert(TABLE_NAME, null, values);
        } catch (SQLException e) {
            e.printStackTrace(); // Imprime el error en el log
        } finally {
            if (db != null && db.isOpen()) {
                db.close(); // Asegúrate de cerrar la base de datos aquí
            }
        }
        return result;
    }

    // Método para obtener todos los contactos
    public Cursor getAllContacts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                TABLE_NAME, // Nombre de la tabla
                null,       // Selección de columnas (null significa todas)
                null,       // WHERE
                null,       // Argumentos del WHERE
                null,       // GroupBy
                null,       // Having
                COLUMN_ID + " DESC" // Ordenar por ID descendente
        );
    }

    // Método para cerrar el cursor de manera segura
    public void closeCursor(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }
}



