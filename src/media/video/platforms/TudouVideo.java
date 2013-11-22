package media.video.platforms;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import media.entity.Video;
import media.entity.VideoSegment;

import org.json.JSONArray;
import org.json.JSONObject;

public class TudouVideo extends Video{
	private int seed;
	private String vidEncoded;
	private String key1;
	private String key2;
	private double seconds;
	private HashMap<String, String> streamfileids;
	private HashMap<String, String> stream_ids;
	private HashMap<String, Long> streamsizes;

	@SuppressWarnings("unchecked")
	public TudouVideo(String playListJosn) throws ParseException {
		JSONObject obj = new JSONObject(playListJosn);
		JSONArray data = obj.getJSONArray("data");
		JSONObject video = data.optJSONObject(0);
		int seed = video.getInt("seed");
		String videoid = video.getString("videoid");
		String vidEncoded = video.getString("vidEncoded");
		String key1 = video.has("key1") ? video.getString("key1") : "";
		String key2 = video.has("key2") ? video.getString("key2") : "";
		double seconds = video.getDouble("seconds");
		// for file ids
		JSONObject fileidsObj = video.getJSONObject("streamfileids");
		HashMap<String, String> streamfileids = new HashMap<String, String>();
		for (Iterator<String> iter = fileidsObj.keys(); iter.hasNext();) {
			String key = iter.next();
			String value = fileidsObj.getString(key);
			streamfileids.put(key, value);
		}
		// for stream_ids
		JSONObject sidObj = video.getJSONObject("stream_ids");
		HashMap<String, String> streamids = new HashMap<String, String>();
		for (Iterator<String> iter = sidObj.keys(); iter.hasNext();) {
			String key = iter.next();
			String value = sidObj.getString(key);
			streamids.put(key, value);
		}
		// for stream sizes
		JSONObject sizeObj = video.getJSONObject("streamsizes");
		HashMap<String, Long> streamsizes = new HashMap<String, Long>();
		for (Iterator<String> iter = sizeObj.keys(); iter.hasNext();) {
			String key = iter.next();
			long value = sizeObj.getLong(key);
			streamsizes.put(key, value);
		}
		// for segments
		JSONObject segsObj = video.getJSONObject("segs");
		HashMap<String, List<VideoSegment>> segs = new HashMap<String, List<VideoSegment>>();
		for (Iterator<String> iter = segsObj.keys(); iter.hasNext();) {
			String key = iter.next();
			JSONArray segments = segsObj.optJSONArray(key);
			List<VideoSegment> value = new ArrayList<VideoSegment>();
			for (int i = 0; i < segments.length(); ++i) {
				JSONObject s = segments.getJSONObject(i);
				int no = s.getInt("no");
				long size = s.getLong("size");
				double duration = s.getDouble("seconds");
				String k = s.has("k") ? s.getString("k") : "";
				String k2 = s.has("k2") ? s.getString("k2") : "";
				VideoSegment v = new VideoSegment(key, no, size, duration, k,
						k2);
				v.setSegs(segments.length());
				value.add(v);
			}
			segs.put(key, value);
		}

		super.setVideoid(videoid);
		super.setSegs(segs);
		this.key1 = key1;
		this.key2 = key2;
		this.seed = seed;
		this.seconds = seconds;
		this.stream_ids = streamfileids;
		this.streamfileids = streamfileids;
		this.streamsizes = streamsizes;
		this.vidEncoded = vidEncoded;
	}

	
	public int getSeed() {
		return seed;
	}

	public void setSeed(int seed) {
		this.seed = seed;
	}

	public String getVidEncoded() {
		return vidEncoded;
	}

	public void setVidEncoded(String vidEncoded) {
		this.vidEncoded = vidEncoded;
	}

	public String getKey2() {
		return key2;
	}

	public void setKey2(String key2) {
		this.key2 = key2;
	}

	public String getKey1() {
		return key1;
	}

	public void setKey1(String key1) {
		this.key1 = key1;
	}

	public double getSeconds() {
		return seconds;
	}

	public void setSeconds(double seconds) {
		this.seconds = seconds;
	}

	public HashMap<String, String> getStream_ids() {
		return stream_ids;
	}

	public void setStream_ids(HashMap<String, String> stream_ids) {
		this.stream_ids = stream_ids;
	}

	public HashMap<String, Long> getStreamsizes() {
		return streamsizes;
	}

	public void setStreamsizes(HashMap<String, Long> streamsizes) {
		this.streamsizes = streamsizes;
	}

	public HashMap<String, String> getStreamfileids() {
		return streamfileids;
	}

	public void setStreamfileids(HashMap<String, String> streamfileids) {
		this.streamfileids = streamfileids;
	}
}
