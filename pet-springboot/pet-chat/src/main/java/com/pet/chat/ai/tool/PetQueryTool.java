package com.pet.chat.ai.tool;

import com.pet.common.feign.PetFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PetQueryTool {

    private final PetFeignClient petFeignClient;

    @Tool(description = "根据分类查询宠物列表，支持按类型（狗、猫、鸟等）、品种、名称搜索宠物")
    public String queryPets(
            @ToolParam(description = "宠物类型，如：狗、猫、鸟") String type,
            @ToolParam(description = "宠物品种，如：拉布拉多、金毛、英短等") String breed,
            @ToolParam(description = "宠物名称，支持模糊搜索") String name) {
        try {
            var result = petFeignClient.getPetList(type, breed, name, 1, 20);
            if (result != null && result.getCode() == 200 && result.getData() != null) {
                Map<String, Object> pageData = (Map<String, Object>) result.getData();
                List<Map<String, Object>> records = (List<Map<String, Object>>) pageData.get("records");
                if (records == null || records.isEmpty()) {
                    return "抱歉，没有找到符合条件的宠物。您可以尝试其他搜索条件。";
                }
                StringBuilder sb = new StringBuilder();
                sb.append("找到 ").append(pageData.get("total")).append(" 只可爱的").append(type != null ? type : "宠物").append("～ 快来看看它们的信息吧：\n\n");
                int idx = 1;
                for (Map<String, Object> pet : records) {
                    Object petId = pet.get("id");
                    sb.append("---\n");
                    sb.append("**").append(idx).append(". ").append(pet.get("name")).append("** ");
                    if (petId != null) {
                        sb.append("(ID: ").append(petId).append(")");
                    }
                    sb.append("\n");
                    sb.append("- 品种: ").append(pet.get("breed"));
                    if (pet.get("age") != null) sb.append("  |  年龄: ").append(pet.get("age"));
                    if (pet.get("gender") != null) sb.append("  |  性别: ").append(pet.get("gender"));
                    sb.append("\n");
                    if (pet.get("description") != null) {
                        String desc = pet.get("description").toString();
                        sb.append("- 简介: ").append(desc.length() > 80 ? desc.substring(0, 80) + "..." : desc).append("\n");
                    }
                    if (pet.get("story") != null && !pet.get("story").toString().isBlank()) {
                        String story = pet.get("story").toString();
                        sb.append("- 📖 **故事**: ").append(story.length() > 100 ? story.substring(0, 100) + "..." : story).append("\n");
                    }
                    sb.append("\n");
                    idx++;
                }
                return sb.toString();
            }
            return "查询失败，请稍后重试。";
        } catch (Exception e) {
            log.error("查询宠物失败", e);
            return "系统暂时无法查询宠物信息，请稍后再试。";
        }
    }

    @Tool(description = """
        【⚠️ 领养前必须调用】当用户想领养某只特定宠物时，先用此工具查询宠物的真实ID。
        
        使用场景：
        - 用户说"我想领养贝拉"、"我要领养叫XX的宠物"
        - 需要获取某只宠物的准确数据库ID用于领养申请
        
        ⚠️ 重要：此方法返回的ID是真实的数据库主键，必须用于startAdoptionForm的petId参数！
        """)
    public String getPetByName(@ToolParam(description = "要查找的宠物名称（精确匹配或模糊搜索）") String petName) {
        try {
            log.info("🔍 查询宠物信息: petName={}", petName);
            var result = petFeignClient.getPetList(null, null, petName, 1, 10);
            if (result != null && result.getCode() == 200 && result.getData() != null) {
                Map<String, Object> pageData = (Map<String, Object>) result.getData();
                List<Map<String, Object>> records = (List<Map<String, Object>>) pageData.get("records");
                
                if (records == null || records.isEmpty()) {
                    log.warn("未找到宠物: {}", petName);
                    return "❌ 未找到名为「" + petName + "」的宠物。\n\n请先使用 queryPets 查看平台上的可领养宠物列表，确认正确的宠物名称。";
                }
                
                Map<String, Object> exactMatch = null;
                java.util.List<Map<String, Object>> partialMatches = new java.util.ArrayList<>();
                
                for (Map<String, Object> pet : records) {
                    String name = pet.get("name") != null ? pet.get("name").toString() : "";
                    if (name.equals(petName)) {
                        exactMatch = pet;
                        break;
                    } else if (name.contains(petName) || petName.contains(name)) {
                        partialMatches.add(pet);
                    }
                }
                
                Map<String, Object> targetPet = exactMatch != null ? exactMatch : 
                    (!partialMatches.isEmpty() ? partialMatches.get(0) : records.get(0));
                
                Object id = targetPet.get("id");
                String name = targetPet.get("name") != null ? targetPet.get("name").toString() : "未知";
                String breed = targetPet.get("breed") != null ? targetPet.get("breed").toString() : "未知";
                Object age = targetPet.get("age");
                String type = targetPet.get("type") != null ? targetPet.get("type").toString() : "未知";
                
                StringBuilder sb = new StringBuilder();
                sb.append("✅ 找到宠物信息：\n\n");
                sb.append("**宠物名称**: ").append(name).append("\n");
                sb.append("**🆔 数据库ID**: ").append(id).append(" ← 这是startAdoptionForm需要的petId\n");
                sb.append("**品种**: ").append(breed).append("\n");
                sb.append("**类型**: ").append(type).append("\n");
                if (age != null) sb.append("**年龄**: ").append(age).append("\n");
                if (targetPet.get("gender") != null) sb.append("**性别**: ").append(targetPet.get("gender")).append("\n");
                
                if (exactMatch == null && records.size() > 1) {
                    sb.append("\n⚠️ 未找到完全匹配的名称，显示最接近的结果。如需查看所有匹配结果请使用 queryPets 工具。\n");
                }
                
                log.info("✅ 查询到宠物: name={}, id={}", name, id);
                return sb.toString();
            }
            return "查询失败，请稍后重试。";
        } catch (Exception e) {
            log.error("查询宠物失败", e);
            return "系统暂时无法查询宠物信息，请稍后再试。";
        }
    }

    @Tool(description = "获取所有可领养的宠物类型和品种信息，帮助用户了解平台有哪些宠物")
    public String getPetCategories() {
        try {
            var result = petFeignClient.getPetList(null, null, null, 1, 100);
            if (result != null && result.getCode() == 200 && result.getData() != null) {
                Map<String, Object> pageData = (Map<String, Object>) result.getData();
                List<Map<String, Object>> records = (List<Map<String, Object>>) pageData.get("records");
                if (records == null || records.isEmpty()) {
                    return "目前平台上还没有可领养的宠物。";
                }
                StringBuilder typeSb = new StringBuilder("📋 **平台宠物分类**\n\n");
                java.util.LinkedHashMap<String, java.util.List<String>> typeMap = new java.util.LinkedHashMap<>();
                for (Map<String, Object> pet : records) {
                    String t = pet.get("type") != null ? pet.get("type").toString() : "其他";
                    String b = pet.get("breed") != null ? pet.get("breed").toString() : "未知";
                    typeMap.computeIfAbsent(t, k -> new java.util.ArrayList<>()).add(b);
                }
                for (Map.Entry<String, List<String>> entry : typeMap.entrySet()) {
                    typeSb.append("• **").append(entry.getKey()).append("**: ");
                    typeSb.append(String.join(", ", entry.getValue().stream().distinct().toList())).append("\n");
                }
                typeSb.append("\n共 **").append(records.size()).append("** 只宠物等待领养 ❤️");
                return typeSb.toString();
            }
            return "获取宠物分类失败，请稍后重试。";
        } catch (Exception e) {
            log.error("获取宠物分类失败", e);
            return "系统暂时无法获取宠物分类信息。";
        }
    }
}
