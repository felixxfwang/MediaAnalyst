package media.video.platforms;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.ClientProtocolException;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import media.config.Configuration;
import media.entity.Video;
import media.flvcd.Flvcd;
import media.net.HttpRequest;
import media.net.HttpRequest.HttpResult;

public class IqiyiFlvcd extends Flvcd {

	private String playListUrl = Configuration.get("iqiyi_play_list_url");

	@Override
	public Video getVideo(String playPageLink) throws ParserException,
			IOException, ParseException {
		VidTvid vcode = getVideoCode(playPageLink);
		if (vcode == null)
			return null;
		Video video = getPlayList(vcode);		
		return video;
	}

	private VidTvid getVideoCode(String playPageLink) throws IOException,
			ParserException {
		URL url = new URL(playPageLink);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		Parser htmlParser = new Parser(connection);
		NodeFilter tvidFilter = new HasAttributeFilter("data-player-tvid");
		NodeFilter vidFilter = new HasAttributeFilter("data-player-videoid");
		NodeFilter filter = new AndFilter(tvidFilter,vidFilter);
		NodeList nodes = htmlParser.extractAllNodesThatMatch(filter);
		Pattern pattern = Pattern.compile("data-player-tvid=\"(.+?)\"");
		Pattern pattern2 = Pattern.compile("data-player-videoid=\"(.+?)\"");
		VidTvid vcode = new VidTvid();
		if (nodes != null) {
			for (int i = 0; i < nodes.size(); i++) {
				Node node = nodes.elementAt(i);
				String div = node.getText();
				Matcher matcher = pattern.matcher(div);
				if (matcher.find()) {
					vcode.tvid = matcher.group(1);
				}
				Matcher matcher2 = pattern2.matcher(div);
				if (matcher2.find()) {
					vcode.vid = matcher2.group(1);
					break;
				}
			}
		}
		return vcode;
	}

	private Video getPlayList(VidTvid vcode) throws ClientProtocolException,
			IOException, ParseException {
		HttpRequest request = new HttpRequest();
		String url = playListUrl + vcode.tvid + "/" + vcode.vid + "/";
		HttpResult result = request.get(url);
		Video video = new IqiyiVideo(result.textResult);
		return video;
	}
	
	class VidTvid{
		public String vid;
		public String tvid;
	}

}
