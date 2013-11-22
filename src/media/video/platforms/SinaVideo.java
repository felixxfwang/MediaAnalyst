package media.video.platforms;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import media.entity.Video;
import media.entity.VideoSegment;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.InputSource;

public class SinaVideo extends Video{
	private String hdVideoid;
	private long timelength = -1;
	private long framecount = -1;
	private String vname = null;
	
	private HashMap<Integer, Long> segsLength = null;
	HashMap<String, List<VideoSegment>> segs = null;
	
	public SinaVideo(String[] vids){
		super.setVideoid(vids[0]);
		this.hdVideoid = vids[1];
		segs = new HashMap<String, List<VideoSegment>>();
	}
	
	public void setVideoInfo(String type, String xmlResult){
		
		HashMap<Integer, String> segsUrl = new HashMap<Integer, String>();
		HashMap<Integer, Long> segsLength = new HashMap<Integer, Long>();
		
		List<VideoSegment> value = new ArrayList<VideoSegment>();
		//����һ���µ��ַ���
        StringReader read = new StringReader(xmlResult);
        //�����µ�����ԴSAX ��������ʹ�� InputSource ������ȷ����ζ�ȡ XML ����
        InputSource source = new InputSource(read);
        //����һ���µ�SAXBuilder
        SAXBuilder sb = new SAXBuilder();
        try {
            //ͨ������Դ����һ��Document
            Document doc = sb.build(source);
            //ȡ�ĸ�Ԫ��
            Element root = doc.getRootElement();
            
            long timelength = Long.parseLong(root.getChildText("timelength"));
            long framecount = Long.parseLong(root.getChildText("framecount"));
            String vname = root.getChildText("vname");
            
            List<Element> durlNode = root.getChildren("durl");
            Element et = null;
            for(int i = 0; i < durlNode.size(); i++){
            	et = durlNode.get(i);
            	int order = Integer.parseInt(et.getChildText("order"))-1;
            	long length = (long)Long.parseLong(et.getChildText("length"))/1000;
            	String url = et.getChildText("url");
            	
            	segsLength.put(order, length);
            	segsUrl.put(order, url);
            	
            	VideoSegment v = new VideoSegment(type, order, -1, (double)length, null,
						null);
                v.setSegs(durlNode.size());
				value.add(v);
            }
            segs.put(type, value);            
            
            //���������ֵ
            
            super.setSegs(segs);
            
            if(this.timelength < 0){
            	this.timelength = timelength;
            }
            if(this.framecount < 0){
            	this.framecount = framecount;
            }
            if(this.vname == null){
            	this.vname = vname;
            }
            if(this.segsLength == null){
            	this.segsLength = segsLength;
            }
            //�����Ƶ��ַurl
            this.setDownloadUrls(type, segsUrl);
           
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public String getHdVideoid(){
		return this.hdVideoid;
	}
	public void setHdVideoid(String hd_vid){
		this.hdVideoid = hd_vid;
	}
	public long getTimelength(){
		return this.timelength;
	}
	
	public long getFramecount(){
		return this.framecount;
	}
	
	public String getVname(){
		return this.vname;
	}

}
