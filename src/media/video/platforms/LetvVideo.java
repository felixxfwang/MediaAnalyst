package media.video.platforms;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import media.entity.Video;
import media.entity.VideoSegment;

public class LetvVideo extends Video {
	
	private String[] videoQuality = { "350", "1000", "1300" };
	private String[] types = {"flv","mp4","hd2"};

	public LetvVideo(String playListJson) throws ParseException {
		JSONObject root = new JSONObject(playListJson);
		JSONObject list = root.getJSONObject("dispatch");
		double time = root.getDouble("duration");
		String vid = root.getString("vid");
		HashMap<String,List<VideoSegment>> segs = new HashMap<String,List<VideoSegment>>();
		for(int i = 0;i < videoQuality.length; ++i){
			if(list.has(videoQuality[i])){
				List<VideoSegment> segments = new ArrayList<VideoSegment>();
				JSONArray data =list.getJSONArray(videoQuality[i]);
				String link = data.getString(0);
				VideoSegment segment = new VideoSegment(types[i],0,0,time);
				segment.setDownloadUrl(link);
				segment.setSegs(1);
				segments.add(segment);
				segs.put(types[i], segments);
			}
		}
		super.setVideoid(vid);
		super.setSegs(segs);
	}
}
