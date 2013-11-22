package media.analyst;

public interface ResultProcesser {
	/**
	 * �����ض�ʱ���µĸ��ּ��ָ��
	 * @param velocity ˲ʱ����
	 * @param averageVelocity ƽ������
	 * @param totalAverageVelocity �ܵ�ƽ����������
	 * @param bitrate ����
	 * @param fluency ������
	 * @param time ��ǰʱ��
	 */
	void processVelocity(long velocity,long averageVelocity,long totalAverageVelocity,long bitrate, long fluency, long time);
	void sendDownloadIp(String downloadIp);
	void segmentEnd();
	void allEnd();
}
