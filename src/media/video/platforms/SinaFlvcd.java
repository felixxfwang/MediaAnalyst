package media.video.platforms;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import media.config.Configuration;
import media.entity.Video;
import media.flvcd.Flvcd;
import media.net.HttpRequest;
import media.net.HttpRequest.HttpResult;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class SinaFlvcd extends Flvcd {
	private String sinaParseUrl = Configuration.get("sina_play_list_url");
	private String[] types = { "flv", "mp4"};

	@Override
	public Video getVideo(String playPageLink) throws ParserException,
			IOException, ParseException {
		// TODO Auto-generated method stub
		String vids[] = getVideoID(playPageLink);
		if (vids == null)
			return null;
		SinaVideo sinaVideo = new SinaVideo(vids);
		for(int i = 0; i < vids.length; i++){
			if(!vids[i].equals("0")){
				String xml_url = getSinaVideoInfoUrl(vids[i]);
				HttpRequest request = new HttpRequest();
				HttpResult result = request.get(xml_url);
				sinaVideo.setVideoInfo(types[i], result.textResult);
			}
		}
		return (Video)sinaVideo;
	}
	
	/**
	 * 通过新浪视频url解析得到视频vid
	 * @param playPageLink	视频url
	 * @return				返回视频vids   vids[0]	标清视频id
	 * 									vids[1] 高清视频id  值为 0 表示没有高清视频资源
	 * @throws IOException
	 * @throws ParserException
	 */
	private String[] getVideoID(String playPageLink) throws IOException,ParserException {
		
		String vids[] = null;
		//第一种情况 	视频url中带有 #
		if(playPageLink.contains("#")){
			vids = new String[2];
			vids[0] = playPageLink.substring(playPageLink.indexOf("#") + 1);
			vids[1] = "0";
		}
		//第二种情况 	视频url以 / 结尾  （包含搞笑视频 和 公开课 两类
		else if(playPageLink.endsWith("/")){
			vids = null;
		}
		//其他情况
		else{
			URL url = new URL(playPageLink);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			Parser htmlParser = new Parser(connection);
			NodeFilter filter = new TagNameFilter("script");
			NodeList nodes = htmlParser.extractAllNodesThatMatch(filter);
			Pattern pattern = Pattern.compile("vid:\'(.+?)\'");
			String videoID = null;
			if (nodes != null) {
				for (int i = 0; i < nodes.size(); i++) {
					Node node = nodes.elementAt(i);
					String script = node.toPlainTextString();
					Matcher matcher = pattern.matcher(script);
					if (matcher.find()) {
						videoID = matcher.group(1);
						break;
					}
				}
			}
			if(videoID != null){
				vids = new String[2];
				if(videoID.contains("|")){
					vids[0] = videoID.substring(0,videoID.indexOf("|"));
					vids[1] = videoID.substring(videoID.indexOf("|")+1, videoID.length());
				}else{
					vids[0] = videoID;
					vids[1] = "0";
				}
			}
		}
		return vids;
	}
	
	/**
	 * 同过新浪视频vid得到加密后的视频信息xml页面的rul
	 * @param vid	视频vid
	 * @return		视频源信息xml页面url
	 */
	private String getSinaVideoInfoUrl(String vid){
		
		Random random = new Random();
		int ran = random.nextInt(1000)+1;
        
        long time = System.currentTimeMillis()/1000;
        String times = Integer.toBinaryString((int) time);
        times = times.substring(0, times.length()-6);
        times = Integer.valueOf(times,2).toString();
        
        String key = vid + "Z6prk18aWxP278cVAH" + times + ran;
        key = getMD5Str(key);
        key = key.substring(0, 16);
        key = key + times;
        
        String xml_url = sinaParseUrl + "?" + 
                "vid=" + vid + 
                "&uid=null" +
                "&pid=null" +
                "&tid=undefined" +
                "&plid=4001" +
                "&prid=ja_7_4993252847" +
                "&referrer=" +
                "&ran=" + ran +
                "&r=video.sina.com.cn" +
                "&v=p2p4.1.42.23" +
                "&p=i" +
                "&k=" + key;
        return xml_url;
	}
	
	/**
	 * MD5 加密 
	 * @param str	需要加密的字符串
	 * @return		MD5加密后的字符串
	 */
    private String getMD5Str(String str) {  
        MessageDigest messageDigest = null;  
  
        try {  
            messageDigest = MessageDigest.getInstance("MD5");  
  
            messageDigest.reset();  
  
            messageDigest.update(str.getBytes("UTF-8"));  
        } catch (NoSuchAlgorithmException e) {  
            System.out.println("NoSuchAlgorithmException caught!");  
            System.exit(-1);  
        } catch (UnsupportedEncodingException e) {  
            e.printStackTrace();  
        }  
  
        byte[] byteArray = messageDigest.digest();  
  
        StringBuffer md5StrBuff = new StringBuffer();  
  
        for (int i = 0; i < byteArray.length; i++) {              
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)  
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));  
            else  
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));  
        }  
  
        return md5StrBuff.toString();  
    }  

}
