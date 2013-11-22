package media.entity;

public class AudioLinkInfo extends AnalyzeUnit {
	private int bitrate;
	private String format;

	public AudioLinkInfo(int rate, int time, long size, String format,
			String songLink) {
		super(size,time);
		super.setDownloadUrl(songLink);
		this.bitrate = rate;
		this.format = format;
	}

	public int getBitrate() {
		return bitrate;
	}

	public String getFormat(){
		return format;
	}

	@Override
	public void finishDownload() {
		if(processer != null){
			AnalyzeUnit.clear();
			processer.allEnd();
		}
	}

}
