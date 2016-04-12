/*******************************************************************************
	* 系统名称   ： 博瑞思电子商务平台系统
	* 文件名     ： FileUtil.java
 *  			 (C) Copyright brains-info Corporation 2011
 *               All Rights Reserved.
 * *****************************************************************************
 *    注意： 本内容仅限于博瑞思信息技术有限公司内部使用，禁止转发
 * *****************************************************************************/
/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.ofbiz.base.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.net.MalformedURLException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.apache.commons.io.FileUtils;
import org.ofbiz.base.location.ComponentLocationResolver;
import org.ofbiz.base.util.string.FlexibleStringExpander;

/**
 * File Utilities
 *
 */
public class FileUtil {

    public static final String module = FileUtil.class.getName();

    public static File getFile(String path) {
        return getFile(null, path);
    }

    public static File getFile(File root, String path) {
        if (path.startsWith("component://")) {
            try {
                path = ComponentLocationResolver.getBaseLocation(path).toString();
            } catch (MalformedURLException e) {
                Debug.logError(e, module);
                return null;
            }
        }
        String fileNameSeparator = ("\\".equals(File.separator)? "\\" + File.separator: File.separator);
        return new File(root, path.replaceAll("/+|\\\\+", fileNameSeparator));
    }

    public static void writeString(String fileName, String s) throws IOException {
        writeString(null, fileName, s);
    }

