package com.pet.story.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pet.common.exception.BusinessException;
import com.pet.common.result.Result;
import com.pet.story.entity.Story;
import com.pet.story.mapper.StoryMapper;
import com.pet.story.service.StoryService;
import com.pet.story.vo.StoryDetailVO;
import com.pet.story.vo.StoryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoryServiceImpl extends ServiceImpl<StoryMapper, Story> implements StoryService {

    private final DataSource dataSource;

    @Override
    public Result<Page<StoryDetailVO>> getStoryList(Integer page, Integer size) {
        LambdaQueryWrapper<Story> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Story::getStatus, 1)
                .orderByDesc(Story::getCreateTime);

        Page<Story> storyPage = new Page<>(page, size);
        Page<Story> result = this.page(storyPage, wrapper);

        List<StoryDetailVO> detailVOs = convertToDetailVOList(result.getRecords());

        Page<StoryDetailVO> detailPage = new Page<>(page, size, result.getTotal());
        detailPage.setRecords(detailVOs);

        return Result.success(detailPage);
    }

    @Override
    public Result<StoryDetailVO> getStoryById(Long id, Long userId) {
        Story story = this.getById(id);
        if (story == null) {
            throw BusinessException.of("故事不存在");
        }

        story.setViewCount(story.getViewCount() + 1);
        this.updateById(story);

        StoryDetailVO detailVO = convertToDetailVO(story);

        if (userId != null) {
            detailVO.setIsLiked(checkIfLiked(userId, id));
        }

        return Result.success(detailVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> createStory(StoryVO storyVO, Long userId) {
        Story story = new Story();
        story.setUserId(userId);
        story.setPetId(storyVO.getPetId());
        story.setTitle(storyVO.getTitle());
        story.setContent(storyVO.getContent());
        story.setImage(storyVO.getImage());
        story.setLikeCount(0);
        story.setViewCount(0);
        story.setStatus(1);

        this.save(story);

        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateStory(Story story) {
        Story existingStory = this.getById(story.getId());
        if (existingStory == null) {
            throw BusinessException.of("故事不存在");
        }

        this.updateById(story);

        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteStory(Long id, Long userId) {
        Story story = this.getById(id);
        if (story == null) {
            throw BusinessException.of("故事不存在");
        }

        if (!story.getUserId().equals(userId)) {
            throw BusinessException.of("无权删除该故事");
        }

        this.removeById(id);

        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> likeStory(Long storyId, Long userId) {
        Story story = this.getById(storyId);
        if (story == null) {
            throw BusinessException.of("故事不存在");
        }

        boolean isLiked = checkIfLiked(userId, storyId);

        if (isLiked) {
            removeLikeRecord(userId, storyId);
            story.setLikeCount(Math.max(0, story.getLikeCount() - 1));
        } else {
            insertLikeRecord(userId, storyId);
            story.setLikeCount(story.getLikeCount() + 1);
        }

        this.updateById(story);

        return Result.success();
    }

    @Override
    public Result<Page<Story>> getMyStories(Long userId, Integer page, Integer size) {
        LambdaQueryWrapper<Story> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Story::getUserId, userId)
                .orderByDesc(Story::getCreateTime);

        Page<Story> storyPage = new Page<>(page, size);
        Page<Story> result = this.page(storyPage, wrapper);

        return Result.success(result);
    }

    @Override
    public Result<Page<StoryDetailVO>> getMyLikedStories(Long userId, Integer page, Integer size) {
        List<Long> storyIds = queryLikedStoryIds(userId, page, size);

        if (storyIds.isEmpty()) {
            return Result.success(new Page<>(page, size, 0));
        }

        LambdaQueryWrapper<Story> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Story::getId, storyIds);
        List<Story> stories = this.list(wrapper);

        Map<Long, Story> storyMap = stories.stream().collect(Collectors.toMap(Story::getId, s -> s));

        List<Story> orderedStories = storyIds.stream()
                .filter(storyMap::containsKey)
                .map(storyMap::get)
                .toList();

        List<StoryDetailVO> detailVOs = convertToDetailVOList(orderedStories);

        long total = countLikedStories(userId);

        Page<StoryDetailVO> resultPage = new Page<>(page, size, total);
        resultPage.setRecords(detailVOs);

        return Result.success(resultPage);
    }

    private List<Long> queryLikedStoryIds(Long userId, Integer page, Integer size) {
        String sql = "SELECT story_id FROM story_like WHERE user_id = ? AND deleted = 0 ORDER BY create_time DESC LIMIT ? OFFSET ?";
        int offset = (page - 1) * size;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setInt(2, size);
            ps.setInt(3, offset);

            try (ResultSet rs = ps.executeQuery()) {
                List<Long> ids = new ArrayList<>();
                while (rs.next()) {
                    ids.add(rs.getLong("story_id"));
                }
                return ids;
            }
        } catch (Exception e) {
            log.error("查询点赞的故事ID失败", e);
            return new ArrayList<>();
        }
    }

    private long countLikedStories(Long userId) {
        String sql = "SELECT COUNT(*) FROM story_like WHERE user_id = ? AND deleted = 0";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (Exception e) {
            log.error("统计点赞的故事数量失败", e);
        }
        return 0;
    }

    private boolean checkIfLiked(Long userId, Long storyId) {
        String sql = "SELECT COUNT(*) FROM story_like WHERE user_id = ? AND story_id = ? AND deleted = 0";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, storyId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1) > 0;
                }
            }
        } catch (Exception e) {
            log.error("检查是否已点赞失败", e);
        }
        return false;
    }

    private void insertLikeRecord(Long userId, Long storyId) {
        String sql = "INSERT INTO story_like (user_id, story_id, create_time, update_time, deleted) VALUES (?, ?, NOW(), NOW(), 0)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, storyId);
            ps.executeUpdate();
        } catch (Exception e) {
            log.error("插入点赞记录失败", e);
            throw new BusinessException("插入点赞记录失败");
        }
    }

    private void removeLikeRecord(Long userId, Long storyId) {
        String sql = "UPDATE story_like SET deleted = 1 WHERE user_id = ? AND story_id = ? AND deleted = 0";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, storyId);
            ps.executeUpdate();
        } catch (Exception e) {
            log.error("删除点赞记录失败", e);
            throw new BusinessException("删除点赞记录失败");
        }
    }

    private List<StoryDetailVO> convertToDetailVOList(List<Story> stories) {
        if (stories.isEmpty()) {
            return new ArrayList<>();
        }

        Set<Long> userIds = stories.stream().map(Story::getUserId).collect(Collectors.toSet());
        Set<Long> petIds = stories.stream().map(Story::getPetId).filter(id -> id != null).collect(Collectors.toSet());

        Map<Long, Map<String, Object>> userMap = queryUsers(userIds);
        Map<Long, Map<String, Object>> petMap = queryPets(petIds);

        return stories.stream().map(story -> {
            StoryDetailVO vo = new StoryDetailVO();
            BeanUtils.copyProperties(story, vo);

            Map<String, Object> user = userMap.get(story.getUserId());
            if (user != null) {
                vo.setUsername((String) user.get("nickname"));
                vo.setUserAvatar((String) user.get("avatar"));
            }

            if (story.getPetId() != null) {
                Map<String, Object> pet = petMap.get(story.getPetId());
                if (pet != null) {
                    vo.setPetName((String) pet.get("name"));
                    vo.setPetImage((String) pet.get("image"));
                    vo.setPetBreed((String) pet.get("breed"));
                }
            }

            return vo;
        }).collect(Collectors.toList());
    }

    private StoryDetailVO convertToDetailVO(Story story) {
        List<Story> stories = new ArrayList<>();
        stories.add(story);
        List<StoryDetailVO> vos = convertToDetailVOList(stories);
        return vos.isEmpty() ? null : vos.get(0);
    }

    private Map<Long, Map<String, Object>> queryUsers(Set<Long> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }

        String placeholders = userIds.stream().map(id -> "?").collect(Collectors.joining(","));
        String sql = "SELECT id, username, nickname, avatar FROM sys_user WHERE id IN (" + placeholders + ") AND deleted = 0";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int index = 1;
            for (Long id : userIds) {
                ps.setLong(index++, id);
            }

            try (ResultSet rs = ps.executeQuery()) {
                List<Map<String, Object>> list = new ArrayList<>();
                while (rs.next()) {
                    Long uid = rs.getLong("id");
                    String nickname = rs.getString("nickname");
                    String username = rs.getString("username");
                    String avatar = rs.getString("avatar");

                    String displayName = "用户" + uid;
                    if (nickname != null && !nickname.isBlank()) {
                        displayName = nickname;
                    } else if (username != null && !username.isBlank()) {
                        displayName = username;
                    }

                    list.add(Map.of(
                            "id", uid,
                            "nickname", displayName,
                            "avatar", avatar != null ? avatar : ""
                    ));
                }
                return list.stream().collect(Collectors.toMap(m -> (Long) m.get("id"), m -> m));
            }
        } catch (Exception e) {
            log.error("查询用户信息失败", e);
            return Map.of();
        }
    }

    private Map<Long, Map<String, Object>> queryPets(Set<Long> petIds) {
        if (petIds.isEmpty()) {
            return Map.of();
        }

        String placeholders = petIds.stream().map(id -> "?").collect(Collectors.joining(","));
        String sql = "SELECT id, name, image, breed FROM pet_info WHERE id IN (" + placeholders + ") AND deleted = 0";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int index = 1;
            for (Long id : petIds) {
                ps.setLong(index++, id);
            }

            try (ResultSet rs = ps.executeQuery()) {
                List<Map<String, Object>> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(Map.of(
                            "id", rs.getLong("id"),
                            "name", rs.getString("name") != null ? rs.getString("name") : "",
                            "image", rs.getString("image") != null ? rs.getString("image") : "",
                            "breed", rs.getString("breed") != null ? rs.getString("breed") : ""
                    ));
                }
                return list.stream().collect(Collectors.toMap(m -> (Long) m.get("id"), m -> m));
            }
        } catch (Exception e) {
            log.error("查询宠物信息失败", e);
            return Map.of();
        }
    }
}
