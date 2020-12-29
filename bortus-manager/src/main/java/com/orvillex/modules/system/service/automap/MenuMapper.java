package com.orvillex.modules.system.service.automap;

import com.orvillex.entity.BaseMapper;
import com.orvillex.modules.system.domain.Menu;
import com.orvillex.modules.system.service.dto.MenuDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * MenuDto与Menu转换
 * @author y-z-f
 * @version 0.1
 */
@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MenuMapper extends BaseMapper<MenuDto, Menu> {
}
