package media.video.platforms;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;
import org.htmlparser.NodeFilter;
import org.htmlparser.filters.LinkStringFilter;
import org.htmlparser.util.ParserException;

import media.config.Configuration;
import media.entity.Video;
import media.flvcd.Flvcd;
import media.net.HttpRequest;
import media.net.HttpRequest.HttpResult;

public class TencentFlvcd extends Flvcd {

	private String playListUrl = Configuration.get("tencent_play_list_url");
	private String[] videoQuality = { "", "hd", "shd" };

	@Override
	public Video getVideo(String playPageLink) throws ParserException, IOException, ParseException  {
		String vcode = getVideoCode(playPageLink);
		if (vcode == null || vcode.length() > 14)
			return null;
		Video video = getPlayList(vcode);
		NodeFilter filter = new LinkStringFilter("/vlive.qqvideo.tc.qq.com/");
		for (int i = 0; i < formats.length; ++i) {
			HashMap<Integer, String> urls = super.getVideoLinks(playPageLink,
					formats[i], filter);
			video.setDownloadUrls(types[i], urls);
		}
		return video;
	}

	private String getVideoCode(String playPageLink) {
		String vcode = playPageLink.substring(
				playPageLink.lastIndexOf("/") + 1,
				playPageLink.lastIndexOf("."));
		return vcode;
	}

	private Video getPlayList(String videoId) throws ClientProtocolException,
			IOException, ParseException {
		HashMap<String, String> jsons = new HashMap<String, String>();
		for (int i = 0; i < videoQuality.length; ++i) {
			HttpRequest request = new HttpRequest();
			request.addParam("vids", "m00139mfkmr");
			request.addParam("otype", "json");
			request.addParam("defaultfmt", videoQuality[i]);
			HttpResult result = request.post(playListUrl);
			if (result.code == 200) {
				String json = result.textResult.substring("QZOutputJson="
						.length());
				jsons.put(types[i], json);
			}
		}
		Video video = new TencentVideo(jsons);
		return video;
	}
}
