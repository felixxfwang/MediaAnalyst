package media.analyst;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.htmlparser.util.ParserException;

import media.entity.AnalyzeUnit;
import media.entity.Audio;
import media.entity.Video;
import media.flvcd.AudioFlvcd;
import media.flvcd.Flvcd;
import media.video.platforms.FlvcdFactory;
import media.video.platforms.Platform;

public class MediaAnalyst implements VideoAnalyst, AudioAnalyst {

	private List<String> playPageLinks;

	public MediaAnalyst() {
		playPageLinks = new ArrayList<String>();
	}

	@Override
	public void startAudioAnalyst(String type, ResultProcesser processer,
			long ms) {
		startAudioAnalyst(type, processer, false, ms);
	}

	@Override
	public void startVideoAnalyst(Platform platform, String type,
			ResultProcesser processer, long ms) {
		startVideoAnalyst(platform, type, processer, false, ms);
	}

	@Override
	public void startVideoAnalyst(final Platform platform, final String type,
			final ResultProcesser processer, final boolean waitPlay, long ms) {
		Runnable task = new Runnable() {
			@Override
			public void run() {
				Flvcd flvcd = FlvcdFactory.newFlvcd(platform);
				for (String link : playPageLinks) {
					try {
						Video video = flvcd.getVideo(link);
						video.analyze(type, processer, waitPlay);
					} catch (ParserException | IOException | ParseException e) {
						e.printStackTrace();
					}
				}
			}
		};
		startMediaAnalyst(task, processer, ms);
	}

	@Override
	public void startAudioAnalyst(final String type,
			final ResultProcesser processer, final boolean waitPlay, long ms) {
		Runnable task = new Runnable() {
			@Override
			public void run() {
				AudioFlvcd flvcd = new AudioFlvcd();
				for (String link : playPageLinks) {
					try {
						Audio audio = flvcd.getAudio(link);
						audio.analyze(type, processer, waitPlay);
					} catch (ParseException | IOException e) {
						e.printStackTrace();
					}
				}
			}
		};
		startMediaAnalyst(task, processer, ms);
	}

	@SuppressWarnings("deprecation")
	private void startMediaAnalyst(Runnable task, ResultProcesser processer,
			long ms) {
		Thread thread = new Thread(task);
		long initTime = System.currentTimeMillis();
		thread.start();
		if (ms > 0) {
			while (thread.isAlive()) {
				if (System.currentTimeMillis() - initTime >= ms) {
					thread.stop();
					AnalyzeUnit.clear();
					if (processer != null) {
						processer.allEnd();
					}
					System.out.println("Due");
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void addPlayPageLink(String link) {
		playPageLinks.add(link);
	}

	public static void main(String[] args) {
		ResultProcesser processer = new ResultProcesser() {

			@Override
			public void segmentEnd() {
				System.out.println("Segment Download Completed!");
			}

			@Override
			public void processVelocity(long velocity, long averageVelocity,
					long totalAverageVelocity, long bitrate, long fluency,
					long time) {
				System.out.println(time / 1000 + ": " + velocity + "kb/s,"
						+ averageVelocity + "kb/s, " + totalAverageVelocity
						+ "kb/s, bitrate:" + bitrate + "kb/s, fluency:"
						+ fluency);
			}

			@Override
			public void allEnd() {
				System.out.println("Media Download Completed!");
			}

			@Override
			public void sendDownloadIp(String downloadIp) {
				System.out.println("Download ip: " + downloadIp);
			}
		};

		String audioPageLink = "http://music.baidu.com/song/87603531";
		String videoPageLink =
		// "http://www.tudou.com/albumplay/ulKOjCIyn9g/72PAKWqvryU.html";
		// "http://v.youku.com/v_show/id_XNjM1OTA5MDg0.html";
		// "http://v.qq.com/cover/5/53x6bbyb07ebl3s/a0013janssn.html";
		// "http://www.letv.com/ptv/vplay/273253.html";
		// "http://tv.sohu.com/20131119/n390429989.shtml";
		"http://www.iqiyi.com/v_19rrh9mfio.html";
		AudioAnalyst audioAnalyst = new MediaAnalyst();
		audioAnalyst.addPlayPageLink(audioPageLink);
		VideoAnalyst videoAnalyst = new MediaAnalyst();
		videoAnalyst.addPlayPageLink(videoPageLink);

		videoAnalyst.startVideoAnalyst(Platform.Iqiyi, "mp4", processer,
				1000000);
		// audioAnalyst.startAudioAnalyst("320", processer, 10000);
	}
}
