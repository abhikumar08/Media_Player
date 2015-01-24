package com.choxx.mediaplayer;

import java.util.ArrayList;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentUris;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.widget.MediaController;
import android.widget.VideoView;

/*
 * THIS IS FOR PLAYING THE SELECTED VIDEO FROM
 * VIDEOS LIST
 * 
 */

public class VideoPlay extends Activity implements OnCompletionListener
{

	VideoView vv;
	ArrayList<Videos> videosongs;
	MediaPlayer mp;
	ActionBar ac;
	MediaController mc;
	SQLiteDatabase db;
	Bundle b;
	int pos;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_view);
		vv=(VideoView) findViewById(R.id.vidView);
		ac=getActionBar();
		ac.hide();
		videosongs=new ArrayList<Videos>();
		db=openOrCreateDatabase("myappdb", MODE_PRIVATE, null);
		
		Cursor c= db.rawQuery("Select * from videolist", null);
		while(c.moveToNext())
		{
			
			videosongs.add(new Videos((Long.parseLong(c.getString(1))),c.getString(0), c.getString(2)));
		}
		
		b=getIntent().getExtras();
		String uri=b.getString("uri");
		pos=b.getInt("pos");
		mc=new MediaController(VideoPlay.this);
		mc.setAnchorView(vv);
		mc.setMediaPlayer(vv);
		vv.setMediaController(mc);
		vv.setVideoURI(Uri.parse(uri));
		vv.start();
		vv.setOnCompletionListener(this);
		
	}//  ONCREATE() CLOSED
	
	
	@Override
	public void onBackPressed() {
		
		super.onBackPressed();
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.video_view, menu);
		return true;
	}
	
	
	@Override
	public void onCompletion(MediaPlayer mp) 
	{
		playVideo();
		
	}
	
	
	//------------------------	FOR CONTINOUS PLAYING OF VIDEOS	------------------
	public void playVideo()
	{
		pos=pos+1;
		Videos playVideo=videosongs.get(pos);
		long videoId=playVideo.getID();
		//Toast.makeText(PlayList.this, "playlist: "+songId, 2000).show();
		Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,videoId);
		mc.setAnchorView(vv);
		mc.setMediaPlayer(vv);
		vv.setMediaController(mc);
		vv.setVideoURI(trackUri);
		vv.start();
		
		vv.setOnCompletionListener(this);
	}

}
