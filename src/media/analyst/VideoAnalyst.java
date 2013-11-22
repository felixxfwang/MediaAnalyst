package media.analyst;

import media.video.platforms.Platform;

public interface VideoAnalyst {
	void startVideoAnalyst(Platform platform, String type, ResultProcesser processer,long ms);
	void startVideoAnalyst(Platform platform, String type, ResultProcesser processer,boolean waitPlay,long ms);	
	void addPlayPageLink(String link);
}
