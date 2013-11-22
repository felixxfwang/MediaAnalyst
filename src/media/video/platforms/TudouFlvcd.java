package media.video.platforms;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.ClientProtocolException;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.LinkStringFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import media.config.Configuration;
import media.net.HttpRequest;
import media.net.HttpRequest.HttpResult;
import media.entity.Video;
import media.flvcd.Flvcd;

public class TudouFlvcd extends Flvcd {
	private String playListUrl = Configuration.get("tudou_play_list_url");

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
		URL url = new URL(playPageLink);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		Parser htmlParser = new Parser(connection);
		NodeFilter filter = new TagNameFilter("script");
		NodeList nodes = htmlParser.extractAllNodesThatMatch(filter);
		Pattern pattern = Pattern.compile("vcode: \'(.+?)\'");
		String vcode = null;
		if (nodes != null) {
			for (int i = 0; i < nodes.size(); i++) {
				Node node = nodes.elementAt(i);
				String script = node.toPlainTextString();
				Matcher matcher = pattern.matcher(script);
				if (matcher.find()) {
					vcode = matcher.group(1);
					break;
				}
			}
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
