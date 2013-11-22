package media.video.platforms;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import media.entity.Video;
import media.entity.VideoSegment;

public class SohuVideo extends Video {

	public SohuVideo(HashMap<String,String> playListJsons) throws ParseException {
		String vid = null;
		HashMap<String, List<VideoSegment>> segs = new HashMap<String, List<VideoSegment>>();
		for(String key:playListJsons.keySet()){
			String playListJson = playListJsons.get(key);
			JSONObject obj = new JSONObject(playListJson);
			vid = obj.getString("id");
			String allot = obj.getString("allot");
			JSONObject data = obj.getJSONObject("data");
			JSONArray clipsBytes = data.getJSONArray("clipsBytes");
			JSONArray clipsDuration= data.getJSONArray("clipsDuration");			
			JSONArray su = data.getJSONArray("su");			
			List<VideoSegment> segments = new ArrayList<VideoSegment>();
			for(int i = 0;i<su.length();++i){
				String suItem = su.getString(i);
				double time = clipsDuration.getDouble(i);
				long size = clipsBytes.getInt(i);
				int no = i;
				String link = "http://" + allot + "/?new=" + suItem;
				VideoSegment segment = new VideoSegment(key,no,size,time);
				segment.setDownloadUrl(link);
				segment.setSegs(su.length());
				segments.add(segment);
			}
			segs.put(key, segments);
		}
		super.setVideoid(vid);
		super.setSegs(segs);		
	}
}
