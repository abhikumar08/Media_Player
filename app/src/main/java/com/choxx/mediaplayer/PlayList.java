package com.choxx.mediaplayer;

import java.util.ArrayList;
import android.net.Uri;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/*
 * 
 * THIS IS FOR PLAYLIST OF SONGS
 * 
 */
public class PlayList extends Activity {

	SharedPreferences sp;
	SQLiteDatabase db;
	ListView lvSongList;
	ArrayList<Song> songs;
	ActionBar ac;
	int pos;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_list);
		
		ac=getActionBar();
		ac.setTitle("Play List");
		ac.setIcon(R.drawable.ic_playlist);
		lvSongList = (ListView) findViewById(R.id.lvSongList);
		
		songs=new ArrayList<Song>();
		
		db=openOrCreateDatabase("myappdb", MODE_PRIVATE, null);
		
		Cursor c= db.rawQuery("Select * from songlist", null);
		while(c.moveToNext())
		{
			
			songs.add(new Song((Long.parseLong(c.getString(2))),c.getString(0), c.getString(1),c.getString(3)));
		}
		
		
		SongAdapter sngadt= new SongAdapter(this, songs);
		lvSongList.setAdapter(sngadt);
		
		lvSongList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,long arg3) 
			{
				db.execSQL("insert into pos values('"+pos+"')");
				Song playsong=songs.get(pos);
				long songId=playsong.getID();
				Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,songId);
				String uri=trackUri.toString();
				
				
				Intent send=new Intent(PlayList.this,PlayingSong.class);
				send.putExtra("uri", uri);
				send.putExtra("id",pos);
				
				SharedPreferences sp=getSharedPreferences("song_pref", MODE_PRIVATE);
				SharedPreferences.Editor ed=sp.edit();
				ed.putInt("pos", pos);
				ed.commit();
				
				startActivity(send);
				finish();
			}
		});
		
	}// ONCREATE() CLOSED

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		
			if(item.getItemId()==R.id.gotoplayer)
			{
				try{
					startActivity(new Intent(this,PlayingSong.class));
					finish();
				}catch (Exception e) {
					// TODO: handle exception
				}
			}
		
		
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.play_list, menu);
		return true;
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent send=new Intent(PlayList.this,PlayingSong.class);
		startActivity(send);
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
	};
}
