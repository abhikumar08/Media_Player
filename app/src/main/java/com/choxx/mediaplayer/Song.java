package com.choxx.mediaplayer;


/*
 * 
 * THIS IS TO GENERATE CUSTOM LIST VIEW FOR SONGS
 * 
 */
public class Song {
	
	private long id;
	private String title;
	private String artist;
	private String duration;
	public Song(long songID, String songTitle, String songArtist, String songDur){
		id=songID;
		title=songTitle;
		artist=songArtist;
		duration=songDur;
	}
	
	public long getID(){return id;}
	public String getTitle(){return title;}
	public String getArtist(){return artist;}
	public String getDuration(){return duration;}

}
