package com.choxx.mediaplayer;

import java.util.ArrayList;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;


/*
 * 	THIS IS THE SERVICE CLASS TO PLAY 
 * THE SONG.
 */
public class ServiceToPlay extends Service implements android.media.MediaPlayer.OnCompletionListener 
{

	
	MediaPlayer mp;
	ArrayList<Song> song;
	int pos;
	SQLiteDatabase db;
	String tempUri;
	
	@Override
	public void onCreate() 
	{
		
		super.onCreate();
		mp=new MediaPlayer();
		 db=openOrCreateDatabase("myappdb", MODE_PRIVATE, null);
		song=new ArrayList<Song>();
		
		
		
	
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		
		mp.reset();
		
		Bundle b=intent.getExtras();
		String uri=b.getString("uri");
		mp=MediaPlayer.create(this, Uri.parse(uri));
		mp.start();
		mp.setOnCompletionListener(this);
		tempUri=uri;
		return super.onStartCommand(intent, flags, startId);
	}
	
		
	@Override
	public IBinder onBind(Intent intent) {
	
		return null;
	}

	@Override
	public void onDestroy() 
	{
		super.onDestroy();
		mp.pause();
	}

	@Override
	public void onCompletion(MediaPlayer mp) 
	{
		playagain();
			
	}
	
	//-----------    METHOD FOR CONTINOUS PLAYING OF SONG	----------
	public void playagain()
	{
		
		Cursor c= db.rawQuery("Select * from songlist", null);
		
		while(c.moveToNext())
		{
		
			song.add(new Song((Long.parseLong(c.getString(2))),c.getString(0), c.getString(1),c.getString(3)));
		}
		Cursor p=db.rawQuery("select * from pos", null);
		while(p.moveToNext())
		{
		pos=Integer.parseInt(p.getString(0));
		}
		pos=pos+1;
		db.execSQL("insert into pos values('"+pos+"')");
	 	Song playsong=song.get(pos);
		long songId=playsong.getID();
		Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,songId);
		String uri=trackUri.toString();
		
		mp.reset();
		mp=MediaPlayer.create(this, Uri.parse(uri));
		mp.start();
		mp.setOnCompletionListener(this);
		
	}
}
