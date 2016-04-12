/*******************************************************************************
	* 系统名称   ： 博瑞思电子商务平台系统
	* 文件名     ： UtilZipFile.java
 *  			 (C) Copyright brains-info Corporation 2011
 *               All Rights Reserved.
 * *****************************************************************************
 *    注意： 本内容仅限于博瑞思信息技术有限公司内部使用，禁止转发
 * *****************************************************************************/
/**
 * 
 */
package org.ofbiz.base.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

/**
 * @author liangyx
 *
 */
public class UtilZipFile {
	
	private static final int BUFFER = 2048;
	
//	public static void main(String[] args){
//		try {
//			uncompress("H:/webservice/ofbiz_documents/OfbizTutorial_WS_RMI.zip", "H:/tools/");
//		} catch (ZipException e) {
//			e.printStackTrace();
//		}
//	}

	public static void uncompress(String inputFile, String toDir) throws ZipException{
		try {
            FileInputStream tin = new FileInputStream(inputFile);
            CheckedInputStream cin = new CheckedInputStream(tin, new CRC32());
            BufferedInputStream bufferIn = new BufferedInputStream(cin, BUFFER);
            ZipInputStream in = new ZipInputStream(bufferIn);
            ZipEntry z = in.getNextEntry();

            while (z != null) {
                String name = z.getName();
                if (z.isDirectory()) {
                    File f = new File(toDir + File.separator + name);
                    f.mkdir();
                } else {
                    File f = new File(toDir + File.separator + name);
                    f.createNewFile();
                    FileOutputStream out = new FileOutputStream(f);
                    byte data[] = new byte[BUFFER];
                    int b;

                    while ( (b = in.read(data, 0, BUFFER)) != -1) {
                        out.write(data, 0, b);
                    }
                    out.close();
                }
                z = in.getNextEntry();
            }

            in.close();
        } catch (IOException ex) {
            throw new ZipException(ex.getMessage());
        }
	}
}
