package com.orvillex.utils;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.poi.excel.BigExcelWriter;
import cn.hutool.poi.excel.ExcelUtil;
import com.orvillex.exception.BadRequestException;
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
import java.time.LocalDateTime;
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

    public static final String IMAGE = "图片";
    public static final String TXT = "文档";
    public static final String MUSIC  = "音乐";
    public static final String VIDEO = "视频";
    public static final String OTHER = "其他";

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

    /**
     * 导出excel
     */
    public static void downloadExcel(List<Map<String, Object>> list, HttpServletResponse response) throws IOException {
        String tempPath = SYS_TEM_DIR + IdUtil.fastSimpleUUID() + ".xlsx";
        File file = new File(tempPath);
        BigExcelWriter writer = ExcelUtil.getBigWriter(file);
        writer.write(list, true);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=file.xlsx");
        ServletOutputStream out = response.getOutputStream();
        file.deleteOnExit();
        writer.flush(out, true);
        IoUtil.close(out);
    }

    /**
     * 获取文件类型
     */
    public static String getFileType(String type) {
        String documents = "txt doc pdf ppt pps xlsx xls docx";
        String music = "mp3 wav wma mpa ram ra aac aif m4a";
        String video = "avi mpg mpe mpeg asf wmv mov qt rm mp4 flv m4v webm ogv ogg";
        String image = "bmp dib pcp dif wmf gif jpg tif eps psd cdr iff tga pcd mpt png jpeg";
        if (image.contains(type)) {
            return IMAGE;
        } else if (documents.contains(type)) {
            return TXT;
        } else if (music.contains(type)) {
            return MUSIC;
        } else if (video.contains(type)) {
            return VIDEO;
        } else {
            return OTHER;
        }
    }

    /**
     * 判断文件尺寸
     * @param maxSize 文件最大尺寸，单位MB
     * @param size 文件尺寸，单位B
     */
    public static void checkSize(long maxSize, long size) {
        int len = 1024 * 1024;
        if (size > (maxSize * len)) {
            throw new BadRequestException("文件超出规定大小");
        }
    }

    /**
     * 判断两个文件是否相等
     */
    public static boolean check(File origin, File dest) {
        String file1Md5 = getMd5(origin);
        String file2Md5 = getMd5(dest);
        return file1Md5.equals(file2Md5);
    }

    /**
     * 下载文件
     */
    public static void downloadFile(HttpServletRequest request, HttpServletResponse response, File file, boolean deleteOnExit) {
        response.setCharacterEncoding(request.getCharacterEncoding());
        response.setContentType("application/octet-stream");
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
            IOUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                    if (deleteOnExit) {
                        file.deleteOnExit();
                    }
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 获取文件MD5码
     */
    public static String getMd5(File file) {
        return getMd5(getByte(file));
    }

    /**
     * 获取文件字节组
     */
    private static byte[] getByte(File file) {
        byte[] b = new byte[(int)file.length()];
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            System.out.println(inputStream.read(b));
        } catch (IOException io) {
            log.error(io.getMessage(), io);
            return null;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return b;
    }

    /**
     * 获取文件MD5
     */
    private static String getMd5(byte[] bytes) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(bytes);
            byte[] md = md5.digest();
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;
            for (byte bytetmp : md) {
                str[k++] = hexDigits[bytetmp >>> 4 & 0xf];
                str[k++] = hexDigits[bytetmp & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }


}
