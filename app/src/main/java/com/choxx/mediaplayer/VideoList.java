package com.choxx.mediaplayer;

import java.util.ArrayList;


import android.net.Uri;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import android.widget.AdapterView.OnItemClickListener;

//------------------------     FOR GENERATING LIST OF VIDEOS	------------------

public class VideoList extends Activity {
ListView vidList;
	
	ArrayList<Videos> videosongs;
	ActionBar ac;
	SQLiteDatabase db;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_list);
		videosongs= new ArrayList<Videos>();
		ac=getActionBar();
		ac.setTitle("Videos");
		ac.setIcon(R.drawable.nowplaying);
		vidList=(ListView) findViewById(R.id.vidList);
		videosongs=new ArrayList<Videos>();
		db=openOrCreateDatabase("myappdb", MODE_PRIVATE, null);
		
		Cursor c= db.rawQuery("Select * from videolist", null);
		while(c.moveToNext())
		{
			
			videosongs.add(new Videos((Long.parseLong(c.getString(1))),c.getString(0), c.getString(2)));
		}
		
		VideoAdapter vidadt= new VideoAdapter(this, videosongs);
		vidList.setAdapter(vidadt);
		vidList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,long arg3) 
			{	
				Videos playVideo=videosongs.get(pos);
				long videoId=playVideo.getID();
				Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,videoId);
				Intent i = new Intent(VideoList.this,VideoPlay.class);
				i.putExtra("uri", trackUri.toString());
				i.putExtra("pos", pos);
				startActivity(i);
				
			}
		});
	}

	
}
