package media.config;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/***
 * 读取config.properties配置文件
 * @author Felix
 *
 */
public class Configuration {
	private static final String CONFIG_FILE_NAME = "config.properties";

	/**
	 * 根据KEY，读取文件对应的值
	 * 
	 * @param key 键
	 * 
	 * @return key对应的值
	 */
	public static String get(String key) {
		Properties props = new Properties();
		InputStream in = Configuration.class.getResourceAsStream(CONFIG_FILE_NAME);
		String value = null;
		try {
			props.load(in);	
			in.close();
			value = props.getProperty(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	/**
	 * 修改或添加键值对 如果key存在，修改, 反之，添加。
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            键对应的值
	 */
	public static void store(String key, String value) {
		Properties prop = new Properties();
		try {
			InputStream fis = Configuration.class.getResourceAsStream(CONFIG_FILE_NAME);
			prop.load(fis);
			// 一定要在修改值之前关闭fis
			fis.close();
			String path = ClassLoader.getSystemResource(CONFIG_FILE_NAME).toString().substring(6);
			OutputStream fos = new FileOutputStream(path);			
			prop.setProperty(key, value);
			// 保存，并加入注释
			prop.store(fos, null);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
