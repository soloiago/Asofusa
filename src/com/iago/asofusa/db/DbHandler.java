package com.iago.asofusa.db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHandler {
	public static final String tag = "ASOFUSA";

	private static final String DATABASE_NAME = "ASOFUSABD";
	public static final String DATABASE_CLASIFICACION_TABLE = "CLASIFICACION_TABLE";
	public static final String DATABASE_GOLEADORES_TABLE = "GOLEADORES_TABLE";
	public static final String DATABASE_DEPORTIVIDAD_TABLE = "DEPORTIVIDAD_TABLE";
	public static final String DATABASE_RESULTADOS_TABLE = "RESULTADOS_TABLE";
	public static final String DATABASE_PROXIMA_JORNADA_TABLE = "PROXIMA_JORNADA_TABLE";
	public static final String DATABASE_EQUIPOS_TABLE = "EQUIPOS_TABLE";
	public static final String DATABASE_CALENDARIO_TABLE = "CALENDARIO_TABLE";
	public static final String DATABASE_COMPETICIONES_TABLE = "COMPETICIONES_TABLE";
	private static final int DATABASE_VERSION = 4;

	public static final String KEY_LIGA = "liga";
	public static final String KEY_CALIFICACION = "calificacion";
	public static final String KEY_GOLEADORES = "goleadores";
	public static final String KEY_DEPORTIVIDAD = "deportividad";
	public static final String KEY_RESULTADOS = "resultados";
	public static final String KEY_PROXIMA_JORNADA = "proxima_jornada";
	public static final String KEY_FECHA = "fecha";
	public static final String KEY_EQUIPO = "equipo";
	public static final String KEY_URL_EQUIPO = "url_equipo";
	public static final String KEY_CALENDARIO = "calendario";
	public static final String KEY_URL_CALENDARIO = "url_calendario";
	public static final String KEY_NOMBRE_COMPETICION = "nombre_competicion";
	public static final String KEY_COMPETICION_LINK = "competicion_link";
	public static final String KEY_CALENDARIO_LINK = "calendario_link";
	public static final String KEY_ALERTA = "alerta";
	public static final String KEY_ROWID = "_id";

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final Context mCtx;

	private static final String CREATE_CALIFICACION_TABLE =
			"create table " + DATABASE_CLASIFICACION_TABLE + " ("
					+ KEY_ROWID + " integer primary key autoincrement, "
					+ KEY_CALIFICACION + " text not null, "
					+ KEY_FECHA + " text not null, "
					+ KEY_LIGA + " text not null); ";

	private static final String CREATE_GOLEADORES_TABLE =
			"create table " + DATABASE_GOLEADORES_TABLE + " ("
					+ KEY_ROWID + " integer primary key autoincrement, "
					+ KEY_GOLEADORES + " text not null, "
					+ KEY_FECHA + " text not null, "
					+ KEY_LIGA + " text not null); ";

	private static final String CREATE_DEPORTIVIDAD_TABLE =
			"create table " + DATABASE_DEPORTIVIDAD_TABLE + " ("
					+ KEY_ROWID + " integer primary key autoincrement, "
					+ KEY_DEPORTIVIDAD + " text not null, "
					+ KEY_FECHA + " text not null, "
					+ KEY_LIGA + " text not null); ";

	private static final String CREATE_RESULTADOS_TABLE =
			"create table " + DATABASE_RESULTADOS_TABLE + " ("
					+ KEY_ROWID + " integer primary key autoincrement, "
					+ KEY_RESULTADOS + " text not null, "
					+ KEY_FECHA + " text not null, "
					+ KEY_LIGA + " text not null); ";

	private static final String CREATE_PROXIMA_JORNADA_TABLE =
			"create table " + DATABASE_PROXIMA_JORNADA_TABLE + " ("
					+ KEY_ROWID + " integer primary key autoincrement, "
					+ KEY_PROXIMA_JORNADA + " text not null, "
					+ KEY_FECHA + " text not null, "
					+ KEY_LIGA + " text not null); ";

	private static final String CREATE_EQUIPOS_TABLE =
			"create table " + DATABASE_EQUIPOS_TABLE + " ("
					+ KEY_ROWID + " integer primary key autoincrement, "
					+ KEY_EQUIPO + " text not null, "
					+ KEY_FECHA + " text not null, "
					+ KEY_URL_EQUIPO + " text not null); ";

	private static final String CREATE_CALENDARIO_TABLE =
			"create table " + DATABASE_CALENDARIO_TABLE + " ("
					+ KEY_ROWID + " integer primary key autoincrement, "
					+ KEY_CALENDARIO + " text not null, "
					+ KEY_FECHA + " text not null, "
					+ KEY_URL_CALENDARIO + " text not null); ";

	private static final String CREATE_COMPETICIONES_TABLE =
			"create table " + DATABASE_COMPETICIONES_TABLE + " ("
					+ KEY_ROWID + " integer primary key autoincrement, "
					+ KEY_NOMBRE_COMPETICION + " text not null, "
					+ KEY_ALERTA + " integer default 0, "
					+ KEY_CALENDARIO_LINK + " text not null, "
					+ KEY_COMPETICION_LINK + " text not null); ";


	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			try {
				db.execSQL(CREATE_CALIFICACION_TABLE);
				db.execSQL(CREATE_GOLEADORES_TABLE);
				db.execSQL(CREATE_DEPORTIVIDAD_TABLE);
				db.execSQL(CREATE_RESULTADOS_TABLE);
				db.execSQL(CREATE_PROXIMA_JORNADA_TABLE);
				db.execSQL(CREATE_EQUIPOS_TABLE);
				db.execSQL(CREATE_CALENDARIO_TABLE);
				db.execSQL(CREATE_COMPETICIONES_TABLE);

				db.execSQL("insert into competiciones_table (\"_id\", \"nombre_competicion\", \"calendario_link\", \"competicion_link\") values ('1', 'SERIE \"A\" Grupo I', 'http://www.asofusa.com/calendario?idcompeticion=308', 'http://www.asofusa.com/competiciones?temporada=9&competicion=308');");
				db.execSQL("insert into competiciones_table (\"_id\", \"nombre_competicion\", \"calendario_link\", \"competicion_link\") values ('2', 'SERIE \"A\" Grupo II', 'http://www.asofusa.com/calendario?idcompeticion=309', 'http://www.asofusa.com/competiciones?temporada=9&competicion=309');");
				db.execSQL("insert into competiciones_table (\"_id\", \"nombre_competicion\", \"calendario_link\", \"competicion_link\") values ('3', 'SERIE \"A\" Grupo III', 'http://www.asofusa.com/calendario?idcompeticion=310', 'http://www.asofusa.com/competiciones?temporada=9&competicion=310');");
				db.execSQL("insert into competiciones_table (\"_id\", \"nombre_competicion\", \"calendario_link\", \"competicion_link\") values ('4', 'SERIE \"B\" Grupo I', 'http://www.asofusa.com/calendario?idcompeticion=311', 'http://www.asofusa.com/competiciones?temporada=9&competicion=311');");
				db.execSQL("insert into competiciones_table (\"_id\", \"nombre_competicion\", \"calendario_link\", \"competicion_link\") values ('5', 'SERIE \"B\" Grupo II', 'http://www.asofusa.com/calendario?idcompeticion=312', 'http://www.asofusa.com/competiciones?temporada=9&competicion=312');");
				db.execSQL("insert into competiciones_table (\"_id\", \"nombre_competicion\", \"calendario_link\", \"competicion_link\") values ('6', 'SERIE \"B\" Grupo III', 'http://www.asofusa.com/calendario?idcompeticion=313', 'http://www.asofusa.com/competiciones?temporada=9&competicion=313');");
				db.execSQL("insert into competiciones_table (\"_id\", \"nombre_competicion\", \"calendario_link\", \"competicion_link\") values ('7', 'SERIE \"B\" Grupo IV', 'http://www.asofusa.com/calendario?idcompeticion=314', 'http://www.asofusa.com/competiciones?temporada=9&competicion=314');");
				db.execSQL("insert into competiciones_table (\"_id\", \"nombre_competicion\", \"calendario_link\", \"competicion_link\") values ('8', 'LIGA JUVENIL ASOFUSA', 'http://www.asofusa.com/calendario?idcompeticion=319', 'http://www.asofusa.com/competiciones?temporada=9&competicion=319');");
				db.execSQL("insert into competiciones_table (\"_id\", \"nombre_competicion\", \"calendario_link\", \"competicion_link\") values ('9', 'LIGA FEMENINA ASOFUSA', 'http://www.asofusa.com/calendario?idcompeticion=319', 'http://www.asofusa.com/competiciones?temporada=9&competicion=321');");
				db.execSQL("insert into competiciones_table (\"_id\", \"nombre_competicion\", \"calendario_link\", \"competicion_link\") values ('10', '1ª DIVISIÓN OLMEDO', 'http://www.asofusa.com/calendario?idcompeticion=319', 'http://www.asofusa.com/competiciones?temporada=9&competicion=323');");
				db.execSQL("insert into competiciones_table (\"_id\", \"nombre_competicion\", \"calendario_link\", \"competicion_link\") values ('11', '1ª DIVISIÓN LOCAL PEÑAFIEL', 'http://www.asofusa.com/calendario?idcompeticion=319', 'http://www.asofusa.com/competiciones?temporada=9&competicion=322');");
				db.execSQL("insert into competiciones_table (\"_id\", \"nombre_competicion\", \"calendario_link\", \"competicion_link\") values ('12', 'LIGA VETERANOS JUNTA C Y L', 'http://www.asofusa.com/calendario?idcompeticion=319', 'http://www.asofusa.com/competiciones?temporada=9&competicion=320');");
				db.execSQL("insert into competiciones_table (\"_id\", \"nombre_competicion\", \"calendario_link\", \"competicion_link\") values ('13', 'LIGA VETERANOS INSTITUCIONES', 'http://www.asofusa.com/calendario?idcompeticion=319', 'http://www.asofusa.com/competiciones?temporada=9&competicion=316');");

			} catch (Exception e) {
				Log.e(tag, e.getMessage());
			}

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			try {
				Log.w(tag, "Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
				db.execSQL("DROP TABLE IF EXISTS " + DATABASE_CLASIFICACION_TABLE); 
				db.execSQL("DROP TABLE IF EXISTS " + DATABASE_GOLEADORES_TABLE);
				db.execSQL("DROP TABLE IF EXISTS " + DATABASE_DEPORTIVIDAD_TABLE);
				db.execSQL("DROP TABLE IF EXISTS " + DATABASE_RESULTADOS_TABLE);
				db.execSQL("DROP TABLE IF EXISTS " + DATABASE_PROXIMA_JORNADA_TABLE);
				db.execSQL("DROP TABLE IF EXISTS " + DATABASE_EQUIPOS_TABLE);
				db.execSQL("DROP TABLE IF EXISTS " + DATABASE_CALENDARIO_TABLE);
				db.execSQL("DROP TABLE IF EXISTS " + DATABASE_COMPETICIONES_TABLE);
				onCreate(db);
			} catch (Exception e) {
				Log.w(tag, e);
			}
		}
	}

	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 * 
	 * @param ctx the Context within which to work
	 */
	public DbHandler(Context ctx) {
		this.mCtx = ctx;
	}

	/**
	 * Open the database. If it cannot be opened, try to create a new
	 * instance of the database. If it cannot be created, throw an exception to
	 * signal the failure
	 * 
	 * @return this (self reference, allowing this to be chained in an
	 *         initialization call)
	 * @throws SQLException if the database could be neither opened or created
	 */
	public DbHandler open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		
		return this;
	}

	public void close() {
		mDbHelper.close();
		mDb.close();
	}

	public String getTabla(String tabla, String key, String liga) {
		String[] campos = new String[] {key, KEY_FECHA};
		String[] args = new String[] {liga};
		String out = "";

		Cursor cursor = mDb.query(tabla, campos, KEY_LIGA+"=?", args, null, null, null);


		if (cursor.moveToFirst()) {
			String fecha = cursor.getString(1);
			int horas = getHaceCuanto(fecha);

			out = cursor.getString(0) + "<p align=\"center\" style=\"font-size:10;\"><b>Última actualización: " + cursor.getString(1) + " (hace " + String.valueOf(horas) + " horas)</b></p></body></html>";
		}

		return out;
	}

	private int getHaceCuanto(String fecha) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Date date = new Date();
		try {
			date = dateFormat.parse(fecha);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Date currentTime = new Date();
		int horas = Integer.valueOf((int)(currentTime.getTime() - date.getTime())/3600000);
		return horas;
	}

	public void updateTabla(String tabla, String key, String dato, String liga) {
		String[] args = new String[] {liga};

		ContentValues contentValues = new ContentValues();
		contentValues.put(key, dato);

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"); 
		Date date = new Date();
		contentValues.put(KEY_FECHA, dateFormat.format(date).toString());


		if (mDb.update(tabla, contentValues, KEY_LIGA + "=?", args) == 0) {
			contentValues.put(KEY_LIGA, liga);
			mDb.insert(tabla, null, contentValues);		
		}
	}
	
	public void updateAlerts(String competicion, boolean b) {
		String[] args = new String[] {competicion};

		ContentValues contentValues = new ContentValues();
		if(b) {
			contentValues.put(KEY_ALERTA, 1);
		} else {
			contentValues.put(KEY_ALERTA, 0);
		}

		mDb.update(DATABASE_COMPETICIONES_TABLE, contentValues, KEY_NOMBRE_COMPETICION+"=?", args);
		
	}

	public String getEquipo(String url) {
		String out = "";
		String[] campos = new String[] {KEY_EQUIPO, KEY_FECHA};
		String[] args = new String[] {url};

		Cursor cursor = mDb.query(DATABASE_EQUIPOS_TABLE, campos, KEY_URL_EQUIPO+"=?", args, null, null, null);

		if (cursor.moveToFirst()) {
			int horas = getHaceCuanto(cursor.getString(1));
			out = cursor.getString(0) + "<p align=\"center\" style=\"font-size:4;\"><b>Última actualización: " + cursor.getString(1) + " (hace " + String.valueOf(horas) + " horas)</b></p></body></html>";
		}

		return out;
	}

	public void updateTablaEquipo(String url, String equipo) {
		String[] args = new String[] {url};

		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_EQUIPO, equipo);

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"); 
		Date date = new Date();
		contentValues.put(KEY_FECHA, dateFormat.format(date).toString());


		if (mDb.update(DATABASE_EQUIPOS_TABLE, contentValues, KEY_URL_EQUIPO+"=?", args) == 0) {
			contentValues.put(KEY_URL_EQUIPO, url);
			mDb.insert(DATABASE_EQUIPOS_TABLE, null, contentValues);		
		}
	}

	public String getCalendario(String url) {
		String out = "";
		String[] campos = new String[] {KEY_CALENDARIO, KEY_FECHA};
		String[] args = new String[] {url};

		Cursor cursor = mDb.query(DATABASE_CALENDARIO_TABLE, campos, KEY_URL_CALENDARIO+"=?", args, null, null, null);

		if (cursor.moveToFirst()) {
			int horas = getHaceCuanto(cursor.getString(1));
			out = cursor.getString(0) + "<p align=\"center\" style=\"font-size:4;\"><b>Última actualización: " + cursor.getString(1) + " (hace " + String.valueOf(horas) + " horas)</b></p></body></html>";
		}

		return out;
	}

	public void updateTablaCalendario(String url, String calendario) {
		String[] args = new String[] {url};

		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_CALENDARIO, calendario);

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"); 
		Date date = new Date();
		contentValues.put(KEY_FECHA, dateFormat.format(date).toString());


		if (mDb.update(DATABASE_CALENDARIO_TABLE, contentValues, KEY_URL_CALENDARIO+"=?", args) == 0) {
			contentValues.put(KEY_URL_CALENDARIO, url);
			mDb.insert(DATABASE_CALENDARIO_TABLE, null, contentValues);		
		}
	}

	public List <String> getListaCompeticiones() {
		List <String> out = new ArrayList<String>();
		String[] campos = new String[] {KEY_NOMBRE_COMPETICION};

		Cursor cursor = mDb.query(DATABASE_COMPETICIONES_TABLE, campos, null, null, null, null, null);

		if (cursor.moveToFirst()) {
			do {
				out.add(cursor.getString(0));
			} while (cursor.moveToNext());
		}

		return out;
	}
	
	public List<Integer> getListaAlertas() {
		List <Integer> out = new ArrayList<Integer>();
		String[] campos = new String[] {KEY_ALERTA};

		Cursor cursor = mDb.query(DATABASE_COMPETICIONES_TABLE, campos, null, null, null, null, null);

		if (cursor.moveToFirst()) {
			do {
				out.add(cursor.getInt(0));
			} while (cursor.moveToNext());
		}

		return out;
	}

	public void updateListCompeticiones(List<String[]> listaCompeticiones_Url) throws SQLException {
		
		try {
			mDb.execSQL("DROP TABLE IF EXISTS " + DATABASE_COMPETICIONES_TABLE);
			mDb.execSQL(CREATE_COMPETICIONES_TABLE);

			for (int i = 0; i < listaCompeticiones_Url.size(); i++) {
				ContentValues contentValues = new ContentValues();
				int index = listaCompeticiones_Url.get(i)[1].indexOf("LIGA ASOFUSA ");
				String nombre_competicion = listaCompeticiones_Url.get(i)[1];
				if (index != -1) {
					nombre_competicion = listaCompeticiones_Url.get(i)[1].substring(("LIGA ASOFUSA ").length());
				}
				contentValues.put(KEY_NOMBRE_COMPETICION, nombre_competicion);
				contentValues.put(KEY_COMPETICION_LINK, "http://www.asofusa.com/competiciones?temporada=9&" + listaCompeticiones_Url.get(i)[0]);
				contentValues.put(KEY_CALENDARIO_LINK, "http://www.asofusa.com/calendario?id" + listaCompeticiones_Url.get(i)[0].substring(listaCompeticiones_Url.get(i)[0].indexOf("competicion=")));
		
				mDb.insert(DATABASE_COMPETICIONES_TABLE, null, contentValues);		
			}
		} catch (SQLException e) {
			throw e;
		}

	}

	public String getCalendarUrlFromLiga(String currentLiga) {
		String out = "";
		String[] campos = new String[] {KEY_CALENDARIO_LINK};
		String[] args = new String[] {currentLiga};

		Cursor cursor = mDb.query(DATABASE_COMPETICIONES_TABLE, campos, KEY_NOMBRE_COMPETICION+"=?", args, null, null, null);

		if (cursor.moveToFirst()) {
			out = cursor.getString(0);
		}

		return out;
	}
	public String getCompetitionUrlFromLiga(String currentLiga) {
		String out = "";
		String[] campos = new String[] {KEY_COMPETICION_LINK};
		String[] args = new String[] {currentLiga};

		Cursor cursor = mDb.query(DATABASE_COMPETICIONES_TABLE, campos, KEY_NOMBRE_COMPETICION+"=?", args, null, null, null);

		if (cursor.moveToFirst()) {
			out = cursor.getString(0);
		}

		return out;
	}

}
