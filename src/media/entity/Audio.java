package media.entity;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;

import media.analyst.ResultProcesser;

import org.json.JSONArray;
import org.json.JSONObject;

public class Audio {
	private String songId;
	private HashMap<String, AudioLinkInfo> audioLinks;

	private Audio(String songId) {
		this.songId = songId;
		audioLinks = new HashMap<String, AudioLinkInfo>();
	}

	@SuppressWarnings("unchecked")
	public static Audio getAudio(String json) throws ParseException {
		JSONObject obj = new JSONObject(json);
		JSONObject data = obj.getJSONObject("data");
		JSONArray songList = data.getJSONArray("songList");
		JSONObject song = songList.optJSONObject(0);
		String songId = song.getString("songId");
		JSONObject linkInfo = song.getJSONObject("linkinfo");
		Audio audio = new Audio(songId);
		for (Iterator<String> iter = linkInfo.keys(); iter.hasNext();) {
			String key = iter.next();
			JSONObject link = linkInfo.getJSONObject(key);
			String songLink = link.getString("songLink");
			String format = link.getString("format");
			int time = link.getInt("time");
			long size = link.getLong("size");
			int rate = link.getInt("rate");
			AudioLinkInfo audioLinkInfo = new AudioLinkInfo(rate, time, size,
					format, songLink);
			audio.addAudioLink(key, audioLinkInfo);
		}
		return audio;
	}

	public void analyze(String type, ResultProcesser processer)
			throws IOException {
		analyze(type, processer, false);
	}

	public void analyze(String type, ResultProcesser processer, boolean waitPlay)
			throws IOException {
		if (audioLinks.containsKey(type)) {
			AudioLinkInfo link = audioLinks.get(type);
			
			link.setProcesser(processer);
			link.startAnalyzing(waitPlay);
		}
	}

//	public void analyze(ResultProcesser processer) throws IOException {
//		analyze(processer, false);
//	}
//
//	public void analyze(ResultProcesser processer, boolean waitPlay)
//			throws IOException {
//		for (String key : audioLinks.keySet()) {
//			analyze(key, processer, waitPlay);
//		}
//	}

	public String getSongId() {
		return this.songId;
	}

	private void addAudioLink(String type, AudioLinkInfo link) {
		audioLinks.put(type, link);
	}
}
