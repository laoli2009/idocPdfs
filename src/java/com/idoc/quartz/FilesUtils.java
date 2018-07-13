package com.idoc.quartz;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FilesUtils {
	
	public static List<FileYearRecoder> getFileYearRecoder(String fileDir){
		List<String> listname = Lists.newArrayList() ;
		readAllFile(fileDir, listname);
		Map<String , Map<String , List<String>>> fileMaps = Maps.newHashMap() ;
		for(String name : listname){
			try{
				String str = name.replace(fileDir+"/", "") ;
				String year = str.substring(0 , 4) ;
				String month = str.substring(5 , 7) ;
				Integer.valueOf(year) ;
				Integer.valueOf(month) ; 
				String fileName = str.substring(8) ;
				if(! fileMaps.containsKey(year)){
					fileMaps.put(year, Maps.newHashMap()) ;
				}
				if(! fileMaps.get(year).containsKey(month)){
					fileMaps.get(year).put(month, Lists.newArrayList()) ;
				}
				fileMaps.get(year).get(month).add(fileName) ; 
			}catch(Exception e){
				
			}
		}
		List<FileYearRecoder> recoders = Lists.newArrayList() ;
		for(Map.Entry<String , Map<String , List<String>>> en : fileMaps.entrySet()){
			FileYearRecoder fileYearRecoder = new FileYearRecoder() ;
			fileYearRecoder.setYear(en.getKey() );
			Map<String , List<String>> monthValue = en.getValue() ;
			for(Map.Entry<String , List<String>> mev : monthValue.entrySet()){
				FileMonthRecoder fileMonthRecoder = new FileMonthRecoder() ;
				fileMonthRecoder.setMonth(mev.getKey()) ;
				fileMonthRecoder.setFileName(mev.getValue()) ;
				fileYearRecoder.getMonths().add(fileMonthRecoder) ;
			}
			Collections.sort(fileYearRecoder.getMonths())  ;
			recoders.add(fileYearRecoder) ; 
		}
		Collections.sort(recoders) ;
		return recoders ;
	}

	public static void main(String[] args) throws IOException {
		String dir = "E:/ly/jsuan/BSP" ; 
		System.out.println(JSONObject.toJSONString(FilesUtils.getFileYearRecoder(dir)));
	} 
	/**
	 * zip压缩文件
	 * @param dir
	 * @param zippath
	 */
	public static void zip(String dir ,String zippath){
		List<String> paths = getFiles(dir); 
		compressFilesZip(paths.toArray(new String[paths.size()]),zippath,dir	);
	}
	/**
	 * 递归取到当前目录所有文件
	 * @param dir
	 * @return
	 */
	public static List<String> getFiles(String dir){
		List<String> lstFiles = null;		
		if(lstFiles == null){
			lstFiles = new ArrayList<String>();
		}
		File file = new File(dir);
		File [] files = file.listFiles(); 
		for(File f : files){
			if(f.isDirectory()){
				lstFiles.add(f.getAbsolutePath());
				lstFiles.addAll(getFiles(f.getAbsolutePath()));
			}else{ 
				String str =f.getAbsolutePath();
				lstFiles.add(str);
			}
		}
		return lstFiles;
	}
	public static void readAllFile(String filepath , List<String> listname){  
        File file= new File(filepath);  
        if(! file.isDirectory()){  
            listname.add(filepath);  
        }
        else{  
            String[] filelist = file.list() ;  
            for(String fileName : filelist){  
                readAllFile(filepath + "/" + fileName , listname);//递归  
            }  
        }  
	 }
	
	/**
	 * 文件名处理
	 * @param dir
	 * @param path
	 * @return
	 */
	public static String getFilePathName(String dir,String path){
		String p = path.replace(dir+File.separator, "");
		p = p.replace("\\", "/");
		return p;
	}
    /**
     * 把文件压缩成zip格式
     * @param files         需要压缩的文件
     * @param zipFilePath 压缩后的zip文件路径   ,如"D:/test/aa.zip";
     */
    public static void compressFilesZip(String[] files,String zipFilePath,String dir) {
        if(files == null || files.length <= 0) {
            return ;
        }
        ZipArchiveOutputStream zaos = null;
        try {
            File zipFile = new File(zipFilePath);
            zaos = new ZipArchiveOutputStream(zipFile);
            zaos.setUseZip64(Zip64Mode.AsNeeded);
            //将每个文件用ZipArchiveEntry封装
            //再用ZipArchiveOutputStream写到压缩文件中
            for(String strfile : files) {
            	File file = new File(strfile);
                if(file != null) {
                	String name = getFilePathName(dir,strfile);
                    ZipArchiveEntry zipArchiveEntry  = new ZipArchiveEntry(file,name);
                    zaos.putArchiveEntry(zipArchiveEntry);
                    if(file.isDirectory()){
                    	continue;
                    }
                    InputStream is = null;
                    try {
                        is = new BufferedInputStream(new FileInputStream(file));
                        byte[] buffer = new byte[1024 ]; 
                        int len = -1;
                        while((len = is.read(buffer)) != -1) {
                            //把缓冲区的字节写入到ZipArchiveEntry
                            zaos.write(buffer, 0, len);
                        }
                        zaos.closeArchiveEntry(); 
                    }catch(Exception e) {
                        throw new RuntimeException(e);
                    }finally {
                        if(is != null)
                            is.close();
                    }
                     
                }
            }
            zaos.finish();
        }catch(Exception e){
            throw new RuntimeException(e);
        }finally {
                try {
                    if(zaos != null) {
                        zaos.close();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
        }
         
    }
    
   
    /**
    * 把zip文件解压到指定的文件夹
    * @param zipFilePath zip文件路径, 如 "D:/test/aa.zip"
    * @param saveFileDir 解压后的文件存放路径, 如"D:/test/" ()
    */
	public static void unzip(String zipFilePath, String saveFileDir) {
		if(!saveFileDir.endsWith("\\") && !saveFileDir.endsWith("/") ){
			saveFileDir += File.separator;
		}
		File dir = new File(saveFileDir);
		if(!dir.exists()){
			dir.mkdirs();
		}
		File file = new File(zipFilePath);
		if (file.exists()) {
			InputStream is = null; 
			ZipArchiveInputStream zais = null;
			try {
				is = new FileInputStream(file);
				zais = new ZipArchiveInputStream(is);
				ArchiveEntry archiveEntry = null;
				while ((archiveEntry = zais.getNextEntry()) != null) { 
					// 获取文件名
					String entryFileName = archiveEntry.getName();
					// 构造解压出来的文件存放路径
					String entryFilePath = saveFileDir + entryFileName;
					OutputStream os = null;
					try {
						// 把解压出来的文件写到指定路径
						File entryFile = new File(entryFilePath);
						if(entryFileName.endsWith("/")){
							entryFile.mkdirs();
						}else{
							os = new BufferedOutputStream(new FileOutputStream(
									entryFile));							
							byte[] buffer = new byte[1024 ]; 
	                        int len = -1; 
	                        while((len = zais.read(buffer)) != -1) {
	                        	os.write(buffer, 0, len); 
	                        }
						}
					} catch (IOException e) {
						throw new IOException(e);
					} finally {
						if (os != null) {
							os.flush();
							os.close();
						}
					}

				} 
			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
				try {
					if (zais != null) {
						zais.close();
					}
					if (is != null) {
						is.close();
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
}
