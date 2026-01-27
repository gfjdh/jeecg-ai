DROP TABLE IF EXISTS `student_grade`;
CREATE TABLE `student_grade` (
  `id` varchar(36) NOT NULL COMMENT '主键',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新日期',
  `student_no` varchar(32) DEFAULT NULL COMMENT '学号',
  `course` varchar(100) DEFAULT NULL COMMENT '课程',
  `score` double(10,2) DEFAULT NULL COMMENT '成绩',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_sg_student_no` (`student_no`) USING BTREE,
  CONSTRAINT `fk_sg_student_no` FOREIGN KEY (`student_no`) REFERENCES `student_info` (`student_no`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生成绩信息';
