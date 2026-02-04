-- 注意：该页面对应的前台目录为views/course文件夹下
-- 如果你想更改到其他目录，请修改sql中component字段对应的值


-- 主菜单
INSERT INTO sys_permission(id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description, status, del_flag, rule_flag, create_by, create_time, update_by, update_time, internal_or_external)
VALUES ('177018002950701', NULL, '班级选必修信息表', '/course/classCourseTypeList', 'course/ClassCourseTypeList', NULL, NULL, 0, NULL, '1', 0.00, 0, NULL, 1, 0, 0, 0, 0, NULL, '1', 0, 0, 'admin', '2026-02-04 12:40:29', NULL, NULL, 0);

-- 新增
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('177018002950702', '177018002950701', '添加班级选必修信息表', NULL, NULL, 0, NULL, NULL, 2, 'course:class_course_type:add', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-02-04 12:40:29', NULL, NULL, 0, 0, '1', 0);

-- 编辑
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('177018002950703', '177018002950701', '编辑班级选必修信息表', NULL, NULL, 0, NULL, NULL, 2, 'course:class_course_type:edit', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-02-04 12:40:29', NULL, NULL, 0, 0, '1', 0);

-- 删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('177018002950704', '177018002950701', '删除班级选必修信息表', NULL, NULL, 0, NULL, NULL, 2, 'course:class_course_type:delete', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-02-04 12:40:29', NULL, NULL, 0, 0, '1', 0);

-- 批量删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('177018002950705', '177018002950701', '批量删除班级选必修信息表', NULL, NULL, 0, NULL, NULL, 2, 'course:class_course_type:deleteBatch', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-02-04 12:40:29', NULL, NULL, 0, 0, '1', 0);

-- 导出excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('177018002950706', '177018002950701', '导出excel_班级选必修信息表', NULL, NULL, 0, NULL, NULL, 2, 'course:class_course_type:exportXls', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-02-04 12:40:29', NULL, NULL, 0, 0, '1', 0);

-- 导入excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('177018002950707', '177018002950701', '导入excel_班级选必修信息表', NULL, NULL, 0, NULL, NULL, 2, 'course:class_course_type:importExcel', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-02-04 12:40:29', NULL, NULL, 0, 0, '1', 0);

-- 角色授权（以 admin 角色为例，role_id 可替换）
INSERT INTO sys_role_permission (id, role_id, permission_id, data_rule_ids, operate_date, operate_ip) VALUES ('177018002950808', 'f6817f48af4fb3af11b9e8bf182f618b', '177018002950701', NULL, '2026-02-04 12:40:29', '127.0.0.1');
INSERT INTO sys_role_permission (id, role_id, permission_id, data_rule_ids, operate_date, operate_ip) VALUES ('177018002950809', 'f6817f48af4fb3af11b9e8bf182f618b', '177018002950702', NULL, '2026-02-04 12:40:29', '127.0.0.1');
INSERT INTO sys_role_permission (id, role_id, permission_id, data_rule_ids, operate_date, operate_ip) VALUES ('177018002950810', 'f6817f48af4fb3af11b9e8bf182f618b', '177018002950703', NULL, '2026-02-04 12:40:29', '127.0.0.1');
INSERT INTO sys_role_permission (id, role_id, permission_id, data_rule_ids, operate_date, operate_ip) VALUES ('177018002950811', 'f6817f48af4fb3af11b9e8bf182f618b', '177018002950704', NULL, '2026-02-04 12:40:29', '127.0.0.1');
INSERT INTO sys_role_permission (id, role_id, permission_id, data_rule_ids, operate_date, operate_ip) VALUES ('177018002950812', 'f6817f48af4fb3af11b9e8bf182f618b', '177018002950705', NULL, '2026-02-04 12:40:29', '127.0.0.1');
INSERT INTO sys_role_permission (id, role_id, permission_id, data_rule_ids, operate_date, operate_ip) VALUES ('177018002950813', 'f6817f48af4fb3af11b9e8bf182f618b', '177018002950706', NULL, '2026-02-04 12:40:29', '127.0.0.1');
INSERT INTO sys_role_permission (id, role_id, permission_id, data_rule_ids, operate_date, operate_ip) VALUES ('177018002950814', 'f6817f48af4fb3af11b9e8bf182f618b', '177018002950707', NULL, '2026-02-04 12:40:29', '127.0.0.1');