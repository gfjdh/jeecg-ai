-- 注意：该页面对应的前台目录为views/course文件夹下
-- 如果你想更改到其他目录，请修改sql中component字段对应的值


-- 主菜单
INSERT INTO sys_permission(id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description, status, del_flag, rule_flag, create_by, create_time, update_by, update_time, internal_or_external)
VALUES ('177017461891101', NULL, '教师课程安排', '/course/teacherCourseList', 'course/TeacherCourseList', NULL, NULL, 0, NULL, '1', 0.00, 0, NULL, 1, 0, 0, 0, 0, NULL, '1', 0, 0, 'admin', '2026-02-04 11:10:18', NULL, NULL, 0);

-- 新增
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('177017461891102', '177017461891101', '添加教师课程安排', NULL, NULL, 0, NULL, NULL, 2, 'course:teacher_course:add', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-02-04 11:10:18', NULL, NULL, 0, 0, '1', 0);

-- 编辑
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('177017461891103', '177017461891101', '编辑教师课程安排', NULL, NULL, 0, NULL, NULL, 2, 'course:teacher_course:edit', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-02-04 11:10:18', NULL, NULL, 0, 0, '1', 0);

-- 删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('177017461891104', '177017461891101', '删除教师课程安排', NULL, NULL, 0, NULL, NULL, 2, 'course:teacher_course:delete', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-02-04 11:10:18', NULL, NULL, 0, 0, '1', 0);

-- 批量删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('177017461891105', '177017461891101', '批量删除教师课程安排', NULL, NULL, 0, NULL, NULL, 2, 'course:teacher_course:deleteBatch', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-02-04 11:10:18', NULL, NULL, 0, 0, '1', 0);

-- 导出excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('177017461891106', '177017461891101', '导出excel_教师课程安排', NULL, NULL, 0, NULL, NULL, 2, 'course:teacher_course:exportXls', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-02-04 11:10:18', NULL, NULL, 0, 0, '1', 0);

-- 导入excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('177017461891107', '177017461891101', '导入excel_教师课程安排', NULL, NULL, 0, NULL, NULL, 2, 'course:teacher_course:importExcel', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-02-04 11:10:18', NULL, NULL, 0, 0, '1', 0);

-- 角色授权（以 admin 角色为例，role_id 可替换）
INSERT INTO sys_role_permission (id, role_id, permission_id, data_rule_ids, operate_date, operate_ip) VALUES ('177017461891208', 'f6817f48af4fb3af11b9e8bf182f618b', '177017461891101', NULL, '2026-02-04 11:10:18', '127.0.0.1');
INSERT INTO sys_role_permission (id, role_id, permission_id, data_rule_ids, operate_date, operate_ip) VALUES ('177017461891209', 'f6817f48af4fb3af11b9e8bf182f618b', '177017461891102', NULL, '2026-02-04 11:10:18', '127.0.0.1');
INSERT INTO sys_role_permission (id, role_id, permission_id, data_rule_ids, operate_date, operate_ip) VALUES ('177017461891210', 'f6817f48af4fb3af11b9e8bf182f618b', '177017461891103', NULL, '2026-02-04 11:10:18', '127.0.0.1');
INSERT INTO sys_role_permission (id, role_id, permission_id, data_rule_ids, operate_date, operate_ip) VALUES ('177017461891211', 'f6817f48af4fb3af11b9e8bf182f618b', '177017461891104', NULL, '2026-02-04 11:10:18', '127.0.0.1');
INSERT INTO sys_role_permission (id, role_id, permission_id, data_rule_ids, operate_date, operate_ip) VALUES ('177017461891212', 'f6817f48af4fb3af11b9e8bf182f618b', '177017461891105', NULL, '2026-02-04 11:10:18', '127.0.0.1');
INSERT INTO sys_role_permission (id, role_id, permission_id, data_rule_ids, operate_date, operate_ip) VALUES ('177017461891213', 'f6817f48af4fb3af11b9e8bf182f618b', '177017461891106', NULL, '2026-02-04 11:10:18', '127.0.0.1');
INSERT INTO sys_role_permission (id, role_id, permission_id, data_rule_ids, operate_date, operate_ip) VALUES ('177017461891214', 'f6817f48af4fb3af11b9e8bf182f618b', '177017461891107', NULL, '2026-02-04 11:10:18', '127.0.0.1');