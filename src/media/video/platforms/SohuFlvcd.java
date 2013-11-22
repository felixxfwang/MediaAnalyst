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
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.json.JSONObject;

import media.config.Configuration;
import media.entity.Video;
import media.flvcd.Flvcd;
import media.net.HttpRequest;
import media.net.HttpRequest.HttpResult;

public class SohuFlvcd extends Flvcd {

	private String playListUrl = Configuration.get("sohu_play_list_url");

	@Override
	public Video getVideo(String playPageLink) throws ParserException,
			IOException, ParseException {
		VidPid vcode = getVideoCode(playPageLink);
		Video video = getPlayList(vcode);
		return video;
	}

	private VidPid getVideoCode(String playPageLink) throws IOException,
			ParserException {
		URL url = new URL(playPageLink);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		Parser htmlParser = new Parser(connection);
		String charset = connection.getContentEncoding();
		charset = charset != null ? charset : "GBK";
		htmlParser.setEncoding(charset);
		NodeFilter filter = new TagNameFilter("script");
		NodeList nodes = htmlParser.extractAllNodesThatMatch(filter);
		Pattern pattern = Pattern.compile("var vid=\"(.+?)\";");
		Pattern patternPid = Pattern.compile("var pid =\"(.+?)\";");
		VidPid vcode = new VidPid();
		if (nodes != null) {
			for (int i = 0; i < nodes.size(); i++) {
				Node node = nodes.elementAt(i);
				String script = node.toPlainTextString();
				Matcher matcher = pattern.matcher(script);
				if (matcher.find()) {
					vcode.vid = matcher.group(1);
				}
				Matcher matcher2 = patternPid.matcher(script);
				if (matcher2.find()) {
					vcode.pid = matcher2.group(1);
					break;
				}
			}
		}
		return vcode;
	}

	private Video getPlayList(VidPid vcode) throws ClientProtocolException,
			IOException, ParseException {
		HttpRequest request = new HttpRequest();
		String url = playListUrl + "?vid=" + vcode.vid + "&pid=" + vcode.pid;
		HttpResult result = request.get(url);
		String[] vids = getVids(result.textResult);
		HashMap<String, String> jsons = new HashMap<String, String>();
		for(int i = 0;i < vids.length;++i){
			String infoUrl = playListUrl + "?vid=" + vids[i] + "&pid=" + vcode.pid;
			HttpResult r = request.get(infoUrl);
			jsons.put(types[i], r.textResult);
		}
		Video video = new SohuVideo(jsons);
		return video;
	}
	
	private String[] getVids(String json) throws ParseException{
		JSONObject obj = new JSONObject(json);
		JSONObject data = obj.getJSONObject("data");
		String[] vids = new String[3];
		vids[0] = data.getString("norVid");
		vids[1] = data.getString("highVid");
		vids[2] = data.getString("superVid");
		return vids;
	}

	class VidPid {
		public String vid;
		public String pid;
	}

}
