package media.entity;

public class VideoSegment extends AnalyzeUnit {
	private String type;
	private int no;
	private String k;
	private String k2;

	private int segs;
	
	public VideoSegment(String type, int no,long size,double seconds){
		super(size, seconds);
		this.type = type;
		this.no = no;
	}

	public VideoSegment(String type, int no, long size, double seconds,
			String k, String k2) {
		super(size, seconds);
		this.type = type;
		this.no = no;
		this.k = k;
		this.k2 = k2;
	}

	public boolean lastSegment() {
		return segs - no == 1;
	}

	public String getType() {
		return type;
	}

	public int getNo() {
		return no;
	}

	public int getSegs() {
		return segs;
	}

	public void setSegs(int segs) {
		this.segs = segs;
	}

	public String getK() {
		return k;
	}

	public String getK2() {
		return k2;
	}

	@Override
	public void finishDownload() {
		if (processer != null) {
			if (lastSegment()) {
				AnalyzeUnit.clear();
				processer.allEnd();
			} else {
				processer.segmentEnd();
			}
		}
	}
}
