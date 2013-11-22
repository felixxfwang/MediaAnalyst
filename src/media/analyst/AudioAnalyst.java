package media.analyst;

public interface AudioAnalyst {
	void startAudioAnalyst(String type,ResultProcesser processer,long ms);
	void startAudioAnalyst(String type,ResultProcesser processer,boolean waitPlay, long ms);	
	void addPlayPageLink(String link) ;
}
