package media.video.platforms;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import media.config.Configuration;
import media.entity.Video;
import media.entity.VideoSegment;

public class IqiyiVideo extends Video {

	private String downloadUrlPrefix = Configuration
			.get("iqiyi_download_url_prefix");
	private int[] bids = { 1, 2, 3 };
	private String[] types = { "flv", "mp4", "hd2" };

	public IqiyiVideo(String playListJson) throws ParseException {
		JSONObject obj = new JSONObject(playListJson);
		String vid = obj.getString("nvid");
		JSONArray tkl = obj.getJSONArray("tkl");
		JSONObject tkl0 = tkl.optJSONObject(0);
		JSONArray vs = tkl0.getJSONArray("vs");
		HashMap<String, List<VideoSegment>> segs = new HashMap<String, List<VideoSegment>>();
		for (int j = 0; j < bids.length; ++j) {
			List<VideoSegment> segments = new ArrayList<VideoSegment>();
			for (int i = 0; i < vs.length(); ++i) {
				JSONObject vsItem = vs.getJSONObject(i);
				if (vsItem.getInt("bid") == bids[j]) {
					JSONArray fs = vsItem.getJSONArray("flvs");
					if(fs.length() == 0){
						fs = vsItem.getJSONArray("fs");
					}
					for (int n = 0; n < fs.length(); ++n) {
						JSONObject fsItem = fs.getJSONObject(n);
						double time = fsItem.getDouble("d") / 1000;
						long size = fsItem.getLong("b");
						String filename = fsItem.getString("l");
						String link = downloadUrlPrefix + filename;
						VideoSegment segment = new VideoSegment(types[j], n,
								size, time);
						segment.setDownloadUrl(link);
						segment.setSegs(fs.length());
						segments.add(segment);
					}
					break;
				}
			}
			segs.put(types[j], segments);
		}
		super.setVideoid(vid);
		super.setSegs(segs);
	}

}
