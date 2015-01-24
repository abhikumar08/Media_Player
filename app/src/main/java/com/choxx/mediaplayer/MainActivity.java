package com.choxx.mediaplayer;

import java.util.ArrayList;



import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends Activity {

	
	SQLiteDatabase db;
	ActionBar ab;
	SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ab=getActionBar();
		ab.setTitle("Updating Songs/Videos in Library");
		
		//--------------------   CREATING DATABASE    -------------------
		db=openOrCreateDatabase("myappdb", MODE_PRIVATE, null);
		db.execSQL("create table if not exists songlist(title varchar(30),artist varchar(50),songid varchar(30),duration varchar(5))");
		db.execSQL("create table if not exists videolist(videotitle varchar(30),videoid varchar(30),videoduration varchar(5))");
		db.execSQL("create table if not exists pos(position int(5))");
		
		SharedPreferences sp=getSharedPreferences("song_pref", MODE_PRIVATE);
		
		if(sp.getString("libraryUpdate","notUpdate" ).equals("update")) 
		{
			SharedPreferences.Editor ed=sp.edit();
		getSongList();
		Toast.makeText(getApplicationContext(), "Library updated Successfully..", 2000).show();
		ed.putString("libraryUpdate", "notUpdate");
		ed.commit();
		}
		
		if(sp.getBoolean("created", false))
		{
		startActivity(new Intent(this,PlayingSong.class));
		}
		else{
			SharedPreferences.Editor ed=sp.edit();
			getSongList();
			startActivity(new Intent(this,PlayList.class));
			ed.putBoolean("created", true);
			ed.commit();
		}
		finish();
	}

	
	
	//----------------   METHOD TO INSERT SONGS AND VIDEOS INTO DATABASE  ---------------
	public void getSongList(){
		
		
		db.execSQL("drop table songlist");
		db.execSQL("drop table videolist");
		db.execSQL("create table if not exists songlist(title varchar(30),artist varchar(50),songid varchar(30),duration varchar(5))");
		
		//-----------------------     FOR SONGS    --------------------------
		//query external audio
		ContentResolver musicResolver = getContentResolver();
		Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		
		Cursor musicCursor = musicResolver.query(musicUri, null, null, null,null);
		//iterate over results if valid
		if(musicCursor!=null && musicCursor.moveToFirst()){
			//get columns
			int titleColumn = musicCursor.getColumnIndex
					(android.provider.MediaStore.Audio.Media.TITLE);
			int idColumn = musicCursor.getColumnIndex
					(android.provider.MediaStore.Audio.Media._ID);
			int artistColumn = musicCursor.getColumnIndex
					(android.provider.MediaStore.Audio.Media.ARTIST);
			int durationColumn=musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.DURATION);
			//add songs to list
			while (musicCursor.moveToNext())
			{
				long thisId = musicCursor.getLong(idColumn);
				String thisTitle = musicCursor.getString(titleColumn);
				String thisArtist = musicCursor.getString(artistColumn);
				String thisDuration=musicCursor.getString(durationColumn);
				
				try{
					db.execSQL("insert into songlist values('"+thisTitle+"','"+thisArtist+"','"+thisId+"','"+thisDuration+"')");
				}catch (Exception e) {
					
				}
			}//while 

		}//if
			
			
			//----------------		for videos		-----------------------
			
		db.execSQL("create table if not exists videolist(videotitle varchar(30),videoid varchar(30),videoduration varchar(5))");
			ContentResolver videoResolver = getContentResolver();
			Uri videoUri = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
			
			Cursor videoCursor = videoResolver.query(videoUri, null, null, null,null);
			//iterate over results if valid
			if(videoCursor!=null && videoCursor.moveToFirst()){
				//get columns
				int titleColumnVideo = videoCursor.getColumnIndex
						(android.provider.MediaStore.Video.Media.TITLE);
				int idColumnVideo = videoCursor.getColumnIndex
						(android.provider.MediaStore.Video.Media._ID);
				int durationColumnVideo=videoCursor.getColumnIndex(android.provider.MediaStore.Video.Media.DURATION);
				
				//add VIDEOS to list
				while (videoCursor.moveToNext())
				{
					long thisId = videoCursor.getLong(idColumnVideo);
					String thisTitle = videoCursor.getString(titleColumnVideo);
					
					String thisDuration=videoCursor.getString(durationColumnVideo);
					
					try{
						db.execSQL("insert into videolist values('"+thisTitle+"','"+thisId+"','"+thisDuration+"')");
					}catch (Exception e) {
						// TODO: handle exception
					}
					
				}//while 
				
			
		}//if
	}//getview
		

}// CLASS CLOSED--------------------
