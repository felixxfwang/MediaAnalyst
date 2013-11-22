package media.analyst;

public interface ResultProcesser {
	/**
	 * 处理特定时间下的各种监测指标
	 * @param velocity 瞬时速率
	 * @param averageVelocity 平均速率
	 * @param totalAverageVelocity 总的平均下载速率
	 * @param bitrate 码率
	 * @param fluency 流畅度
	 * @param time 当前时间
	 */
	void processVelocity(long velocity,long averageVelocity,long totalAverageVelocity,long bitrate, long fluency, long time);
	void sendDownloadIp(String downloadIp);
	void segmentEnd();
	void allEnd();
}
