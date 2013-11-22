package media.video.platforms;

import java.io.IOException;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.util.ParserException;

import media.config.Configuration;
import media.entity.Video;
import media.flvcd.Flvcd;
import media.net.HttpRequest;
import media.net.HttpRequest.HttpResult;

public class LetvFlvcd extends Flvcd {
	private String playListUrl = Configuration.get("letv_play_list_url");

	@Override
	public Video getVideo(String playPageLink) throws ParserException,
			IOException, ParseException {
		String vcode = getVideoCode(playPageLink);
		if(vcode == null) return null;
		Video video = getPlayList(vcode);
		return video;
	}
	
	private String getVideoCode(String playPageLink){
		String vcode = playPageLink.substring(
				playPageLink.lastIndexOf("/") + 1,
				playPageLink.lastIndexOf("."));
		return vcode;
	}
	
	private Video getPlayList(String vid) throws IOException, ParseException{
		String url = playListUrl + vid + ".xml";
		HttpRequest request = new HttpRequest();
		HttpResult result = request.get(url);
		Pattern pattern = Pattern.compile("<playurl>(.+?)</playurl>");
		Matcher matcher = pattern.matcher(result.textResult);
		String playListJson = null;
		if(matcher.find()){
			playListJson = matcher.group(1);
		}
		if(playListJson.startsWith("<![CDATA[")){
			playListJson = playListJson.substring("<![CDATA[".length(), playListJson.length() - 3);
		}
		Video video = new LetvVideo(playListJson);
		return video;
	}

}
