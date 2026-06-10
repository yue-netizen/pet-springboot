package com.pet.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pet.admin.entity.Donation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface DonationMapper extends BaseMapper<Donation> {

    @Select("SELECT d.id, d.user_id as userId, d.amount, d.payment_method as paymentMethod, d.status, " +
            "d.transaction_id as transactionId, d.remark, d.create_time as createTime, " +
            "u.nickname, u.avatar, u.username " +
            "FROM donation_record d LEFT JOIN sys_user u ON d.user_id = u.id " +
            "WHERE d.status = 1 ORDER BY d.create_time DESC")
    List<Map<String, Object>> getDonationListWithUser();

    @Select("SELECT d.user_id as userId, u.nickname, u.username, SUM(d.amount) as totalAmount, COUNT(d.id) as donateCount " +
            "FROM donation_record d LEFT JOIN sys_user u ON d.user_id = u.id " +
            "WHERE d.status = 1 GROUP BY d.user_id, u.nickname, u.username ORDER BY totalAmount DESC")
    List<Map<String, Object>> getDonationStatsByUser();

    @Select("SELECT COALESCE(SUM(amount), 0) FROM donation_record WHERE status = 1")
    BigDecimal getTotalAmount();
}
