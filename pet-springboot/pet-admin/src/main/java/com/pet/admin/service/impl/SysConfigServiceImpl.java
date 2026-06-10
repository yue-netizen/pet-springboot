package com.pet.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pet.admin.entity.SysConfig;
import com.pet.admin.mapper.SysConfigMapper;
import com.pet.admin.service.SysConfigService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    @Override
    public List<SysConfig> getAllConfigs() {
        return this.list();
    }

    @Override
    public SysConfig getByKey(String configKey) {
        return getOne(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigKey, configKey));
    }

    @Override
    public void updateByKey(String configKey, String configValue) {
        SysConfig existing = getByKey(configKey);
        if (existing != null) {
            existing.setConfigValue(configValue);
            existing.setUpdateTime(LocalDateTime.now());
            updateById(existing);
        } else {
            SysConfig newConfig = new SysConfig();
            newConfig.setConfigKey(configKey);
            newConfig.setConfigValue(configValue);
            newConfig.setConfigName(configKey);
            newConfig.setDescription(configKey);
            newConfig.setCreateTime(LocalDateTime.now());
            newConfig.setUpdateTime(LocalDateTime.now());
            save(newConfig);
        }
    }
}
