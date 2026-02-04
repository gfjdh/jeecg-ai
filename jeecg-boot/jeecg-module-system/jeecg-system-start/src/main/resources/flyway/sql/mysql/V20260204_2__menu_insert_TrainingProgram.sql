-- 注意：该页面对应的前台目录为views/course文件夹下
-- 如果你想更改到其他目录，请修改sql中component字段对应的值


-- 主菜单
INSERT INTO sys_permission(id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description, status, del_flag, rule_flag, create_by, create_time, update_by, update_time, internal_or_external)
VALUES ('177017332520201', NULL, '培养方案表', '/course/trainingProgramList', 'course/TrainingProgramList', NULL, NULL, 0, NULL, '1', 0.00, 0, NULL, 1, 0, 0, 0, 0, NULL, '1', 0, 0, 'admin', '2026-02-04 10:48:45', NULL, NULL, 0);

-- 新增
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('177017332520202', '177017332520201', '添加培养方案表', NULL, NULL, 0, NULL, NULL, 2, 'course:training_program:add', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-02-04 10:48:45', NULL, NULL, 0, 0, '1', 0);

-- 编辑
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('177017332520203', '177017332520201', '编辑培养方案表', NULL, NULL, 0, NULL, NULL, 2, 'course:training_program:edit', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-02-04 10:48:45', NULL, NULL, 0, 0, '1', 0);

-- 删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('177017332520204', '177017332520201', '删除培养方案表', NULL, NULL, 0, NULL, NULL, 2, 'course:training_program:delete', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-02-04 10:48:45', NULL, NULL, 0, 0, '1', 0);

-- 批量删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('177017332520205', '177017332520201', '批量删除培养方案表', NULL, NULL, 0, NULL, NULL, 2, 'course:training_program:deleteBatch', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-02-04 10:48:45', NULL, NULL, 0, 0, '1', 0);

-- 导出excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('177017332520206', '177017332520201', '导出excel_培养方案表', NULL, NULL, 0, NULL, NULL, 2, 'course:training_program:exportXls', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-02-04 10:48:45', NULL, NULL, 0, 0, '1', 0);

-- 导入excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('177017332520207', '177017332520201', '导入excel_培养方案表', NULL, NULL, 0, NULL, NULL, 2, 'course:training_program:importExcel', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-02-04 10:48:45', NULL, NULL, 0, 0, '1', 0);

-- 角色授权（以 admin 角色为例，role_id 可替换）
INSERT INTO sys_role_permission (id, role_id, permission_id, data_rule_ids, operate_date, operate_ip) VALUES ('177017332520308', 'f6817f48af4fb3af11b9e8bf182f618b', '177017332520201', NULL, '2026-02-04 10:48:45', '127.0.0.1');
INSERT INTO sys_role_permission (id, role_id, permission_id, data_rule_ids, operate_date, operate_ip) VALUES ('177017332520309', 'f6817f48af4fb3af11b9e8bf182f618b', '177017332520202', NULL, '2026-02-04 10:48:45', '127.0.0.1');
INSERT INTO sys_role_permission (id, role_id, permission_id, data_rule_ids, operate_date, operate_ip) VALUES ('177017332520310', 'f6817f48af4fb3af11b9e8bf182f618b', '177017332520203', NULL, '2026-02-04 10:48:45', '127.0.0.1');
INSERT INTO sys_role_permission (id, role_id, permission_id, data_rule_ids, operate_date, operate_ip) VALUES ('177017332520311', 'f6817f48af4fb3af11b9e8bf182f618b', '177017332520204', NULL, '2026-02-04 10:48:45', '127.0.0.1');
INSERT INTO sys_role_permission (id, role_id, permission_id, data_rule_ids, operate_date, operate_ip) VALUES ('177017332520312', 'f6817f48af4fb3af11b9e8bf182f618b', '177017332520205', NULL, '2026-02-04 10:48:45', '127.0.0.1');
INSERT INTO sys_role_permission (id, role_id, permission_id, data_rule_ids, operate_date, operate_ip) VALUES ('177017332520313', 'f6817f48af4fb3af11b9e8bf182f618b', '177017332520206', NULL, '2026-02-04 10:48:45', '127.0.0.1');
INSERT INTO sys_role_permission (id, role_id, permission_id, data_rule_ids, operate_date, operate_ip) VALUES ('177017332520314', 'f6817f48af4fb3af11b9e8bf182f618b', '177017332520207', NULL, '2026-02-04 10:48:45', '127.0.0.1');