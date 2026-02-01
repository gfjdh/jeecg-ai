# jeecg-module-student 复用说明

## 1. 核心基础框架
- **JeecgController**: `StudentController` 继承自 `JeecgController`，复用了底层通用的 Service 注入与 CRUD 逻辑。
- **JeecgEntity**: `Student` 实体继承自 `JeecgEntity`，复用了标准审计字段（id, createBy, createTime, updateBy, updateTime）。
- **Result**: 使用 `Result<T>` 对象统一封装 API 响应。
- **QueryGenerator**: 使用 `QueryGenerator.initQueryWrapper` 自动根据前端参数和实体注解构建 MyBatis-Plus 查询条件。

## 2. 日志管理
- **@AutoLog**: 在 `StudentController` 的增删改方法上补充了 `@AutoLog` 注解，开启了系统操作日志的自动记录功能。

## 3. 安全与隐私
- **@SensitiveField**: 在 `Student` 实体的 `phone` 字段上添加了 `@SensitiveField(type = SensitiveEnum.MOBILE_PHONE)` 注解。
    - 实现了手机号在批量传输给前端时的自动脱敏处理。

## 4. 工具与注解
- **Swagger/OpenAPI**: 使用 `@Tag` 和 `@Operation` 注解生成标准 API 文档。
