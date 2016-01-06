package com.baidu.ueditor.upload;

import com.baidu.ueditor.PathFormat;
import com.baidu.ueditor.define.AppInfo;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.FileType;
import com.baidu.ueditor.define.State;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

public class BinaryUploader {
	//private static Logger log = Logger.getLogger(BinaryUploader.class);
	public static final State save(HttpServletRequest request,
			Map<String, Object> conf) {
		
		//二次开发，定义图片存储根路径和访问根路径
		String rootPathExternal = (String) request.getAttribute("rootPathExternal");
		String rootUrlExternal = (String) request.getAttribute("rootUrlExternal");
		
		FileItemStream fileStream = null;
		boolean isAjaxUpload = request.getHeader( "X_Requested_With" ) != null;

		if (!ServletFileUpload.isMultipartContent(request)) {
			return new BaseState(false, AppInfo.NOT_MULTIPART_CONTENT);
		}

		ServletFileUpload upload = new ServletFileUpload(
				new DiskFileItemFactory());

        if ( isAjaxUpload ) {
            upload.setHeaderEncoding( "UTF-8" );
        }

		try {
			FileItemIterator iterator = upload.getItemIterator(request);

			while (iterator.hasNext()) {
				fileStream = iterator.next();

				if (!fileStream.isFormField())
					break;
				fileStream = null;
			}

			if (fileStream == null) {
				return new BaseState(false, AppInfo.NOTFOUND_UPLOAD_DATA);
			}

			String savePath = (String) conf.get("savePath");
			
			//log.error("LZB TEST 001: savePath:"+savePath);
			
			String originFileName = fileStream.getName();
			String suffix = FileType.getSuffixByFilename(originFileName);

			//log.error("LZB TEST 002: originFileName:"+originFileName);
			
			originFileName = originFileName.substring(0,
					originFileName.length() - suffix.length());
			savePath = savePath + suffix;
			
			//log.error("LZB TEST 003: savePath:"+savePath);
			
			long maxSize = ((Long) conf.get("maxSize")).longValue();

			if (!validType(suffix, (String[]) conf.get("allowFiles"))) {
				return new BaseState(false, AppInfo.NOT_ALLOW_FILE_TYPE);
			}

			savePath = PathFormat.parse(savePath, originFileName);

			//log.error("LZB TEST 004: savePath:"+savePath);
			//二次开发，修改了图片存储的路径，去掉实例的根路径rootPath，替换成图片存储根路径。
			//String physicalPath = (String) conf.get("rootPath") + savePath;
			String physicalPath = rootPathExternal + savePath;
			//log.error("LZB TEST 005: physicalPath:"+physicalPath);
			physicalPath = physicalPath.replace("//", "/");

			//log.error("LZB TEST 006: physicalPath:"+physicalPath);
			InputStream is = fileStream.openStream();
			State storageState = StorageManager.saveFileByInputStream(is,
					physicalPath, maxSize);
			is.close();

			if (storageState.isSuccess()) {
				//二次开发，返回访问根路径 + savePath
				storageState.putInfo("url", (rootUrlExternal + savePath).replace("//", "/"));
				storageState.putInfo("type", suffix);
				storageState.putInfo("original", originFileName + suffix);

				
			}

			return storageState;
		} catch (FileUploadException e) {
			return new BaseState(false, AppInfo.PARSE_REQUEST_ERROR);
		} catch (IOException e) {
		}
		return new BaseState(false, AppInfo.IO_ERROR);
	}

	private static boolean validType(String type, String[] allowTypes) {
		List<String> list = Arrays.asList(allowTypes);

		return list.contains(type);
	}
}
