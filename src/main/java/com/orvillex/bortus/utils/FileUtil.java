package com.orvillex.bortus.utils;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.poi.excel.BigExcelWriter;
import cn.hutool.poi.excel.ExcelUtil;
import com.orvillex.bortus.exception.BadRequestException;
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * File工具类，扩展hutool工具包
 * @author y-z-f
 * @version 0.1
 */
public class FileUtil extends cn.hutool.core.io.FileUtil {
    
    private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 系统临时目录
     */
    public static final String SYS_TEM_DIR = System.getProperty("java.io.tmpdir");

    private static final int GB = 1024 * 1024 * 1024;
    private static final int MB = 1024 * 1024;
    private static final int KB = 1024;

    private static final DecimalFormat DF = new DecimalFormat("0.00");

    /**
     * MultipartFile转File
     */
    public static File toFile(MultipartFile multipartFile) {
        String fileName = multipartFile.getOriginalFilename();
        String prefix = "." + getExtensionName(fileName);
        File file = null;
        try {
            file = File.createTempFile(IdUtil.simpleUUID(), prefix);
            multipartFile.transferTo(file);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return file;
    }

    /**
     * 获取文件扩展名
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    /**
     * 获取文件名
     */
    public static String getFileName(String filename) {
        if (filename != null && !filename.isEmpty()) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    /**
     * 获取文件尺寸
     */
    public static String getSize(long size) {
        String resultSize;
        if (size / GB >= 1) {
            resultSize = DF.format(size / (float)GB) + "GB ";
        } else if (size / MB >= 1) {
            resultSize = DF.format(size / (float)MB) + "MB ";
        } else if (size / KB >= 1) {
            resultSize = DF.format(size / (float)KB) + "KB ";
        } else {
            resultSize = size + "B ";
        }
        return resultSize;
    }

    /**
     * inputStream转File
     */
    public static File inputStreamToFile(InputStream input, String name) throws Exception {
        File file = new File(SYS_TEM_DIR + name);
        if (file.exists()) {
            return file;
        }
        try(OutputStream os = new FileOutputStream(file)) {
            int bytesRead;
            int len = 8192;
            byte[] buffer = new byte[len];
            while ((bytesRead = input.read(buffer, 0, len)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } finally {
            input.close();
        }
        return file;
    }

    /**
     * 上传文件
     */
    public static File upload(MultipartFile file, String filePath) {
        LocalDateTime time = LocalDateTime.now();
        String name = getFileName(file.getOriginalFilename());
        String suffix = getExtensionName(file.getOriginalFilename());
        String nowStr = "-" + DateUtil.localDateTimeFormat(time, "yyyyMMddhhmmssS");

        try {
            String fileName = name + nowStr + "." + suffix;
            String path = filePath + fileName;
            File dest = new File(path).getCanonicalFile();
            if (!dest.getParentFile().exists()) {
                if (!dest.getParentFile().mkdirs()) {
                    System.out.println("was not successful.");
                }
            }
            file.transferTo(dest);
            return dest;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    
}
