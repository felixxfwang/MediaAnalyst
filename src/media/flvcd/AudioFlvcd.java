package media.flvcd;

import java.io.IOException;
import java.text.ParseException;

import media.config.Configuration;
import media.entity.Audio;
import media.net.HttpRequest;
import media.net.HttpRequest.HttpResult;

public class AudioFlvcd {
	private final String SONG_LINK_URL = Configuration.get("song_link_url");

	public Audio getAudio(String playPageLink) throws ParseException,
			IOException {
		String songId = null;
		if (playPageLink != null) {
			songId = playPageLink.substring(playPageLink
					.indexOf("music.baidu.com/song/")
					+ "music.baidu.com/song/".length());
		}
		return getAudioFromId(songId);
	}

	private Audio getAudioFromId(String songId) throws ParseException,
			IOException {
		HttpRequest request = new HttpRequest();
		request.addParam("songIds", songId);
		request.addParam("hq", "1");
		HttpResult result = request.post(SONG_LINK_URL);
		Audio audio = Audio.getAudio(result.textResult);
		return audio;
	}

}
