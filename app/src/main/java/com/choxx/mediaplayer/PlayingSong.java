package com.choxx.mediaplayer;

import java.util.ArrayList;

import android.net.Uri;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;


/*
 * THIS IS THE CLASS WHERE CURRENT SONG IS BEING PLAYED
 * 
 */
public class PlayingSong extends Activity {

	
	ActionBar ac;
	SQLiteDatabase db;
	Intent i;
	ArrayList<Song> songs;
	TextView tvSongTitle,tvSongArtist;
	ImageButton play,next,pause,previous,ivUpdatLib;
	ImageView ivAlbumArt;
	static int pos=0;
	String defuri;
	SeekBar seekBar;
	ProgressBar progressBar;
	Thread tfrst,tplay,tnext,tprevious;
	int dur;
	Song playsong;
	SharedPreferences sp;
	long songId;
	
	//------------------------  ONCREATE()   -----------------
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_playing_song);
		
		//-------------------  GETTING ID's   ----------------------
		next=(ImageButton) findViewById(R.id.next);
		pause=(ImageButton) findViewById(R.id.pause);
		play=(ImageButton) findViewById(R.id.play);
		previous=(ImageButton) findViewById(R.id.previous);
		tvSongTitle=(TextView) findViewById(R.id.tvSongTitle);
		tvSongArtist=(TextView) findViewById(R.id.tvSongArtist);
		ivAlbumArt=(ImageView) findViewById(R.id.ivAlbumArt);
		ivUpdatLib=(ImageButton) findViewById(R.id.ivUpdateLib);
		progressBar=(ProgressBar) findViewById(R.id.progressBar);
		sp=getSharedPreferences("song_pref", MODE_PRIVATE);
		
		songs=new ArrayList<Song>();
		ac=getActionBar();
		ac.setTitle("Now Playing");
		ac.setIcon(R.drawable.icon_nowplaying);
		tvSongTitle.setText(sp.getString("title", ""));
		tvSongArtist.setText(sp.getString("artist", ""));
		db=openOrCreateDatabase("myappdb", MODE_PRIVATE, null);		// OPENING DATABASE OBJECT
		pause.setVisibility(View.INVISIBLE);
		
		Cursor c= db.rawQuery("Select * from songlist", null);
		while(c.moveToNext())
		{
			
			songs.add(new Song((Long.parseLong(c.getString(2))),c.getString(0), c.getString(1),c.getString(3)));
		}

		Song s1=songs.get(0);
		long defid=s1.getID();
		Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,defid);
		defuri=trackUri.toString();
				
		
		
		//-------------getting uri of current song--------
		try {
			Bundle b=getIntent().getExtras();
			pos=b.getInt("id");
			Song curr=songs.get(pos);
			String uri=b.getString("uri");//uri of current song coming from playlist
			songId=b.getLong("songId");
		
			SharedPreferences.Editor ed=sp.edit();
			ed.putString("currSong", uri);
			ed.putString("title", curr.getTitle());
			ed.putString("artist", curr.getArtist());
			
			ed.commit();
			i=new Intent(PlayingSong.this,ServiceToPlay.class);
			Bundle bundle=new Bundle();
			bundle.putString("uri", uri);
			i.putExtras(bundle);

			
			tvSongTitle.setText(curr.getTitle());
			tvSongArtist.setText(curr.getArtist());
			dur=Integer.parseInt(curr.getDuration());
			
			
			  progressBar.setMax(dur);
			startService(i);
			play.setVisibility(View.INVISIBLE);
			pause.setVisibility(View.VISIBLE);
		} 
		catch (Exception e) 
		{
			
		}
		//------------  THREAD FOR PROGRESS BAR   -----------
		tfrst=new Thread()
		{
			public void run()
			{
				for(int i=0;i<=dur;i+=100)
				{
					progressBar.setProgress(i);
				
				try{
					Thread.sleep(100);
				}catch (Exception e) {
					
				}
				}
			}
		};
		tfrst.start();
		
		
		//--------------- PAUSE CLICK   ----------
		pause.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
			
				play.setVisibility(View.VISIBLE);
				pause.setVisibility(View.INVISIBLE);
				stopService(i);
				
				
			}
		});
		
		
		//----------------   PLAY CLICK  -----------------------
		play.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				play.setVisibility(View.INVISIBLE);
				pause.setVisibility(View.VISIBLE);
				String uri1=sp.getString("currSong", defuri);
				i=new Intent(PlayingSong.this,ServiceToPlay.class);
				Bundle bundle=new Bundle();
				bundle.putString("uri", uri1);
				i.putExtras(bundle);
				startService(i);
				progressBar.setMax(dur);
				tplay=new Thread()
				{
					public void run()
					{
						for(int i=0;i<=dur;i+=100)
						{
							progressBar.setProgress(i);
						
						try{
							Thread.sleep(100);
						}catch (Exception e) {
							
						}
						}
					}
				};
				tplay.start();
			}
		});
		
		
		//------------------   NEXT CLICK    --------------------
		next.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				play.setVisibility(View.INVISIBLE);
				pause.setVisibility(View.VISIBLE);
				Cursor p=db.rawQuery("select * from pos", null);
				while(p.moveToNext())
					{
					pos=Integer.parseInt(p.getString(0));
					}
				pos=pos+1;
				playsong=songs.get(pos);
				
				db.execSQL("insert into pos values('"+pos+"')");
				final int dur=Integer.parseInt(playsong.getDuration());
				
				long songId=playsong.getID();
				Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,songId);
				String uri=trackUri.toString();
				Bundle bb=new Bundle();
				bb.putString("uri", uri);
				i=new Intent(PlayingSong.this,ServiceToPlay.class);
				i.putExtras(bb);
				startService(i);
				progressBar.setMax(dur);
				SharedPreferences.Editor ed=sp.edit();
				ed.putString("currSong", uri);
				ed.putString("title", playsong.getTitle());
				ed.putString("artist", playsong.getArtist());
				ed.commit();
				
				 tnext=new Thread()
				 
				{
					public void run()
					{
						for(int i=0;i<=dur;i+=100)
						{
							progressBar.setProgress(i);
						
						try{
							Thread.sleep(100);
						}catch (Exception e) {
							
						}
						}
					}
				};
				tnext.start();
				
				
				
				tvSongTitle.setText(playsong.getTitle());
				tvSongArtist.setText(playsong.getArtist());			
			}
		});
	
		
		//------------------    PREVIOUS CLICK    -------------
		previous.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				play.setVisibility(View.INVISIBLE);
				pause.setVisibility(View.VISIBLE);
				Cursor p=db.rawQuery("select * from pos", null);
				while(p.moveToNext())
					{
					pos=Integer.parseInt(p.getString(0));
					}
				pos=pos-1;
				playsong=songs.get(pos);
				
				db.execSQL("insert into pos values('"+pos+"')");
				dur=Integer.parseInt(playsong.getDuration());
				long songId=playsong.getID();
				Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,songId);
				String uri=trackUri.toString();
				
				Intent i=new Intent(PlayingSong.this,ServiceToPlay.class);
				Bundle bb=new Bundle();
				bb.putString("uri", uri);
				i.putExtras(bb);
				startService(i);
				SharedPreferences.Editor ed=sp.edit();
				ed.putString("currSong", uri);
				ed.putString("title", playsong.getTitle());
				ed.putString("artist", playsong.getArtist());
				ed.commit();
				progressBar.setMax(dur);
				tprevious=new Thread()
				{
					public void run()
					{
						for(int i=0;i<=dur;i+=100)
						{
							progressBar.setProgress(i);
						
						try{
							Thread.sleep(100);
						}catch (Exception e) {
							
						}
						}
					}
				};
				tprevious.start();
				
				
				tvSongTitle.setText(playsong.getTitle());
				tvSongArtist.setText(playsong.getArtist());
				
			}
		});
		
		
		//-----------------  UPDATE LIBRARY  ------------------
		ivUpdatLib.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				AlertDialog.Builder ob=new AlertDialog.Builder(PlayingSong.this);
				ob.setTitle("Confirmation Required");
				ob.setMessage("Do You Want To Update Your Library??");
				ob.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						try{
							stopService(i);
						}catch (Exception e) {
							
						}
						startActivity(new Intent(getApplicationContext(),MainActivity.class));
						SharedPreferences.Editor ed=sp.edit();
						ed.putString("libraryUpdate", "update");
						
						ed.commit();
						finish();
					}
				});
				ob.setNegativeButton("NO",new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						return;
					}
				});
				ob.show();
			}
		});
		
		
		
	}//ONCREATE()

	
	
	
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		
		
			if(item.getItemId()==R.id.playlist)
			{
				startActivity(new Intent(this,PlayList.class));
				finish();
			}
			else if(item.getItemId()==R.id.itemVideo){
				startActivity(new Intent(PlayingSong.this,VideoList.class));
				try{
				stopService(i);
				}catch (Exception e) {
					
				}
				}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.playing_song, menu);
		return true;
	}
	
	
	@Override
	public void onBackPressed() 
	{
		
		super.onBackPressed();
		finish();
	}

}// CLASS CLOSED
