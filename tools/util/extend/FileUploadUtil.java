/**
 * @author guhao
 */
package org.ofbiz.base.util.extend;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import javolution.util.FastMap;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.string.FlexibleStringExpander;

public class FileUploadUtil {
	
	
	public static final String module = FileUploadUtil.class.getName();
	
	
	
	/**
	 * 实现上传功能，并把前台提交的参数全部封装到Map
	 * @param imageFolder
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> uploadFileAndToMap(String imageFolder,HttpServletRequest request){

		Map<String, Object> context = FastMap.newInstance();
		
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		
		String maxSize = UtilProperties.getPropertyValue("catalog", "image.maxsize", "1048576");
		String imageExts = UtilProperties.getPropertyValue("catalog", "image.exts", ".gif|.jpg|.png");
		long lMaxSize = Long.parseLong(maxSize);
		try {
			List<FileItem> items = upload.parseRequest(request);

			Iterator<FileItem> it = items.iterator();

			while (it.hasNext()) {
				FileItem fileItem = it.next();
				if (fileItem.isFormField()) {
					String fieldName = fileItem.getFieldName();
					String fieldValue = fileItem.getString("UTF-8");

					if (!context.containsKey(fieldName)) {
						context.put(fieldName, fieldValue);
					} else {
						// 如果已包含，把两个值用";"隔开
						String old = (String) context.get(fieldName);
						context.put(fieldName, old + "," + fieldValue);
					}
				} else {
					if (fileItem.getSize() > 0) {	
						String fileFullName = fileItem.getName();
						String extendName = fileFullName.substring(fileFullName.lastIndexOf("."));
						extendName = extendName.toLowerCase();
						
						if(extendName.indexOf(imageExts) > -1 && fileItem.getSize() > lMaxSize){
							Map<String,String> errorMap = FastMap.newInstance();
							errorMap.put(fileItem.getFieldName(), "文件大小超过"+lMaxSize/1024+"KB");
							context.put("errorMap", errorMap);
							return context;
						}
						
//						String fileFullName = fileItem.getName();
//						String extendName = fileFullName.substring(fileFullName.lastIndexOf("."));
						String nowDay=new SimpleDateFormat("yyyyMMdd").format(new Date());
						String uuid =UUIDUtil.uuidTomini();
						String filePath =  nowDay;
	                    File saveFilePath = new File(imageFolder+filePath);
	                    if (!saveFilePath.exists()) { saveFilePath.mkdirs();} 
	                    filePath = filePath+"/" + uuid + extendName;
						File saveFile = new File(imageFolder+filePath);
						if (!saveFile.exists()) {
							saveFile.createNewFile();
						}
						
						fileItem.write(saveFile);
						//context.put("fileName", fileName);
						context.put(fileItem.getFieldName(), filePath);
						context.put(fileItem.getFieldName()+"_fname", fileFullName);
						//context.put("filePath", filePath);
					}
				}
			}

		} catch (Exception e) {
		    Debug.log(e.getMessage(),module);
		    
		}
		return context;
	}
	
	/**
	 * 封装页面提交参数，调用端指定图片路径名称
	 * @param paramValues
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> doUploadParamToMap(Map<String,String> paramValues,HttpServletRequest request){

		Map<String, Object> context = FastMap.newInstance();
		
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);		
		
		Map<String,String> errorMap = FastMap.newInstance();
		String maxSize = UtilProperties.getPropertyValue("catalog", "image.maxsize", "1048576");
		String imageExts = UtilProperties.getPropertyValue("catalog", "image.exts", ".gif|.jpg|.png");
		long lMaxSize = Long.parseLong(maxSize);
		
		String imageServerPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("catalog", "image.server.path"), paramValues);
		try {
			List<FileItem> items = upload.parseRequest(request);

			Iterator<FileItem> it = items.iterator();

			while (it.hasNext()) {
				FileItem fileItem = it.next();
				if (fileItem.isFormField()) {
					String fieldName = fileItem.getFieldName();
					String fieldValue = fileItem.getString("UTF-8");

					if (!context.containsKey(fieldName)) {
						context.put(fieldName, fieldValue);
					} else {
						// 如果已包含，把两个值用";"隔开
						String old = (String) context.get(fieldName);
						context.put(fieldName, old + "," + fieldValue);
					}
				} else {
					if (fileItem.getSize() > 0) {		
						String paramName = fileItem.getFieldName();						
						String fileFullName = fileItem.getName();
						String extendName = fileFullName.substring(fileFullName.lastIndexOf("."));
						extendName = extendName.toLowerCase();
						
						if(extendName.indexOf(imageExts) > -1 && fileItem.getSize() > lMaxSize){
							errorMap.put(paramName, "文件大小超过"+lMaxSize/1024+"KB");
							context.put("errorMap", errorMap);
							return context;
						}

						File saveFile = null;
						File saveFilePath = null;
						String filePath = null;
						if(paramValues.containsKey(paramName)){
							String paramValue = paramValues.get(paramName);
							filePath = paramValue.indexOf("/")==0 ? paramValue : "/" + paramValue;
							saveFilePath = new File(imageServerPath + filePath.substring(0, filePath.lastIndexOf("/")));
							if (!saveFilePath.exists()) {saveFilePath.mkdirs();} 
							filePath += extendName;
						}else{
							String uploadFolder = UtilProperties.getPropertyValue("catalog", "catalog.upload.path", "/catalog/upload");
							String nowDay=new SimpleDateFormat("yyyyMMdd").format(new Date());
							String uuid =UUIDUtil.uuidTomini();
							filePath = uploadFolder + "/" + nowDay;
		                    saveFilePath = new File(imageServerPath + filePath);
		                    if (!saveFilePath.exists()) {saveFilePath.mkdirs();} 
		                    filePath += "/" + uuid + extendName;
						}
	                    saveFile =new File(imageServerPath + filePath);
						if (saveFile.exists()) {
							saveFile.delete();
						}
						saveFile.createNewFile();
						fileItem.write(saveFile);
						context.put(fileItem.getFieldName(), "/images"+filePath);
					}
				}
			}

		} catch (Exception e) {
		    Debug.log(e.getMessage(),module);
		    
		}
		return context;
	}
}
