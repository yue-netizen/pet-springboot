package com.pet.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pet.admin.entity.SysConfig;

import java.util.List;

public interface SysConfigService extends IService<SysConfig> {

    List<SysConfig> getAllConfigs();

    SysConfig getByKey(String configKey);

    void updateByKey(String configKey, String configValue);
}