    public static void writeString(String path, String name, String s) throws IOException {
        Writer out = getBufferedWriter(path, name);

        try {
            out.write(s + System.getProperty("line.separator"));
        } catch (IOException e) {
            Debug.logError(e, module);
            throw e;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    Debug.logError(e, module);
                }
            }
        }
    }

    /**
     * Writes a file from a string with a specified encoding.
     *
     * @param path
     * @param name
     * @param encoding
     * @param s
     * @throws IOException
     */
    public static void writeString(String path, String name, String encoding, String s) throws IOException {
        String fileName = getPatchedFileName(path, name);
        if (UtilValidate.isEmpty(fileName)) {
            throw new IOException("Cannot obtain buffered writer for an empty filename!");
        }

        try {
            FileUtils.writeStringToFile(new File(fileName), s, encoding);
        } catch (IOException e) {
            Debug.logError(e, module);
            throw e;
        }
    }

    public static void writeString(String encoding, String s, File outFile) throws IOException {
        try {
            FileUtils.writeStringToFile(outFile, s, encoding);
        } catch (IOException e) {
            Debug.logError(e, module);
            throw e;
        }
    }
    /**
     * 保存图片方法
     * @param imageData			图片数据
     * @param uploadFileName	图片名称
     * @param filePath			保存文件夹
     * @param fileType			图片类型（商品图片、相册图片）
     * @param productVersionId	保存记录id
     * @param imageServerPath	保存绝对路径
     * @return	imageUrl		图片相对路径
     * @return	filenameToUse	图片名称
     */
    public static Map<String, String> writeImageFile(ByteBuffer imageData,String uploadFileName, String filePath, 
    		String fileType, String id, String imageServerPath){
    	Map<String, String> result = FastMap.newInstance();
    	
    	String imageFilenameFormat = UtilProperties.getPropertyValue("catalog", "image.filename.format");
        String imageUrlPrefix = UtilProperties.getPropertyValue("catalog", "image.url.prefix");
        FlexibleStringExpander filenameExpander = FlexibleStringExpander.getInstance(imageFilenameFormat);
        
        String fileLocation = filenameExpander.expandString(UtilMisc.toMap("location", filePath, "type", fileType, "id", id));
        String filenameToUse = id;
        filenameToUse += uploadFileName.substring(uploadFileName.lastIndexOf("."));
        fileLocation += uploadFileName.substring(uploadFileName.lastIndexOf("."));
        
    	File file = new File(imageServerPath + "/" + fileLocation);
        
        try {
            RandomAccessFile out = new RandomAccessFile(file, "rw");
            out.write(imageData.array());
            out.close();
        } catch (FileNotFoundException e) {
            Debug.logError(e, "Unable to open file for writing: " + file.getAbsolutePath(), module);
            result.put("error", "error");
            return result;
        } catch (IOException e) {
            Debug.logError(e, "Unable to write binary data to: " + file.getAbsolutePath(), module);
            result.put("error", "error");
            return result;
        }
        
        String imageUrl = imageUrlPrefix + "/" + fileLocation;
        result.put("sucess", "sucess");
        result.put("imageUrl", imageUrl);
        result.put("filenameToUse", filenameToUse);
        return result;
    }
    
    public static Writer getBufferedWriter(String path, String name) throws IOException {
        String fileName = getPatchedFileName(path, name);
        if (UtilValidate.isEmpty(fileName)) {
            throw new IOException("Cannot obtain buffered writer for an empty filename!");
        }

        return new BufferedWriter(new FileWriter(fileName));
    }

    public static OutputStream getBufferedOutputStream(String path, String name) throws IOException {
        String fileName = getPatchedFileName(path, name);
        if (UtilValidate.isEmpty(fileName)) {
            throw new IOException("Cannot obtain buffered writer for an empty filename!");
        }

        return new BufferedOutputStream(new FileOutputStream(fileName));
    }

    public static String getPatchedFileName(String path, String fileName) throws IOException {
        // make sure the export directory exists
        if (UtilValidate.isNotEmpty(path)) {
            path = path.replaceAll("\\\\", "/");
            File parentDir = new File(path);
            if (!parentDir.exists()) {
                if (!parentDir.mkdir()) {
                    throw new IOException("Cannot create directory for path: " + path);
                }
            }

            // build the filename with the path
            if (!path.endsWith("/")) {
                path = path + "/";
            }
            if (fileName.startsWith("/")) {
                fileName = fileName.substring(1);
            }
            fileName = path + fileName;
        }

        return fileName;
    }

    public static StringBuffer readTextFile(File file, boolean newline) throws FileNotFoundException, IOException {
        if (!file.exists()) {
            throw new FileNotFoundException();
        }

        StringBuffer buf = new StringBuffer();
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));

            String str;
            while ((str = in.readLine()) != null) {
                buf.append(str);
                if (newline) {
                    buf.append(System.getProperty("line.separator"));
                }
            }
        } catch (IOException e) {
            Debug.logError(e, module);
            throw e;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Debug.logError(e, module);
                }
            }
        }

        return buf;
    }
    public static StringBuffer readTextFile(String fileName, boolean newline) throws FileNotFoundException, IOException {
        File file = new File(fileName);
        return readTextFile(file, newline);
    }

    public static String readString(String encoding, File inFile) throws IOException {
        String readString = "";
        try {
            readString = FileUtils.readFileToString(inFile, encoding);
        } catch (IOException e) {
            Debug.logError(e, module);
            throw e;
        }
        return readString;
    }

    public static void searchFiles(List<File> fileList, File path, FilenameFilter filter, boolean includeSubfolders) throws IOException {
        // Get filtered files in the current path
        File[] files = path.listFiles(filter);
        if (files == null) {
            return;
        }

        // Process each filtered entry
        for (int i = 0; i < files.length; i++) {
            // recurse if the entry is a directory
            if (files[i].isDirectory() && includeSubfolders && !files[i].getName().startsWith(".")) {
                searchFiles(fileList, files[i], filter, true);
            } else {
                // add the filtered file to the list
                fileList.add(files[i]);
            }
        }
    }

    public static List<File> findFiles(String fileExt, String basePath, String partialPath, String stringToFind) throws IOException {
        if (basePath == null) {
            basePath = System.getProperty("ofbiz.home");
        }

        Set<String> stringsToFindInPath = FastSet.newInstance();
        Set<String> stringsToFindInFile = FastSet.newInstance();

        if (partialPath != null) {
           stringsToFindInPath.add(partialPath);
        }
        if (stringToFind != null) {
           stringsToFindInFile.add(stringToFind);
        }

        List<File> fileList = FastList.newInstance();
        FileUtil.searchFiles(fileList, new File(basePath), new SearchTextFilesFilter(fileExt, stringsToFindInPath, stringsToFindInFile), true);

        return fileList;
    }

    public static List<File> findXmlFiles(String basePath, String partialPath, String rootElementName, String xsdOrDtdName) throws IOException {
        if (basePath == null) {
            basePath = System.getProperty("ofbiz.home");
        }

        Set<String> stringsToFindInPath = FastSet.newInstance();
        Set<String> stringsToFindInFile = FastSet.newInstance();

        if (partialPath != null) stringsToFindInPath.add(partialPath);
        if (rootElementName != null) stringsToFindInFile.add("<" + rootElementName + " ");
        if (xsdOrDtdName != null) stringsToFindInFile.add(xsdOrDtdName);

        List<File> fileList = FastList.newInstance();
        FileUtil.searchFiles(fileList, new File(basePath), new SearchTextFilesFilter("xml", stringsToFindInPath, stringsToFindInFile), true);
        return fileList;
    }

    public static class SearchTextFilesFilter implements FilenameFilter {
        String fileExtension;
        Set<String> stringsToFindInFile = FastSet.newInstance();
        Set<String> stringsToFindInPath = FastSet.newInstance();

        public SearchTextFilesFilter(String fileExtension, Set<String> stringsToFindInPath, Set<String> stringsToFindInFile) {
            this.fileExtension = fileExtension;
            if (stringsToFindInPath != null) {
                this.stringsToFindInPath.addAll(stringsToFindInPath);
            }
            if (stringsToFindInFile != null) {
                this.stringsToFindInFile.addAll(stringsToFindInFile);
            }
        }

        public boolean accept(File dir, String name) {
            File file = new File(dir, name);
            if (file.getName().startsWith(".")) {
                return false;
            }
            if (file.isDirectory()) {
                return true;
            }

            boolean hasAllPathStrings = true;
            String fullPath = dir.getPath().replace('\\', '/');
            for (String pathString: stringsToFindInPath) {
                if (fullPath.indexOf(pathString) < 0) {
                    hasAllPathStrings = false;
                    break;
                }
            }

            if (hasAllPathStrings && name.endsWith("." + fileExtension)) {
                if (stringsToFindInFile.size() == 0) {
                    return true;
                }
                StringBuffer xmlFileBuffer = null;
                try {
                    xmlFileBuffer = FileUtil.readTextFile(file, true);
                } catch (FileNotFoundException e) {
                    Debug.logWarning("Error reading xml file [" + file + "] for file search: " + e.toString(), module);
                    return false;
                } catch (IOException e) {
                    Debug.logWarning("Error reading xml file [" + file + "] for file search: " + e.toString(), module);
                    return false;
                }
                if (UtilValidate.isNotEmpty(xmlFileBuffer)) {
                    boolean hasAllStrings = true;
                    for (String stringToFile: stringsToFindInFile) {
                        if (xmlFileBuffer.indexOf(stringToFile) < 0) {
                            hasAllStrings = false;
                            break;
                        }
                    }
                    return hasAllStrings;
                }
            } else {
                return false;
            }
            return false;
        }
    }
    
    /** Get System default Encoding */
    public static String getDefaultEncoding(){
        String temp = System.getProperty("file.encoding");
        return temp;
    }
    /** make dir */
    public boolean mkdir(String strPath){
        try{
            File files=new File(strPath.toString());
            if(!files.exists()){
                files.mkdir();
            }
        } catch(Exception e){
            Debug.logError(e, module);
            return false;
        }
        return true;
    }
    /** Delete File */
    public static boolean deleFile(String strPath){
        try{
            File files = new File(strPath.toString());
            if(files.exists()){
                boolean b = files.delete();
                Debug.logInfo("删除文件：" + strPath + ", result : " + b, module);
            }
        } catch(Exception e){
            Debug.logError(e, module);
            return false;
        }
        return true;
    }
    /** Create New File */
    public static boolean createFile(String strPath,String content,String strEncode){
        BufferedWriter bw=null;
        try{
            File files=new File(strPath.toString());
            bw= new BufferedWriter(new OutputStreamWriter(new FileOutputStream(files), strEncode));
            bw.write(content,0,content.length());
            bw.flush();
        } catch(IOException e){
        	Debug.logError(e, module);
            return false;
        } finally{
            try{
                bw.close();
            } catch(IOException e){
                Debug.logError(e, module);
                return false;
            }
        }
        return true;
    }
    
    /** Read File */
    public static String readFile(String filepath,String strEncode) throws FileNotFoundException {
        StringBuffer returnStr = new StringBuffer();
        BufferedReader br = null;
        InputStreamReader read = null;
        try{
            File file = new File(filepath.toString());
            read = new InputStreamReader(new FileInputStream(file),strEncode);
            br = new BufferedReader(read);
            String line = br.readLine();
            while(line != null){
                returnStr.append(line+"\n");
                line = br.readLine();
            }
        } catch(IOException e){
            Debug.logError(e, module);
            return null;
        } finally{
        	if(br != null)
	            try{
	                br.close();
	                read.close();
	            }catch(IOException e){
	                Debug.logError(e, module);
	            }
        }
        return returnStr.toString();
    }
    
    /**
     * 拷贝二进制文件
     * @param sourceFile 源文件路径
     * @param targetFile 目标文件目录
     * @return
     */
    public static String copyImageFile(String sourceFile, String targetFile){
    	
    	if(UtilValidate.isNotEmpty(sourceFile) && UtilValidate.isNotEmpty(targetFile)){
	    	File realFile1 = FileUtil.getFile(sourceFile);
	    	FileInputStream fis = null;
	    	BufferedInputStream bins = null;
	    	FileOutputStream fos = null;
	    	BufferedOutputStream bout= null;
	    	if(realFile1.exists()){
	    		try{
		    		File realFile2 = FileUtil.getFile(targetFile);
		    		fis = new FileInputStream(realFile1);
		    		bins = new BufferedInputStream(fis);
		    		fos = new FileOutputStream(realFile2);
		    		bout= new BufferedOutputStream(fos);
		    		  
		    		byte[] buf=new byte[1024];
		    		int len= bins.read(buf);//读文件，将读到的内容放入到buf数组中，返回的是读到的长度
		    		while(len != -1){
		    			bout.write(buf, 0, len);
	    		   		len = bins.read(buf);
		    		}
	    		}catch (Exception e) {
					Debug.logError(e, module);
					return "error";
				} finally {
					if(bout != null){
						try {
							bout.close();
				    		fos.close();
				    		bins.close();
				    		fis.close();
						} catch (IOException e) {
							Debug.logError(e, module);
							return "error";
						}
					}
				}
	    	} else {
	    		return "error";
	    	}
    	}
		return "success";
    }
    
    public static String FILE_SEPRATOR = System.getProperty("file.separator");
    
    /**
     * 检查文件是否是图片
     * @param file 文件
     * @return true|false
     * @throws IOException
     */
    public static boolean checkImageFile(File file) throws IOException{
    	ImageInputStream iis = ImageIO.createImageInputStream(file);
		Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
		if(! iter.hasNext()){
			iis.close();  
			return false;
		}
		iis.close();
		return true;
    }
    
    /**
     * 采用NIO写入文件
     * @param file 文件
     * @param buff 待写入数据
     * @return 成功，失败
     */
    public static boolean writeFile(File file, ByteBuffer buff){
    	try {
    		buff.flip();
			FileChannel outChannel = new FileOutputStream(file).getChannel();
			outChannel.write(buff);
			outChannel.close();
		} catch (FileNotFoundException e) {
			Debug.logError(e, module);
			return false;
		} catch (IOException e) {
			Debug.logError(e, module);
			return false;
		}
		return true;
    }
}
