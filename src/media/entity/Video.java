package media.entity;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import media.analyst.ResultProcesser;

public abstract class Video {
	protected String videoid;
	protected HashMap<String, List<VideoSegment>> segs;

	public void analyze(String type, ResultProcesser processer)
			throws IOException {
		analyze(type, processer, false);
	}

	public void analyze(String type, ResultProcesser processer, boolean waitPlay)
			throws IOException {
		if (segs.containsKey(type)) {
			List<VideoSegment> segments = segs.get(type);

			for (VideoSegment segment : segments) {
				System.out.println("type: " + segment.getType());
				segment.setProcesser(processer);
				segment.startAnalyzing(waitPlay);
			}
		}
	}

	public void setDownloadUrls(String type, HashMap<Integer, String> urls) {
		if (this.segs.containsKey(type)) {
			List<VideoSegment> segments = segs.get(type);
			for (VideoSegment seg : segments) {
				if (urls.containsKey(seg.getNo())) {
					seg.setDownloadUrl(urls.get(seg.getNo()));
				}
			}
		}
	}

	public String getVideoid() {
		return videoid;
	}

	public void setVideoid(String videoid) {
		this.videoid = videoid;
	}

	public HashMap<String, List<VideoSegment>> getSegs() {
		return segs;
	}

	public void setSegs(HashMap<String, List<VideoSegment>> segs) {
		this.segs = segs;
	}
}
