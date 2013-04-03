package es.tony.crawling.dao;

import java.net.UnknownHostException;

import org.jongo.Jongo;
import org.jongo.MongoCollection;

import com.mongodb.DB;
import com.mongodb.Mongo;

public class ChordDao {
	
	private static Jongo jongo;
	
	
	public static boolean inicializar() {
		DB db;
		try {
			db = new Mongo().getDB("chordsDB");
			jongo = new Jongo(db);
			return true;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	
	public static void insertar(String document){
		MongoCollection chords = jongo.getCollection("chords");;
		chords.insert(document);
	}
}
