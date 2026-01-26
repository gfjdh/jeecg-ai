DROP TABLE IF EXISTS `student_info`;
CREATE TABLE `student_info` (
  `id` varchar(36) NOT NULL COMMENT '主键',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新日期',
  `student_no` varchar(32) NOT NULL COMMENT '学号',
  `name` varchar(100) NOT NULL COMMENT '姓名',
  `sex` int(1) DEFAULT NULL COMMENT '性别 (1:男 2:女)',
  `birthday` datetime DEFAULT NULL COMMENT '出生日期',
  `major` varchar(100) DEFAULT NULL COMMENT '专业',
  `class_name` varchar(100) DEFAULT NULL COMMENT '班级',
  `year` varchar(32) DEFAULT NULL COMMENT '年级',
  `phone` varchar(32) NOT NULL COMMENT '手机号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_student_info_student_no` (`student_no`) USING BTREE,
  UNIQUE KEY `uk_student_info_phone` (`phone`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生信息';