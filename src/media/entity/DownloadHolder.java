package media.entity;

public interface DownloadHolder {
	void sendVelocity(long size,long velocity,long averageVelocity,long time);
	void finishDownload();
	void sendDownloadIp(String ip);
	void sendContentLength(long fileSize);
}
