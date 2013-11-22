package media.entity;

import java.io.IOException;

import media.analyst.ResultProcesser;
import media.net.HttpDownloadHelper;

public abstract class AnalyzeUnit implements DownloadHolder {
	protected long size;
	protected double seconds;

	protected String downloadUrl;
	protected ResultProcesser processer;
	HttpDownloadHelper helper;

	protected boolean analyzing = false;
	private long playTime = 0;
	private long lastTime = 0;
	
	/*
	 * 用于手动停止分析
	 */
//	public static boolean stop = false;
//	private long stopTime;

	/*
	 * 计算总的平均下载速率用的变量
	 */
	protected static long totalSize = 0;
	protected static long totalTime = 0;
	protected static long totalVelocity = 0;

	protected AnalyzeUnit(long size, double seconds) {
		this.size = size;
		this.seconds = seconds;
	}

	public long playVelocity() {
		return (long) (size / (1000 * seconds));
	}

	/***
	 * 进行本单元的分析
	 * 
	 * @return 下载用时
	 */
	public boolean startAnalyzing(boolean waitPlay) throws IOException {		
		if (analyzing) {
			return false;
		}
		helper = new HttpDownloadHelper();
		helper.setHolder(this);
		analyzing = true;
		helper.downloadByUrl(downloadUrl);

		/*
		 * 如果等待播放，就模拟等待播放的过程
		 */
		if (waitPlay) {
			long remainTime = (long) (seconds * 1000) - lastTime;
			long averageVelocity = size / lastTime;
			long initTime = System.currentTimeMillis();
			long deltaTime = 0;
			while (deltaTime <= remainTime) {
				if (processer != null) {
					processer.processVelocity(0, averageVelocity,
							totalVelocity, playVelocity(), remainTime, lastTime
									+ deltaTime);
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				deltaTime = System.currentTimeMillis() - initTime;
			}
		}

		analyzing = false;
		return true;
	}

	public boolean analyzing() {
		return this.analyzing;
	}

	public long getSize() {
		return this.size;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public void setProcesser(ResultProcesser processer) {
		this.processer = processer;
	}
	
	public static void clear(){
		totalSize = 0;
		totalTime = 0;
	}

	@Override
	public void sendVelocity(long size, long velocity, long averageVelocity,
			long time) {
		if (processer != null) {
			long bitrate = playVelocity();
			long fluency = time * averageVelocity / bitrate - playTime;
			if (fluency <= 0) {
				fluency = 0;
			} else {
				playTime += time - lastTime;
			}
			totalTime += time - lastTime;
			totalSize += size;
			totalVelocity = totalSize / totalTime;
			lastTime = time;
			processer.processVelocity(velocity, averageVelocity, totalVelocity,
					bitrate, fluency, time);
		}
	}

	@Override
	public abstract void finishDownload();

	@Override
	public void sendDownloadIp(String ip) {
		if (processer != null) {
			processer.sendDownloadIp(ip);
		}
	}

	@Override
	public void sendContentLength(long length) {
		if (size != length) {
			System.out.println("Modify Size from " + size + " to " + length);
			size = length;
		}
	}
}
