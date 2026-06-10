package com.pet.tips.ai.controller;

import com.pet.common.result.Result;
import com.pet.tips.ai.service.TipsAiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Tag(name = "养宠AI助手")
@RestController
@RequestMapping("/tips-ai")
@RequiredArgsConstructor
public class TipsAiController {

    private final TipsAiService tipsAiService;

    @Operation(summary = "向AI助手提问养宠问题")
    @PostMapping("/ask")
    public Result<Map<String, String>> ask(@RequestBody AskRequest request) {
        log.info("AI助手请求: breed={}, question={}", request.breed(), request.question());

        String answer = tipsAiService.ask(request.breed(), request.question());

        Map<String, String> result = new HashMap<>();
        result.put("answer", answer);

        return Result.success(result);
    }

    public record AskRequest(String breed, String question) {}
}
