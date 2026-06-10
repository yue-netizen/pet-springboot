package com.pet.pet.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pet.common.constant.RedisConstants;
import com.pet.common.dto.PetDTO;
import com.pet.common.exception.BusinessException;
import com.pet.common.result.Result;
import com.pet.pet.entity.Adoption;
import com.pet.pet.entity.Pet;
import com.pet.pet.mapper.AdoptionMapper;
import com.pet.pet.mapper.PetMapper;
import com.pet.pet.service.PetService;
import com.pet.pet.vo.AdoptionApplyVO;
import com.pet.pet.vo.AdoptionRecordVO;
import com.pet.pet.vo.PetQueryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class PetServiceImpl extends ServiceImpl<PetMapper, Pet> implements PetService {

    private final AdoptionMapper adoptionMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Result<Page<Pet>> getPetList(PetQueryVO queryVO) {
        LambdaQueryWrapper<Pet> wrapper = new LambdaQueryWrapper<>();

        if (StrUtil.isNotBlank(queryVO.getName()) || StrUtil.isNotBlank(queryVO.getBreed())) {
            wrapper.and(w -> {
                if (StrUtil.isNotBlank(queryVO.getName())) {
                    w.like(Pet::getName, queryVO.getName());
                }
                if (StrUtil.isNotBlank(queryVO.getBreed())) {
                    if (StrUtil.isNotBlank(queryVO.getName())) {
                        w.or().like(Pet::getBreed, queryVO.getBreed());
                    } else {
                        w.like(Pet::getBreed, queryVO.getBreed());
                    }
                }
            });
        }
        if (StrUtil.isNotBlank(queryVO.getType())) {
            wrapper.eq(Pet::getType, queryVO.getType());
        }
        if (StrUtil.isNotBlank(queryVO.getAge())) {
            wrapper.eq(Pet::getAge, queryVO.getAge());
        }
        if (queryVO.getStatus() != null) {
            wrapper.eq(Pet::getStatus, queryVO.getStatus());
        }
        
        wrapper.orderByAsc(Pet::getId);
        
        List<Pet> allPets = this.list(wrapper);
        
        if (queryVO.getMinAge() != null || queryVO.getMaxAge() != null) {
            allPets = allPets.stream()
                    .filter(pet -> {
                        Integer petAge = extractAge(pet.getAge());
                        if (petAge == null) return true;
                        boolean match = true;
                        if (queryVO.getMinAge() != null) {
                            match = match && petAge >= queryVO.getMinAge();
                        }
                        if (queryVO.getMaxAge() != null) {
                            match = match && petAge <= queryVO.getMaxAge();
                        }
                        return match;
                    })
                    .toList();
        }
        
        int total = allPets.size();
        int pageNum = queryVO.getPage();
        int pageSize = queryVO.getSize();
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        
        List<Pet> pagePets = fromIndex < total ? allPets.subList(fromIndex, toIndex) : new ArrayList<>();
        
        Page<Pet> page = new Page<>(pageNum, pageSize, total);
        page.setRecords(pagePets);
        
        return Result.success(page);
    }
    
    private Integer extractAge(String ageStr) {
        if (StrUtil.isBlank(ageStr)) return null;
        try {
            if (ageStr.contains("个月")) {
                String months = ageStr.replace("个月", "").trim();
                int m = Integer.parseInt(months);
                return m / 12;
            } else if (ageStr.contains("岁")) {
                String years = ageStr.replace("岁", "").trim();
                return Integer.parseInt(years);
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return null;
    }

    @Override
    public Result<PetDTO> getPetById(Long id) {
        String cacheKey = RedisConstants.PET_DETAIL_KEY + id;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        
        if (cached != null) {
            return Result.success((PetDTO) cached);
        }
        
        Pet pet = this.getById(id);
        if (pet == null) {
            throw BusinessException.of("宠物不存在");
        }
        
        PetDTO petDTO = BeanUtil.copyProperties(pet, PetDTO.class);
        
        redisTemplate.opsForValue().set(cacheKey, petDTO, 1, TimeUnit.HOURS);
        
        return Result.success(petDTO);
    }

    @Override
    @Transactional
    public Result<Void> applyAdoption(AdoptionApplyVO applyVO, Long userId) {
        Pet pet = this.getById(applyVO.getPetId());
        if (pet == null) {
            throw BusinessException.of("宠物不存在");
        }
        if (pet.getStatus() != 1) {
            throw BusinessException.of("该宠物不可领养");
        }
        
        Adoption adoption = new Adoption();
        BeanUtil.copyProperties(applyVO, adoption);
        adoption.setUserId(userId);
        adoption.setStatus(0);
        
        adoptionMapper.insert(adoption);
        
        return Result.success();
    }

    @Override
    public Result<Page<Pet>> getMyAdoptions(Long userId, Integer page, Integer size) {
        LambdaQueryWrapper<Adoption> adoptionWrapper = new LambdaQueryWrapper<>();
        adoptionWrapper.eq(Adoption::getUserId, userId);
        adoptionWrapper.eq(Adoption::getStatus, 1);
        adoptionWrapper.orderByDesc(Adoption::getCreateTime);
        
        List<Adoption> adoptions = adoptionMapper.selectList(adoptionWrapper);
        
        List<Long> petIds = adoptions.stream()
                .map(Adoption::getPetId)
                .distinct()
                .toList();
        
        if (petIds.isEmpty()) {
            return Result.success(new Page<>(page, size, 0));
        }
        
        LambdaQueryWrapper<Pet> petWrapper = new LambdaQueryWrapper<>();
        petWrapper.in(Pet::getId, petIds);
        petWrapper.orderByAsc(Pet::getId);
        
        List<Pet> allPets = this.list(petWrapper);
        
        int total = allPets.size();
        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, total);
        
        List<Pet> pagePets = fromIndex < total ? allPets.subList(fromIndex, toIndex) : new ArrayList<>();
        
        Page<Pet> resultPage = new Page<>(page, size, total);
        resultPage.setRecords(pagePets);
        
        return Result.success(resultPage);
    }

    @Override
    public Result<Void> addPet(Pet pet) {
        this.save(pet);
        return Result.success();
    }

    @Override
    public Result<Void> updatePet(Pet pet) {
        this.updateById(pet);
        return Result.success();
    }

    @Override
    public Result<Void> deletePet(Long id) {
        this.removeById(id);
        return Result.success();
    }

    @Override
    public Result<Page<AdoptionRecordVO>> getMyAdoptionRecords(Long userId, Integer page, Integer size) {
        LambdaQueryWrapper<Adoption> adoptionWrapper = new LambdaQueryWrapper<>();
        adoptionWrapper.eq(Adoption::getUserId, userId);
        adoptionWrapper.orderByDesc(Adoption::getCreateTime);

        Page<Adoption> adoptionPage = new Page<>(page, size);
        Page<Adoption> result = adoptionMapper.selectPage(adoptionPage, adoptionWrapper);

        List<AdoptionRecordVO> recordVOs = convertToAdoptionRecordVOList(result.getRecords());

        Page<AdoptionRecordVO> resultPage = new Page<>(page, size, result.getTotal());
        resultPage.setRecords(recordVOs);

        return Result.success(resultPage);
    }

    private List<AdoptionRecordVO> convertToAdoptionRecordVOList(List<Adoption> adoptions) {
        if (adoptions.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> petIds = adoptions.stream().map(Adoption::getPetId).distinct().toList();

        LambdaQueryWrapper<Pet> petWrapper = new LambdaQueryWrapper<>();
        petWrapper.in(Pet::getId, petIds);
        List<Pet> pets = this.list(petWrapper);
        Map<Long, Pet> petMap = pets.stream().collect(Collectors.toMap(Pet::getId, p -> p));

        return adoptions.stream().map(adoption -> {
            AdoptionRecordVO vo = new AdoptionRecordVO();
            BeanUtil.copyProperties(adoption, vo);

            Pet pet = petMap.get(adoption.getPetId());
            if (pet != null) {
                vo.setPetName(pet.getName());
                vo.setPetImage(pet.getImage());
                vo.setPetBreed(pet.getBreed());
                vo.setPetType(pet.getType());
            }

            return vo;
        }).toList();
    }
}