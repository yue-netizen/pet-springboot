package com.pet.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pet.user.entity.SysConfig;
import com.pet.user.mapper.SysConfigMapper;
import com.pet.user.service.SysConfigService;
import org.springframework.stereotype.Service;

@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    @Override
    public SysConfig getByKey(String configKey) {
        return getOne(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigKey, configKey));
    }

    @Override
    public void updateByKey(String configKey, String configValue) {
        update(new LambdaUpdateWrapper<SysConfig>()
                .eq(SysConfig::getConfigKey, configKey)
                .set(SysConfig::getConfigValue, configValue));
    }
}
