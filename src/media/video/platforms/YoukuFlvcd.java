package media.video.platforms;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.ClientProtocolException;
import org.htmlparser.NodeFilter;
import org.htmlparser.filters.LinkStringFilter;
import org.htmlparser.util.ParserException;

import media.config.Configuration;
import media.entity.Video;
import media.flvcd.Flvcd;
import media.net.HttpRequest;
import media.net.HttpRequest.HttpResult;

public class YoukuFlvcd extends Flvcd {

	private String playListUrl = Configuration.get("youku_play_list_url");

	@Override
	public Video getVideo(String playPageLink) throws ParserException,
			IOException, ParseException {
		String vcode = getVideoCode(playPageLink);
		if (vcode == null)
			return null;
		Video video = getPlayList(vcode);
		NodeFilter filter = new LinkStringFilter(
				"f.youku.com/player/getFlvPath/");
		for (int i = 0; i < formats.length; ++i) {
			HashMap<Integer, String> urls = super.getVideoLinks(playPageLink,
					formats[i], filter);
			video.setDownloadUrls(types[i], urls);
		}
		return video;
	}

	private String getVideoCode(String playPageLink) throws IOException,
			ParserException {
		String vcode = null;
		Pattern pattern = Pattern.compile("/id_(.+?).html");
		Matcher matcher = pattern.matcher(playPageLink);
		if (matcher.find()) {
			vcode = matcher.group(1);
		}
		return vcode;
	}

	private Video getPlayList(String videoId) throws ClientProtocolException,
			IOException, ParseException {
		HttpRequest request = new HttpRequest();
		HttpResult result = request.get(playListUrl + videoId);
		Video video = new TudouVideo(result.textResult);
		return video;
	}

	@Override
	protected int extratNumber(String downloadUrl) {
		String fileId = downloadUrl.substring(downloadUrl.indexOf("fileid/")
				+ "fileid/".length());
		String number = fileId.substring(8, 10);
		int no = Integer.parseInt(number, 16);
		return no;
	}

}
