package com.choxx.mediaplayer;

/*
 * THIS IS TO CREATE THE CUSTOM LIST FOR VIDEOS LIST
 * 
 */
public class Videos 
{
	private long id;
	private String title;
	private String duration;
	
	public Videos(long videoID, String videoTitle, String videoDur){
		id=videoID;
		title=videoTitle;
		duration=videoDur;
	}
	
	public long getID(){return id;}
	public String getTitle(){return title;}
	public String getDuration(){return duration;}

	

}
