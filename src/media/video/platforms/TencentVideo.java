package media.video.platforms;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import media.entity.Video;
import media.entity.VideoSegment;

public class TencentVideo extends Video {
	
	public TencentVideo(HashMap<String,String> playListJsons) throws ParseException{
		String vid = null;
		HashMap<String, List<VideoSegment>> segs = new HashMap<String, List<VideoSegment>>();
		for(String key:playListJsons.keySet()){
			String playListJson = playListJsons.get(key);
			JSONObject obj = new JSONObject(playListJson);
			JSONObject vl = obj.getJSONObject("vl");
			JSONArray vi = vl.getJSONArray("vi");
			JSONObject video = vi.optJSONObject(0);
			if(vid == null) vid =  video.getString("vid");
			JSONObject cl = video.getJSONObject("cl");
			JSONArray ci = cl.getJSONArray("ci");
			
			List<VideoSegment> segments = new ArrayList<VideoSegment>();
			for(int i = 0;i<ci.length();++i){
				JSONObject item = ci.getJSONObject(i);
				double time = item.getDouble("cd");
				long size = item.getLong("cs");
				int no = item.getInt("idx") -1;
				VideoSegment segment = new VideoSegment(key,no,size,time);
				segment.setSegs(ci.length());
				segments.add(segment);
			}
			segs.put(key, segments);
		}
		super.setVideoid(vid);
		super.setSegs(segs);		
	}

}
