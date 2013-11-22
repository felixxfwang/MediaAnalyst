package media.video.platforms;

import media.flvcd.Flvcd;

public class FlvcdFactory {

	public static Flvcd newFlvcd(Platform platform){
		if(platform == Platform.Tudou){
			return new TudouFlvcd();
		}else if(platform == Platform.Youku){
			return new YoukuFlvcd();
		}else if(platform == Platform.Tencent){
			return new TencentFlvcd();
		}else if(platform == Platform.Iqiyi){
			return new IqiyiFlvcd();
		}else if(platform == Platform.Sohu){
			return new SohuFlvcd();
		}else if(platform == Platform.Letv){
			return new LetvFlvcd();
		}else if(platform == Platform.Sina){
			return new SinaFlvcd();
		}else if(platform == Platform.Xunlei){
			return new XunleiFlvcd();
		}else {
			return new TudouFlvcd();
		}
	}
}
