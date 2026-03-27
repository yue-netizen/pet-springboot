package com.pet.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pet.user.entity.SysConfig;

public interface SysConfigService extends IService<SysConfig> {

    SysConfig getByKey(String configKey);

    void updateByKey(String configKey, String configValue);
}
