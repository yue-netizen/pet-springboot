package com.pet.story.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pet.common.exception.BusinessException;
import com.pet.common.result.Result;
import com.pet.story.entity.Story;
import com.pet.story.entity.StoryComment;
import com.pet.story.mapper.StoryCommentMapper;
import com.pet.story.mapper.StoryMapper;
import com.pet.story.service.StoryCommentService;
import com.pet.story.vo.StoryCommentDetailVO;
import com.pet.story.vo.StoryCommentVO;
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
public class StoryCommentServiceImpl extends ServiceImpl<StoryCommentMapper, StoryComment> implements StoryCommentService {

    private final StoryMapper storyMapper;
    private final DataSource dataSource;

    @Override
    public Result<Page<StoryCommentDetailVO>> getCommentsByStoryId(Long storyId, Integer page, Integer size) {
        LambdaQueryWrapper<StoryComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StoryComment::getStoryId, storyId)
                .eq(StoryComment::getStatus, 1)
                .orderByDesc(StoryComment::getCreateTime);

        Page<StoryComment> commentPage = new Page<>(page, size);
        Page<StoryComment> result = this.page(commentPage, wrapper);

        List<StoryCommentDetailVO> detailVOs = convertToDetailVOList(result.getRecords());

        Page<StoryCommentDetailVO> detailPage = new Page<>(page, size, result.getTotal());
        detailPage.setRecords(detailVOs);

        return Result.success(detailPage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> createComment(StoryCommentVO commentVO, Long userId) {
        Story story = storyMapper.selectById(commentVO.getStoryId());
        if (story == null) {
            throw BusinessException.of("故事不存在");
        }

        StoryComment comment = new StoryComment();
        comment.setStoryId(commentVO.getStoryId());
        comment.setUserId(userId);
        comment.setParentId(commentVO.getParentId() != null ? commentVO.getParentId() : 0L);
        comment.setContent(commentVO.getContent());
        comment.setLikeCount(0);
        comment.setStatus(1);

        this.save(comment);

        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteComment(Long id, Long userId) {
        StoryComment comment = this.getById(id);
        if (comment == null) {
            throw BusinessException.of("评论不存在");
        }

        if (!comment.getUserId().equals(userId)) {
            throw BusinessException.of("无权删除该评论");
        }

        this.removeById(id);

        return Result.success();
    }

    private List<StoryCommentDetailVO> convertToDetailVOList(List<StoryComment> comments) {
        if (comments.isEmpty()) {
            return new ArrayList<>();
        }

        Set<Long> userIds = comments.stream().map(StoryComment::getUserId).collect(Collectors.toSet());
        Map<Long, Map<String, Object>> userMap = queryUsers(userIds);

        return comments.stream().map(comment -> {
            StoryCommentDetailVO vo = new StoryCommentDetailVO();
            BeanUtils.copyProperties(comment, vo);

            Map<String, Object> user = userMap.get(comment.getUserId());
            if (user != null) {
                vo.setUsername((String) user.get("nickname"));
                vo.setUserAvatar((String) user.get("avatar"));
            }

            return vo;
        }).collect(Collectors.toList());
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
            log.error("查询评论用户信息失败", e);
            return Map.of();
        }
    }
}
