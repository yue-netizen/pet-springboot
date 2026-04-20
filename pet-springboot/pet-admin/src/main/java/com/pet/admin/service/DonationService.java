package com.pet.admin.service;

import com.pet.common.result.Result;

import java.util.List;
import java.util.Map;

public interface DonationService {

    Result<List<Map<String, Object>>> getDonationList();

    Result<List<Map<String, Object>>> getDonationStatsByUser();

    Result<java.math.BigDecimal> getTotalAmount();
}
