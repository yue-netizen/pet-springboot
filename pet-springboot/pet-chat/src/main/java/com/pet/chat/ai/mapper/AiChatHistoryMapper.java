package com.pet.chat.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pet.chat.ai.entity.AiChatHistory;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AiChatHistoryMapper extends BaseMapper<AiChatHistory> {

    @Select("SELECT * FROM ai_chat_history WHERE session_id = #{sessionId} ORDER BY created_at ASC")
    List<AiChatHistory> findBySessionId(@Param("sessionId") String sessionId);

    @Delete("DELETE FROM ai_chat_history WHERE session_id = #{sessionId}")
    int deleteBySessionId(@Param("sessionId") String sessionId);

    @Select("SELECT * FROM ai_chat_history WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<AiChatHistory> findByUserId(@Param("userId") Long userId);
}
