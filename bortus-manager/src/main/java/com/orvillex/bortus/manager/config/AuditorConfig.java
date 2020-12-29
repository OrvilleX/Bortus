package com.orvillex.bortus.manager.config;

import com.orvillex.bortus.manager.utils.SecurityUtils;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import java.util.Optional;

/**
 * 配置审计中的操作员
 * @author y-z-f
 * @version 0.1
 */
@Component("auditorAware")
public class AuditorConfig implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        try {
            return Optional.of(SecurityUtils.getCurrentUsername());
        } catch (Exception e) {

        }
        return Optional.of("System");
    }
}
