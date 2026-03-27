-- 更新关于我们配置数据
-- 先删除旧配置
DELETE FROM sys_config WHERE config_key IN ('about_us', 'about_intro', 'about_intro_image', 'about_status', 'about_status_stat1', 'about_status_stat1_label', 'about_status_stat2', 'about_status_stat2_label', 'about_status_stat3', 'about_status_stat3_label', 'about_history', 'about_news', 'about_contact', 'about_contact_image');

-- 插入新配置
INSERT INTO sys_config (config_key, config_value, config_name, description) VALUES
('about_intro', '爪印之家是一家致力于为流浪、被遗弃和被送养的动物提供安全庇护所的非营利宠物福利组织。我们的使命建立在每一个动物都值得拥有一个充满爱的永久家园的信念之上。', '关于我们-简介', '关于我们页面简介内容'),
('about_intro_image', 'https://images.unsplash.com/photo-1528301721190-1c922d627ea4?auto=format&fit=crop&w=1200&q=80', '关于我们-简介图片', '关于我们页面简介图片'),
('about_status', '目前，我们正在满负荷运营，每天持续接收新动物，同时严格审核领养申请。', '关于我们-当前状况', '关于我们页面当前状况内容'),
('about_status_stat1', '5,000+', '当前状况-统计1数值', '成功领养数量'),
('about_status_stat1_label', '成功领养', '当前状况-统计1标签', '成功领养标签'),
('about_status_stat2', '12', '当前状况-统计2数值', '收容所数量'),
('about_status_stat2_label', '收容所', '当前状况-统计2标签', '收容所标签'),
('about_status_stat3', '200+', '当前状况-统计3数值', '志愿者数量'),
('about_status_stat3_label', '志愿者', '当前状况-统计3标签', '志愿者标签'),
('about_history', '2018年：爪印之家正式成立，最初只有一个小型收容所。\n2019年：扩展到3个收容所，成功帮助500只动物找到新家。\n2020年：建立志愿者网络，志愿者人数突破100人。\n2021年：开设宠物医疗中心，提供专业医疗服务。\n2022年：成功领养数量突破3000只。\n2023年：成为本地区最大的宠物福利组织。', '关于我们-发展历程', '关于我们页面发展历程内容'),
('about_news', '2024年春季领养活动即将开启\n我们将于4月举办大型领养活动，届时将有超过100只可爱的小动物等待新家。\n\n新医疗中心正式运营\n我们的第二医疗中心已于3月正式投入使用，为更多宠物提供医疗服务。\n\n志愿者招募进行中\n我们正在招募更多爱心志愿者，欢迎加入我们的大家庭。', '关于我们-最新动态', '关于我们页面最新动态内容'),
('about_contact', '地址：北京市朝阳区宠物街123号\n电话：400-123-4567\n邮箱：contact@pawhome.com\n工作时间：周一至周日 9:00-18:00', '关于我们-联系我们', '关于我们页面联系我们内容'),
('about_contact_image', 'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&w=1200&q=80', '关于我们-联系我们图片', '关于我们页面联系我们图片');
