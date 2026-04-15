package com.pet.chat.ai.tool;

import com.pet.chat.ai.context.RequestContext;
import com.pet.chat.ai.entity.Adoption;
import com.pet.chat.ai.mapper.AdoptionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class AdoptionFormTool {

    private final ConcurrentHashMap<String, ConcurrentHashMap<String, String>> userForms = new ConcurrentHashMap<>();

    @Autowired
    private AdoptionMapper adoptionMapper;

    private Long getRealUserId(Long aiProvidedUserId) {
        Long realUserId = RequestContext.getUserId();
        if (realUserId != null) {
            log.debug("使用系统真实userId: {}, AI提供的userId: {}", realUserId, aiProvidedUserId);
            return realUserId;
        }
        log.warn("⚠️ 系统userId为空，使用AI提供的userId: {}", aiProvidedUserId);
        return aiProvidedUserId != null ? aiProvidedUserId : 0L;
    }

    @Tool(description = """
        【开始新表单】当用户明确表示要领养某只宠物时调用。
        
        ⚠️⚠️⚠️ 必须先通过queryPets查询到宠物列表后才能调用此方法！
        
        参数说明：
        - petId: 宠物的真实数据库ID（必须从queryPets返回结果中的"ID: xxx"获取）
        - petName: 宠物的名称（必须与queryPets返回的名称完全一致）
        
        ✅ 正确示例：
        1. 先调用 queryPets() 获取宠物列表
        2. 用户说"我想领养贝拉"
        3. 从queryPets结果中找到: "贝拉 (ID: 5)"
        4. 调用 startAdoptionForm(petId=5, petName="贝拉")
        
        ❌ 错误示例：
        - 没有调用queryPets就直接开始表单
        - petId和petName不匹配（如petId=3但petName="贝拉"，而ID=3实际是查理）
        - 编造或猜测petId
        
        ⚠️ 如果不确定正确的petId，请先调用queryPets确认后再开始表单。
        """)
    public String startAdoptionForm(
            @ToolParam(description = "宠物的真实数据库ID（必须从queryPets结果的'ID: xxx'中获取）") Long petId,
            @ToolParam(description = "宠物的准确名称（必须与queryPets返回的名称一致）") String petName) {
        Long userId = getRealUserId(null);
        
        log.info("🎯 收到开始表单请求: userId={}, AI传入的petId={}, petName={}", userId, petId, petName);
        
        if (petId == null || petId <= 0) {
            log.error("❌ 无效的petId: {}，AI可能传入了null或负数", petId);
            return "❌ **错误：无效的宠物ID**\n\n请先使用查询宠物功能找到您想领养的宠物，然后告诉我宠物的名称或ID。";
        }
        
        if (petName == null || petName.isBlank()) {
            log.warn("⚠️ petName为空，建议AI同时提供petName以便确认");
        }
        
        if (petId > 10000) {
            log.warn("⚠️ petId异常大: {}，可能是AI编造的ID，petName={}", petId, petName);
        }
        
        String formKey = userId + "_" + petId;
        userForms.remove(formKey);
        ConcurrentHashMap<String, String> form = new ConcurrentHashMap<>();
        form.put("petId", String.valueOf(petId));
        form.put("userId", String.valueOf(userId));
        form.put("status", "filling");
        if (petName != null && !petName.isBlank()) {
            form.put("petName", petName);
        }
        userForms.put(formKey, form);

        log.info("📝 开始领养表单: userId={}, petId={}, petName={}, formKey={}", userId, petId, petName, formKey);

        String petInfo = (petName != null && !petName.isBlank()) 
            ? "**" + petName + "** (ID: " + petId + ")" 
            : "ID: " + petId;

        return """
                📝 **宠物领养申请表单**
                
                🐾 您正在申请领养：""" + petInfo + """
                
                感谢您决定给一只可爱的小动物一个温暖的家！请填写以下信息：
                
                **必填信息：**
                1. **姓名** - 您的真实姓名
                2. **联系电话** - 您的手机号码 📱
                3. **电子邮箱** - 您的邮箱地址 📧
                4. **年龄** - 申请人的年龄
                5. **居住地址** - 您的详细居住地址 🏠
                6. **住房类型**（如：自有房屋、租房、公寓等）
                7. **是否有养宠经验**（如：有/无/有一定经验）
                8. **家庭成员情况**（如：独居、与家人同住、有小孩等）
                9. **领养原因** - 请告诉我们为什么想领养这只宠物 ❤️
                
                **承诺事项：**
                10. **是否能够做到以下几点？**
                    - 定期带宠物去做健康检查和接种疫苗 💉
                    - 提供充足的食物和水 🍖
                    - 给予足够的关爱和陪伴时间 💕
                    - 不随意遗弃或转送宠物
                
                请逐项告诉我您的信息，我会帮您完成填写。
                """;
    }

    @Tool(description = """
        【填写表单项】当用户提供某项信息的具体内容时调用此方法。
        petId：当前填写的宠物ID（必须与startAdoptionForm时使用的petId一致，如果不确定可传null）
        field：字段名称
        value：用户提供的值
        """)
    public String fillAdoptionFormField(
            @ToolParam(description = "宠物ID（必须与开始表单时一致，不确定可传null）") Long petId,
            @ToolParam(description = "字段名称: name, phone, email, age, address, housingType, hasPetExperience, familyStatus, reason, commitment") String field,
            @ToolParam(description = "用户提供的该字段的值") String value) {
        Long userId = getRealUserId(null);
        
        String formKey = userId + "_" + petId;
        ConcurrentHashMap<String, String> form = userForms.get(formKey);
        
        if (form == null || !"filling".equals(form.get("status"))) {
            log.warn("⚠️ 精确匹配表单失败: userId={}, petId={}, 尝试模糊查找...", userId, petId);
            
            for (String key : userForms.keySet()) {
                if (key.startsWith(userId + "_")) {
                    ConcurrentHashMap<String, String> candidateForm = userForms.get(key);
                    if ("filling".equals(candidateForm.get("status"))) {
                        form = candidateForm;
                        formKey = key;
                        log.info("✅ 模糊查找成功: 找到表单 key={}", key);
                        
                        String savedPetIdStr = candidateForm.get("petId");
                        if (savedPetIdStr != null && !savedPetIdStr.isBlank()) {
                            try {
                                petId = Long.parseLong(savedPetIdStr);
                            } catch (NumberFormatException e) {
                                log.warn("⚠️ 表单中的petId格式异常: {}", savedPetIdStr);
                            }
                        }
                        break;
                    }
                }
            }
        }
        
        if (form == null || !"filling".equals(form.get("status"))) {
            return "❌ 请先开始领养表单流程（使用 startAdoptionForm 开始新表单）。";
        }

        String fieldName = switch (field.toLowerCase()) {
            case "name" -> "姓名";
            case "phone" -> "联系电话";
            case "email" -> "电子邮箱";
            case "age" -> "年龄";
            case "address" -> "居住地址";
            case "housingtype" -> "住房类型";
            case "haspetexperience" -> "养宠经验";
            case "familystatus" -> "家庭情况";
            case "reason" -> "领养原因";
            case "commitment" -> "承诺事项";
            default -> field;
        };

        form.put(field, value);
        
        int filledCount = 0;
        String[] allFields = {"name", "phone", "email", "age", "address", "housingType", "hasPetExperience", "familyStatus", "reason", "commitment"};
        for (String f : allFields) {
            if (form.containsKey(f) && form.get(f) != null && !form.get(f).isBlank()) {
                filledCount++;
            }
        }
        
        StringBuilder response = new StringBuilder();
        response.append("✅ 已记录「**").append(fieldName).append("**」: ").append(value).append("\n\n");
        response.append("📊 进度: ").append(filledCount).append("/10 项已填写\n\n");
        
        if (filledCount < 10) {
            int remaining = 10 - filledCount;
            response.append("还有 ").append(remaining).append(" 项信息需要填写。\n");
            
            String[] fields = {"name", "phone", "email", "age", "address", "housingType", "hasPetExperience", "familyStatus", "reason", "commitment"};
            String[] labels = {"姓名", "联系电话", "电子邮箱", "年龄", "居住地址", "住房类型", "养宠经验", "家庭情况", "领养原因", "承诺事项"};
            
            response.append("接下来请告诉我您的「**");
            for (int i = 0; i < fields.length; i++) {
                String val = form.get(fields[i]);
                if (val == null || val.isBlank()) {
                    response.append(labels[i]).append("**」");
                    break;
                }
            }
        } else {
            response.append("✨ 所有信息已填写完整！\n");
            response.append("您可以说\"提交\"、\"好了\"、\"可以了\"或\"确认提交\"来完成申请。");
        }
        
        return response.toString();
    }

    @Tool(description = """ 
        【查看表单进度】仅在用户主动要求查看已填写内容时使用。
        
        ⚠️ 使用场景限制：
        ✅ 适用：用户说"查看"、"看看填了什么"、"显示表单"、"进度"
        ❌ 不适用：用户说"提交"、"好了"、"确认"等提交意图时（此时必须用submitAdoptionForm）
        
        petId：宠物ID（如果不确定可传null）
        """)
    public String reviewAdoptionForm(@ToolParam(description = "宠物ID（可选）") Long petId) {
        Long userId = getRealUserId(null);
        
        String formKey = userId + "_" + petId;
        ConcurrentHashMap<String, String> form = userForms.get(formKey);
        
        if (form == null && petId != null) {
            log.warn("⚠️ review精确匹配失败，尝试模糊查找: userId={}", userId);
            for (String key : userForms.keySet()) {
                if (key.startsWith(userId + "_")) {
                    form = userForms.get(key);
                    formKey = key;
                    log.info("✅ review模糊查找成功: key={}", key);
                    break;
                }
            }
        }
        
        if (form == null) {
            return "❌ 未找到该表单，请重新开始填写（使用 startAdoptionForm）。";
        }

        StringBuilder sb = new StringBuilder("📋 **当前表单内容**\n\n");
        
        String[] fields = {"name", "phone", "email", "age", "address", "housingType", "hasPetExperience", "familyStatus", "reason", "commitment"};
        String[] labels = {"姓名", "联系电话", "电子邮箱", "年龄", "居住地址", "住房类型", "养宠经验", "家庭情况", "领养原因", "承诺事项"};
        
        int filledCount = 0;
        for (int i = 0; i < fields.length; i++) {
            String val = form.get(fields[i]);
            if (val != null && !val.isBlank()) {
                sb.append("✅ **").append(labels[i]).append("**: ").append(val).append("\n");
                filledCount++;
            } else {
                sb.append("⏳ **").append(labels[i]).append("**: 待填写\n");
            }
        }
        
        sb.append("\n---\n");
        sb.append("📊 填写进度: ").append(filledCount).append("/10\n");
        
        if (filledCount < 10) {
            sb.append("\n⚠️ 还有 ").append(10 - filledCount).append(" 项信息需要填写，全部填完后才能提交。");
        } else {
            sb.append("\n✨ 所有信息已填写完整！您可以说\"提交\"来完成申请。");
        }
        return sb.toString();
    }

    @Tool(description = """
        【⚠️ 最高优先级 - 立即提交表单】
        
        ⚠️⚠️⚠️ 当检测到以下任一情况时，必须立即调用此方法（最高优先级）：
        1. 用户明确说："提交"、"好了"、"可以了"、"确认"、"完成"、"OK"、"行"、"确定"、"发送"
        2. 用户说"所有信息都填好了"、"填完了"、"可以提交了"
        3. 表单10项全部已填写完成，且用户表达任何形式的确认或同意
        
        🚫 绝对禁止的行为：
        - ❌ 不要调用reviewAdoptionForm查看表单（直接提交即可）
        - ❌ 不要让用户再次确认或补充信息
        - ❌ 不要说"现在可以正式提交了"然后等待用户再说一次
        
        ✅ 正确行为：
        - 检测到提交意图 → 立即调用此方法 → 返回提交结果
        
        petId参数：宠物ID（如果不确定，传null或0，系统会自动查找正确的表单）
        """)
    public String submitAdoptionForm(@ToolParam(description = "宠物ID（可选，如果不确定传null）") Long petId) {
        Long userId = getRealUserId(null);

        log.info("🔍 提交表单请求: userId={}, AI提供的petId={}", userId, petId);

        ConcurrentHashMap<String, String> form = null;
        String actualFormKey = null;

        if (petId != null && petId > 0) {
            String exactKey = userId + "_" + petId;
            form = userForms.get(exactKey);
            actualFormKey = exactKey;
            log.info("🔍 尝试精确匹配: formKey={}, 找到={}", exactKey, form != null);
        }

        if (form == null) {
            log.warn("⚠️ 精确匹配失败（可能AI传了错误的petId），开始模糊查找用户的未完成表单...");
            
            java.util.List<String> candidateKeys = new java.util.ArrayList<>();
            for (String key : userForms.keySet()) {
                if (key.startsWith(userId + "_")) {
                    ConcurrentHashMap<String, String> candidateForm = userForms.get(key);
                    String status = candidateForm.get("status");
                    log.debug("🔍 发现用户表单: key={}, status={}", key, status);
                    
                    if ("filling".equals(status)) {
                        candidateKeys.add(key);
                    }
                }
            }

            if (!candidateKeys.isEmpty()) {
                actualFormKey = candidateKeys.get(0);
                form = userForms.get(actualFormKey);
                
                String savedPetIdStr = form.get("petId");
                if (savedPetIdStr != null && !savedPetIdStr.isBlank()) {
                    try {
                        petId = Long.parseLong(savedPetIdStr);
                        log.info("✅ 从表单中恢复正确的petId: {}", petId);
                    } catch (NumberFormatException e) {
                        log.warn("⚠️ 表单中的petId格式异常: {}", savedPetIdStr);
                    }
                }
                log.info("✅ 模糊查找成功: formKey={}", actualFormKey);
            }
        }
        
        if (form == null) {
            log.error("❌ 未找到任何可提交的表单: userId={}", userId);
            
            for (String key : userForms.keySet()) {
                log.debug("现有表单key: {}", key);
            }
            
            return "❌ **提交失败！** 未找到您的领养申请表单。\n\n可能的原因：\n• 您还没有开始填写领养申请\n• 之前的会话已过期\n\n请先告诉我您想领养哪只宠物，我会帮您开始新的申请流程。";
        }

        if (!"filling".equals(form.get("status"))) {
            String applicationId = form.get("applicationId");
            log.info("ℹ️ 表单已提交过: formKey={}, status={}, applicationId={}", actualFormKey, form.get("status"), applicationId);
            
            StringBuilder successMsg = new StringBuilder();
            successMsg.append("✅ **您的领养申请已成功提交！**\n\n");
            successMsg.append("---\n");
            successMsg.append("**申请人**: ").append(form.get("name")).append("\n");
            successMsg.append("**联系电话**: ").append(form.get("phone")).append("\n");
            successMsg.append("**电子邮箱**: ").append(form.get("email")).append("\n");
            successMsg.append("**申请ID**: ").append(applicationId).append("\n");
            successMsg.append("---\n\n");
            successMsg.append("📋 我们的工作人员会尽快审核您的申请（通常1-3个工作日）。\n");
            successMsg.append("您可以在「个人中心-申请记录」中查看进度。\n\n");
            successMsg.append("💕 感谢您的爱心与责任！每一份领养都是一次生命的救赎 🐾");
            
            return successMsg.toString();
        }

        log.info("📋 找到待提交的表单: formKey={}, petId={}", actualFormKey, petId);

        String[] requiredFields = {"name", "phone", "email", "age", "address", "housingType", "hasPetExperience", "familyStatus", "reason", "commitment"};
        String[] requiredLabels = {"姓名", "联系电话", "电子邮箱", "年龄", "居住地址", "住房类型", "养宠经验", "家庭情况", "领养原因", "承诺事项"};
        
        StringBuilder missingFields = new StringBuilder();
        for (int i = 0; i < requiredFields.length; i++) {
            String val = form.get(requiredFields[i]);
            if (val == null || val.isBlank()) {
                missingFields.append("• ").append(requiredLabels[i]).append("\n");
            }
        }
        
        if (missingFields.length() > 0) {
            long missingCount = missingFields.toString().lines().count();
            return "❌ **还有 " + missingCount + " 项信息未填写**：\n" + missingFields + "\n请继续补充这些信息后再说\"提交\"。";
        }

        try {
            log.info("📝 开始保存领养申请到数据库... userId={}, petId={}", userId, petId);
            log.info("📝 表单数据详情: {}", form);

            Adoption adoption = new Adoption();
            adoption.setUserId(userId);
            adoption.setPetId(petId);
            adoption.setStatus(0);
            
            String phone = form.get("phone");
            adoption.setPhone(phone != null ? phone.trim() : "");
            
            String email = form.get("email");
            adoption.setEmail(email != null ? email.trim() : "");
            
            String ageStr = form.get("age");
            if (ageStr != null && !ageStr.isBlank()) {
                try {
                    String ageClean = ageStr.replaceAll("[^0-9]", "");
                    if (!ageClean.isBlank()) {
                        adoption.setApplicantAge(Integer.parseInt(ageClean));
                    }
                } catch (NumberFormatException e) {
                    log.warn("⚠️ 年龄格式异常: {}, 使用null", ageStr);
                }
            }
            
            String address = form.get("address");
            adoption.setAddress(address != null ? address.trim() : "");
            
            String housingType = form.get("housingType");
            adoption.setHousingType(housingType != null ? housingType.trim() : "");
            
            String hasPetExperience = form.get("hasPetExperience");
            adoption.setHasPetExperience(hasPetExperience != null ? hasPetExperience.trim() : "");
            
            String familyStatus = form.get("familyStatus");
            adoption.setFamilyStatus(familyStatus != null ? familyStatus.trim() : "");
            
            String reason = form.get("reason");
            adoption.setReason(reason != null ? reason.trim() : "");
            
            String commitment = form.get("commitment");
            log.info("📝 承诺事项原始内容: {}", commitment);
            
            if (commitment != null && !commitment.isBlank()) {
                String commitmentLower = commitment.toLowerCase().trim();
                
                boolean hasNegative = commitmentLower.contains("不") || commitmentLower.contains("不能") 
                    || commitmentLower.contains("不会") || commitmentLower.contains("不同意") 
                    || commitmentLower.contains("没办法") || commitmentLower.contains("不愿意");
                    
                boolean hasPositiveGeneral = commitmentLower.contains("可以") || commitmentLower.contains("好的") 
                    || commitmentLower.contains("同意") || commitmentLower.contains("会") || commitmentLower.contains("能") 
                    || commitmentLower.contains("愿意") || commitmentLower.contains("没问题") || commitmentLower.contains("一定") 
                    || commitmentLower.contains("保证") || commitmentLower.contains("承诺") || commitmentLower.contains("答应") 
                    || commitmentLower.contains("ok") || commitmentLower.contains("行") || commitmentLower.contains("是的")
                    || commitmentLower.contains("对") || commitmentLower.contains("当然");
                
                boolean hasSpecificKeywords = commitment.contains("体检") || commitment.contains("健康检查") 
                    || commitment.contains("疫苗") || commitment.contains("检查") || commitment.contains("看病")
                    || commitment.contains("绝育") || commitment.contains("食物") || commitment.contains("水") 
                    || commitment.contains("环境") || commitment.contains("饲养") || commitment.contains("照顾")
                    || commitment.contains("医疗") || commitment.contains("及时") || commitment.contains("生病") 
                    || commitment.contains("治疗");
                
                boolean defaultAgree;
                
                if (hasNegative && !hasPositiveGeneral) {
                    defaultAgree = false;
                    log.info("📝 检测到明确否定词，默认不同意: {}", commitment);
                } else if (hasPositiveGeneral || hasSpecificKeywords) {
                    defaultAgree = true;
                    log.info("📝 检测到肯定词或具体承诺内容，默认同意: {}", commitment);
                } else {
                    defaultAgree = true;
                    log.info("📝 未检测到明确否定，默认同意（用户已填写承诺项）: {}", commitment);
                }
                
                boolean agreeHealthCheck = defaultAgree || commitment.contains("体检") || commitment.contains("健康检查") 
                    || commitment.contains("疫苗") || commitment.contains("检查") || commitment.contains("看病");
                boolean agreeNeuter = defaultAgree || commitment.contains("绝育") || commitment.contains("绝育手术");
                boolean agreeGoodEnvironment = defaultAgree || commitment.contains("食物") || commitment.contains("水") 
                    || commitment.contains("环境") || commitment.contains("饲养") || commitment.contains("照顾");
                boolean agreeTimelyMedical = defaultAgree || commitment.contains("医疗") || commitment.contains("及时") 
                    || commitment.contains("生病") || commitment.contains("治疗");
                
                adoption.setAgreeHealthCheck(agreeHealthCheck);
                adoption.setAgreeNeuter(agreeNeuter);
                adoption.setAgreeGoodEnvironment(agreeGoodEnvironment);
                adoption.setAgreeTimelyMedical(agreeTimelyMedical);
                
                log.info("📝 承诺事项解析结果: healthCheck={}, neuter={}, environment={}, timelyMedical={}", 
                         agreeHealthCheck, agreeNeuter, agreeGoodEnvironment, agreeTimelyMedical);
            } else {
                adoption.setAgreeHealthCheck(true);
                adoption.setAgreeNeuter(true);
                adoption.setAgreeGoodEnvironment(true);
                adoption.setAgreeTimelyMedical(true);
                log.info("📝 承诺事项为空，默认全部同意");
            }
            
            log.info("🔍 准备插入的数据: userId={}, petId={}, phone={}, email={}, age={}, status={}", 
                     adoption.getUserId(), adoption.getPetId(), adoption.getPhone(), 
                     adoption.getEmail(), adoption.getApplicantAge(), adoption.getStatus());
            
            int rows = adoptionMapper.insert(adoption);
            log.info("💾 数据库插入结果: 影响行数={}, 生成的申请ID={}", rows, adoption.getId());
            
            if (rows <= 0 || adoption.getId() == null) {
                throw new RuntimeException("数据库插入失败，未生成申请ID");
            }
            
            form.put("status", "submitted");
            form.put("applicationId", adoption.getId().toString());
            
            log.info("✅ 领养申请已成功保存到数据库！用户ID: {}, 宠物ID: {}, 申请ID: {}", userId, petId, adoption.getId());
            
            StringBuilder sb = new StringBuilder();
            sb.append("🎉 **领养申请已成功提交！**\n\n");
            sb.append("---\n");
            sb.append("**申请人**: ").append(form.get("name")).append("\n");
            sb.append("**联系电话**: ").append(form.get("phone")).append("\n");
            sb.append("**电子邮箱**: ").append(form.get("email")).append("\n");
            sb.append("**年龄**: ").append(form.get("age")).append("\n");
            sb.append("**居住地址**: ").append(form.get("address")).append("\n");
            sb.append("**住房类型**: ").append(form.get("housingType")).append("\n");
            sb.append("**养宠经验**: ").append(form.get("hasPetExperience")).append("\n");
            sb.append("**家庭情况**: ").append(form.get("familyStatus")).append("\n");
            sb.append("**领养原因**: ").append(form.get("reason")).append("\n");
            sb.append("**您的承诺**: ").append(form.get("commitment")).append("\n");
            sb.append("---\n\n");
            sb.append("✅ 我们的工作人员会尽快审核您的申请（通常1-3个工作日）。\n");
            sb.append("审核通过后我们会通过电话或邮件联系您安排后续事宜。\n\n");
            sb.append("💾 您的申请记录已保存，可以在「个人中心-申请记录」中查看。\n");
            sb.append("感谢您的爱心与责任！每一份领养都是一次生命的救赎 🐾💕");

            return sb.toString();
        } catch (Exception e) {
            log.error("❌ 保存领养申请到数据库失败！userId={}, petId={}, error={}", userId, petId, e.getMessage(), e);
            return "❌ **提交失败！** 系统在保存您的申请时出现错误：" + e.getMessage() + "\n请稍后重试或联系客服。";
        }
    }
}