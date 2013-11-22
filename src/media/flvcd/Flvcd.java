package media.flvcd;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import media.config.Configuration;
import media.entity.Video;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public abstract class Flvcd {
	private String parseUrl = Configuration.get("flvcd_parse_url");
	protected String[] formats = { "", "high", "super" };
	protected String[] types = { "flv", "mp4", "hd2" };
	private int number = 0;

	public abstract Video getVideo(String playPageLink) throws ParserException,
			IOException, ParseException;

	protected HashMap<Integer, String> getVideoLinks(String playPageLink,
			String format, NodeFilter filter) throws ParserException,
			IOException {
		String urlStr = parseUrl + "?kw=" + playPageLink + "&format=" + format;
		URL url = new URL(urlStr);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		String charset = connection.getContentEncoding();
		charset = charset != null ? charset : "GB2312";
		Parser htmlParser = new Parser(connection);
		htmlParser.setEncoding(charset);
		NodeList nodes = htmlParser.extractAllNodesThatMatch(filter);
		Pattern pattern = Pattern.compile("href=\"(.+?)\"");
		HashMap<Integer, String> videoUrls = new HashMap<Integer, String>();
		if (nodes != null) {
			for (int i = 0; i < nodes.size(); i++) {
				Node node = nodes.elementAt(i);
				String nodeText = node.getText();
				Matcher matcher = pattern.matcher(nodeText);
				if (matcher.find()) {
					String dUrl = matcher.group(1);
					int no = extratNumber(dUrl);
					videoUrls.put(no, dUrl);
				}
			}
		}
		number = 0;
		return videoUrls;
	}

	protected int extratNumber(String downloadUrl) {
		return number++;
	}
}
