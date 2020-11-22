package com.orvillex.bortus.config;

import lombok.Data;
import com.orvillex.bortus.utils.Constant;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 提供文件上传路径配置
 * @author y-z-f
 * @version 0.1
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "file")
public class FileProperties {
    private Long maxSize;
    private long avatarMaxSize;
    private XPath mac;
    private XPath linux;
    private XPath windows;

    public XPath getPath() {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith(Constant.WIN)) {
            return windows;
        } else if (os.toLowerCase().startsWith(Constant.MAC)) {
            return mac;
        }
        return linux;
    }

    @Data
    public static class XPath {
        private String path;
        private String avatar;
    }
}
