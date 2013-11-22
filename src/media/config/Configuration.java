package media.config;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/***
 * ��ȡconfig.properties�����ļ�
 * @author Felix
 *
 */
public class Configuration {
	private static final String CONFIG_FILE_NAME = "config.properties";

	/**
	 * ����KEY����ȡ�ļ���Ӧ��ֵ
	 * 
	 * @param key ��
	 * 
	 * @return key��Ӧ��ֵ
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
	 * �޸Ļ���Ӽ�ֵ�� ���key���ڣ��޸�, ��֮����ӡ�
	 * 
	 * @param key
	 *            ��
	 * @param value
	 *            ����Ӧ��ֵ
	 */
	public static void store(String key, String value) {
		Properties prop = new Properties();
		try {
			InputStream fis = Configuration.class.getResourceAsStream(CONFIG_FILE_NAME);
			prop.load(fis);
			// һ��Ҫ���޸�ֵ֮ǰ�ر�fis
			fis.close();
			String path = ClassLoader.getSystemResource(CONFIG_FILE_NAME).toString().substring(6);
			OutputStream fos = new FileOutputStream(path);			
			prop.setProperty(key, value);
			// ���棬������ע��
			prop.store(fos, null);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
