package com.dance.cache.lib;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.Properties;
import android.content.Context;

/**
 * 缓存引擎，负责提供存取数据的方法，设置缓存目录,缓存大小
 * 
 * @author dance
 * 
 */
public class DCache {
	private static DCache mInstance = null;
	private File mCacheDir;
	private long mCacheSize = 1024 * 1024 * 60;// 默认为60MB
	private long mCacheDuration = 1000 * 60 * 60 * 2;// 缓存周期默认为2小时
	// 用来存储所有缓存文件的文件名和有效期的映射
	private Properties mCacheProperties;
	private final String CACHE_PROPERTIES = "cache.properties";

	public static DCache get(Context context) {
		if (mInstance == null) {
			mInstance = new DCache(context);
		}
		return mInstance;
	}

	private DCache(Context context) {
		// init default cacheDir
		mCacheDir = context.getFilesDir();
	}

	/**
	 * 设置缓存目录
	 * 
	 * @param cacheDir
	 * @return
	 */
	public DCache cacheDir(File cacheDir) {
		this.mCacheDir = cacheDir;
		if (!mCacheDir.exists() && cacheDir.isDirectory()) {
			mCacheDir.mkdirs();
		}
		initCacheProperties();
		
		return mInstance;
	}

	/**
	 * 设置缓存大小
	 * 
	 * @param cacheDir
	 * @return
	 */
	public DCache cacheSize(long cacheSize) {
		mCacheSize = cacheSize;
		return mInstance;
	}

	/**
	 * 设置缓存大小
	 * 
	 * @param cacheDir
	 * @return
	 */
	public DCache cacheDuration(long cacheDuration) {
		mCacheDuration = cacheDuration;
		return mInstance;
	}

	public File getCacheDir(){
		return mCacheDir;
	}
	
	/**
	 * 初始化cacheMap
	 */
	private void initCacheProperties() {
		mCacheProperties = new Properties();
		File propertyFile = newFile(CACHE_PROPERTIES);
		try {
			FileInputStream fis = new FileInputStream(propertyFile);
			mCacheProperties.load(fis);
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 检查是否初始化话CacheProperties
	 */
	private void checkInitProperties(){
		if(mCacheProperties==null){
			initCacheProperties();
		}
	}

	public void putString(String key, String data) {
		putString(key, data, mCacheDuration);
	}

	/**
	 * 缓存字符串数据，比如：json,xml
	 * 
	 * @param key
	 * @param data
	 */
	public void putString(String key, String data, long cacheDuration) {
		checkInitProperties();
		
		File file = newFile(key);
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(file), 1024);
			out.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.flush();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			mCacheProperties.setProperty(key, String.valueOf(cacheDuration));
			storeProperties();
		}
	}

	/**
	 * 获取字符串类型的数据
	 * 
	 * @param key
	 * @return
	 */
	public String getString(String key) {
		if (isInvalid(key)) {
			clearInvalidCache(key);
			return null;
		} else {
			RandomAccessFile raf = null;
			byte[] bytes = null;
			try {
				raf = new RandomAccessFile(newFile(key),"r");
				bytes = new byte[(int) raf.length()];
				raf.read(bytes);
				return new String(bytes,"UTF-8");
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				try {
					raf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return null;
		}
	}

	public void putObject(String key, Serializable obj) {
		putObject(key, obj, mCacheDuration);
	}

	/**
	 * 缓存Object类型的数据
	 * 
	 * @param key
	 * @param obj
	 */
	public void putObject(String key, Serializable obj, long cacheDuration) {
		checkInitProperties();
		
		File file = newFile(key);
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = new FileOutputStream(file);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(obj);
			oos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
				oos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			mCacheProperties.setProperty(key, String.valueOf(cacheDuration));
			storeProperties();
		}

	}

	/**
	 * 获取Object类型的数据
	 * 
	 * @param key
	 * @return
	 */
	public Serializable getObject(String key) {
		if (isInvalid(key)) {
			clearInvalidCache(key);
			return null;
		} else {
			FileInputStream fis = null;
			ObjectInputStream ois = null;
			try {
				fis = new FileInputStream(newFile(key));
				ois = new ObjectInputStream(fis);
				return (Serializable) ois.readObject();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					fis.close();
					ois.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;
		}
	}

	/**
	 * 缓存bytes数据
	 * 
	 * @param key
	 * @param bytes
	 */
	public void putBytes(String key, byte[] bytes, long cacheDuration) {
		checkInitProperties();
		
		File file = newFile(key);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			out.write(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.flush();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			mCacheProperties.setProperty(key, String.valueOf(cacheDuration));
			storeProperties();
		}
	}

	public void putBytes(String key, byte[] bytes) {
		putBytes(key, bytes, mCacheDuration);
	}

	/**
	 * 获取缓存的字节数据
	 * @param key
	 * @return
	 */
	public byte[] getBytes(String key) {
		if (isInvalid(key)) {
			clearInvalidCache(key);
			return null;
		} else {
			RandomAccessFile raf = null;
			byte[] bytes = null;
			try {
				raf = new RandomAccessFile(newFile(key),"r");
				bytes = new byte[(int) raf.length()];
				raf.read(bytes);
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				try {
					raf.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return bytes;
		}
	}
	/**
	 * 清除无效的缓存数据
	 * @param key
	 */
	private void clearInvalidCache(String key){
		deleteFile(key);// 删除文件
		mCacheProperties.remove(key);// 清除映射
		storeProperties();
	}

	/**
	 * 保存Properties
	 */
	private void storeProperties() {
		try {
			mCacheProperties.store(new FileWriter(newFile(CACHE_PROPERTIES)), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 清除缓存数据
	 */
	public void clearCache(){
		File[] files = mCacheDir.listFiles();
		if (files != null) {
			for (File f : files) {
				f.delete();
			}
		}
		mCacheProperties.clear();
	}

	/**
	 * 判断指定文件是否已经失效
	 * 
	 * @param key
	 * @return
	 */
	private boolean isInvalid(String key) {
		File file = newFile(key);
		long existDuration = System.currentTimeMillis() - file.lastModified();
		long cacheDuration = Long.parseLong(mCacheProperties.getProperty(key,
				"0"));
		return existDuration > cacheDuration;
	}

	private File newFile(String key) {
		File file = new File(mCacheDir, key);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}
	private void deleteFile(String key) {
		File file = new File(mCacheDir, key);
		if (file.exists()) {
			file.delete();
		}
	}

}
