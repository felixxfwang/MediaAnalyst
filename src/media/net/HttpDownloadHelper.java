package media.net;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import media.config.Configuration;
import media.entity.DownloadHolder;

public class HttpDownloadHelper {

	private final int BUFFER_SIZE;
	private DownloadHolder holder;

	/* 计算下载速度相关 */
	int totalDownloadSize = 0;
	int lastDownloadSize = 0;
	long lastCheckTime = 0;
	long startDownloadTime = 0;

	public HttpDownloadHelper() {
		BUFFER_SIZE = Integer.parseInt(Configuration
				.get("download_buffer_size"));
	}

	public void downloadByUrl(String address) throws IOException {
		if (address == null)
			return;
		int size = 0;
		byte[] buf = new byte[BUFFER_SIZE];

		/* 下载和计算过程 */
		URL url = new URL(address);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());

		System.out.println("File：" + address + " start download");
		String ip = conn.getRequestProperty("Host");
		holder.sendDownloadIp(ip);
		int fileSize = conn.getContentLength();
		holder.sendContentLength((long) fileSize);
		System.out.println("File Size：" + fileSize / 1024 + "KB");

		startDownloadTime = System.currentTimeMillis();
		lastCheckTime = startDownloadTime;

		while ((size = bis.read(buf)) != -1) {
			totalDownloadSize += size;
			calculateVelocity(1000);
		}
		calculateVelocity(0);
		totalDownloadSize = 0;
		lastDownloadSize = 0;
		lastCheckTime = 0;
		startDownloadTime = 0;

		if (holder != null) {
			holder.finishDownload();
		}
		bis.close();
	}

	/*
	 * 计算每秒下载速度
	 */
	private void calculateVelocity(long minDeltaTime) {
		long currentTime = System.currentTimeMillis();
		long deltaTime = currentTime - lastCheckTime;
		long totalDeltaTime = currentTime - startDownloadTime;
		if (deltaTime > minDeltaTime) {
			long deltaSize = totalDownloadSize - lastDownloadSize;
			long velocity = deltaSize / deltaTime;
			long averageVelocity = totalDownloadSize / totalDeltaTime;
			if (holder != null) {
				holder.sendVelocity(deltaSize, velocity, averageVelocity,
						totalDeltaTime);
			}
			lastCheckTime = currentTime;
			lastDownloadSize = totalDownloadSize;
		}
	}

	public void setHolder(DownloadHolder holder) {
		this.holder = holder;
	}

}
