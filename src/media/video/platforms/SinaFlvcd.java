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
	 * ͨ��������Ƶurl�����õ���Ƶvid
	 * @param playPageLink	��Ƶurl
	 * @return				������Ƶvids   vids[0]	������Ƶid
	 * 									vids[1] ������Ƶid  ֵΪ 0 ��ʾû�и�����Ƶ��Դ
	 * @throws IOException
	 * @throws ParserException
	 */
	private String[] getVideoID(String playPageLink) throws IOException,ParserException {
		
		String vids[] = null;
		//��һ����� 	��Ƶurl�д��� #
		if(playPageLink.contains("#")){
			vids = new String[2];
			vids[0] = playPageLink.substring(playPageLink.indexOf("#") + 1);
			vids[1] = "0";
		}
		//�ڶ������ 	��Ƶurl�� / ��β  ��������Ц��Ƶ �� ������ ����
		else if(playPageLink.endsWith("/")){
			vids = null;
		}
		//�������
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
	 * ͬ��������Ƶvid�õ����ܺ����Ƶ��Ϣxmlҳ���rul
	 * @param vid	��Ƶvid
	 * @return		��ƵԴ��Ϣxmlҳ��url
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
	 * MD5 ���� 
	 * @param str	��Ҫ���ܵ��ַ���
	 * @return		MD5���ܺ���ַ���
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
