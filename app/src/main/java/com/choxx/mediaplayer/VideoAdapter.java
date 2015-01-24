package com.choxx.mediaplayer;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/*
 * THIS IS VIDEO ADAPTER FOR BIND TITLES OF VIDEOS IN LISTV VIEW OF VIDEOS 
 * 
 */

public class VideoAdapter extends BaseAdapter {

	
	
		
		//song list and layout
		private ArrayList<Videos> videos;
		private LayoutInflater videoInf;
		
		//constructor
		public VideoAdapter(Context c, ArrayList<Videos> theVideos){
			videos=theVideos;
			videoInf=LayoutInflater.from(c);
		}

		@Override
		public int getCount() {
			return videos.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//map to song layout
			LinearLayout videoLay = (LinearLayout)videoInf.inflate
					(R.layout.video, parent, false);
			//get title and artist views
			TextView videoView = (TextView)videoLay.findViewById(R.id.videoTitle);
			
			//get song using position
			Videos currVideo = videos.get(position);
			//get title and artist strings
			videoView.setText(currVideo.getTitle());

			//set position as tag
			videoLay.setTag(position);
			return videoLay;
		}

	

	
	
}
