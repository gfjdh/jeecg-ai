# JEECG-BOOT 模块复用目录

## 一、核心基础框架类（⭐ 高可复用性）

### 1. 系统基类模块
- **JeecgController/JeecgEntity/JeecgService** ⭐ - MyBatis-Plus增强基类
- **系统工具模块** (JwtUtil, ResourceUtil等) ⭐ - 系统级通用工具
- **枚举字典模块** (EnumDict) ⭐ - 枚举自动转为字典
- **通用API接口** (CommonAPI, Result) ⭐ - 统一服务接口和响应封装

### 2. 数据处理与查询
- **查询构造器模块** (QueryGenerator) ⭐ - 动态查询条件组装
- **类型转换工具模块** (oConvertUtils) ⭐ - 全面的类型转换工具
- **值对象模块** (LoginUser等VO类) ⭐ - 通用值对象封装
- **动态数据源管理模块** (DynamicDBUtil) - 多数据源管理

### 3. 异常与日志
- **异常处理模块** (JeecgBootExceptionHandler) ⭐ - 统一异常处理
- **切面处理模块** (AutoLogAspect, DictAspect) ⭐ - AOP功能增强

---

## 二、安全与隐私保护（⭐ 高可复用性）

### 1. 数据脱敏与加密
- **数据脱敏模块** (SensitiveSerialize等) ⭐ - 字段级脱敏与加密
- **密码与加密工具模块** (PasswordUtil, AesEncryptUtil) ⭐ - 密码加密解密
- **安全工具模块** (SecurityTools) ⭐ - 数据加解密与签名

### 2. 安全防护
- **SQL注入防护模块** (SqlInjectionUtil) ⭐ - SQL注入检测与防护
- **文件上传安全模块** (SsrfFileTypeFilter) ⭐ - 文件安全校验
- **字典表白名单模块** (IDictTableWhiteListHandler) - 查询安全控制

### 3. 认证与授权
- **Token验证模块** (TokenUtils) ⭐ - Token验证与用户信息
- **用户增强模块** (UserFilterEnhance) - 数据权限扩展
- **Shiro免认证注解模块** (IgnoreAuth) - 免认证接口支持

---

## 三、系统架构与集成（⭐ 高可复用性）

### 1. 配置与上下文
- **Spring上下文工具模块** (SpringContextUtils) ⭐ - Spring Bean获取
- **静态配置模块** (StaticConfig) ⭐ - 统一配置管理
- **应用上下文工具模块** (ApplicationContextUtil) - 上下文访问

### 2. REST与HTTP
- **REST模板配置模块** (RestTemplateConfig) ⭐ - HTTP客户端配置
- **RESTful接口调用模块** (RestUtil) ⭐ - HTTP请求封装
- **请求体保留过滤器模块** (RequestBodyReserveFilter) - 请求体多次读取

### 3. 监控与文档
- **Prometheus监控集成模块** ⭐ - 应用监控集成
- **Swagger3配置模块** ⭐ - API文档生成
- **Druid监控页面广告移除模块** - 监控页面优化

### 4. 跨域与配置
- **CORS跨域配置模块** ⭐ - 智能跨域配置
- **JSON序列化配置模块** ⭐ - Jackson统一配置
- **资源处理配置模块** - 静态资源管理

---

## 四、数据与存储服务（⭐ 高可复用性）

### 1. 文件与存储
- **MinIO对象存储模块** (MinioUtil) ⭐ - MinIO集成
- **云存储集成模块** (OssBootUtil) ⭐ - 阿里云OSS集成
- **文件下载工具模块** (FileDownloadUtils) ⭐ - 文件下载处理

### 2. 搜索与缓存
- **Elasticsearch集成模块** (JeecgElasticsearchTemplate) ⭐ - ES操作封装
- **动态数据源拦截器模块** - 数据源动态切换

### 3. 消息与通知
- **短信服务模块** (DySmsHelper, TencentSms) ⭐ - 短信发送
- **消息推送模块** (MessageDTO等) ⭐ - 消息封装
- **短信发送限制模块** (DySmsLimit) - 频率控制

---

## 五、开发工具与生产力（⭐ 高可复用性）

### 1. 开发工具类
- **断言检查工具模块** (AssertUtils) ⭐ - 参数校验断言
- **日期时间工具模块** (DateUtils) ⭐ - 日期处理工具
- **日期范围工具模块** (DateRangeUtils) ⭐ - 日期范围计算
- **HTML处理工具模块** (HTMLUtils) ⭐ - HTML/Markdown处理
- **反射工具模块** (ReflectHelper) ⭐ - 反射操作封装

### 2. Excel处理
- **AutoPoi Excel配置模块** ⭐ - Excel导入导出增强
- **Excel导入工具模块** (ImportExcelUtil) ⭐ - Excel导入处理

### 3. 编码与生成
- **唯一ID生成模块** (UUIDGenerator) ⭐ - 唯一ID生成
- **填值规则模块** (FillRuleUtil) ⭐ - 动态值生成规则
- **类加载工具模块** (MyClassLoader) - 动态类加载

### 4. 网络与系统
- **IP地址工具模块** (IpUtils) ⭐ - IP地址处理
- **浏览器检测工具模块** (BrowserUtils) ⭐ - 浏览器检测
- **MD5加密工具模块** (Md5Util) ⭐ - MD5计算
- **通用工具模块** (CommonUtils) ⭐ - 系统级通用功能

### 5. 框架集成
- **MyBatis拦截器模块** ⭐ - 自动填充功能
- **自定义MultipartFile模块** - 文件上传兼容
- **线程池增强模块** (ShiroThreadPoolExecutor) - 线程池增强
- **WebSocket配置模块** - WebSocket支持
- **Druid防火墙配置模块** - SQL防火墙配置

---

## 六、业务特定模块（中等可复用性）

### 1. 低代码相关
- **低代码模式拦截模块** - 低代码权限控制

### 2. 注解模块
- 各类业务注解 (@AutoLog, @Dict, @PermissionData等)

### 3. 常量定义模块
- 各类常量类 (CommonConstant, DataBaseConstant等)

---

# 详细模块说明

---

## 一、数据脱敏模块（Desensitization）
### 类名：
- `SensitiveSerialize`
- `SensitiveDataAspect`
- `SensitiveInfoUtil`
- `SensitiveEnum`
- `Sensitive`、`SensitiveDecode`、`SensitiveEncode`、`SensitiveField` 注解

### 功能：
提供字段级别的数据脱敏与加解密支持，包括：
- 敏感字段标注与序列化处理
- 基于注解的字段脱敏（如姓名、手机号、身份证等）
- 支持加密/解密切面处理
- 支持嵌套对象、列表对象的敏感数据处理

### 复用方式：
1. 在实体类字段上标注 `@Sensitive` 或 `@SensitiveField` 注解
2. 在方法上标注 `@SensitiveEncode` 或 `@SensitiveDecode` 注解
3. 直接调用 `SensitiveInfoUtil` 工具类进行脱敏处理

---

## 二、通用API接口（Common API）
### 类名：
- `CommonAPI`
- `AiragFlowDTO`、`LogDTO`、`FileUploadDTO`、`PushMessageDTO` 等 DTO
- `Result`

### 功能：
提供系统级别的通用服务接口，如：
- 用户角色、权限查询
- 数据字典翻译
- 文件上传/下载
- 消息推送
- 统一的返回结果封装（`Result<T>`）

### 复用方式：
1. 在 `jeecg-boot-module` 中注入 `CommonAPI` 接口
2. 使用 `Result` 作为控制器方法的返回类型
3. 调用 `CommonAPI` 中的方法获取用户、角色、字典等信息

---

## 三、切面处理模块（Aspect）
### 类名：
- `AutoLogAspect`
- `DictAspect`
- `PermissionDataAspect`
- `AutoLog`、`Dict`、`PermissionData`、`DynamicTable` 等注解

### 功能：
提供面向切面的系统功能增强，如：
- 自动日志记录（`@AutoLog`）
- 字典值自动翻译（`@Dict`）
- 数据权限控制（`@PermissionData`）
- 动态表名切换（`@DynamicTable`）

### 复用方式：
1. 在方法上标注 `@AutoLog` 实现操作日志自动记录
2. 在实体字段上标注 `@Dict` 实现字典值自动翻译
3. 在方法上标注 `@PermissionData` 实现数据权限控制
4. 在方法上标注 `@DynamicTable` 实现动态表名切换

---

## 四、常量定义模块（Constant）
### 类名：
- `CommonConstant`
- `DataBaseConstant`
- `SymbolConstant`
- `ServiceNameConstants`
- 各枚举类（如 `SensitiveEnum`、`ModuleType`、`OperateTypeEnum` 等）

### 功能：
提供系统级别的常量定义，如：
- 状态码、日志类型、操作类型
- 数据库字段常量、系统变量
- 服务名称、文件类型、消息类型等枚举

### 复用方式：
1. 直接引用常量类中的静态字段
2. 使用枚举类规范参数值
3. 在配置、校验、日志等场景中使用预定义常量

---

## 五、工具类模块（Utility）
### 类名：
- `SensitiveInfoUtil`（脱敏工具）
- `AesEncryptUtil`（加密工具）
- `IpUtils`、`SpringContextUtils`、`oConvertUtils` 等（未在文件中但属于工具类）


### 功能：
提供通用的工具方法，如：
- 字符串脱敏格式化
- AES 加密解密
- IP 获取、Spring 上下文获取、类型转换等

### 复用方式：
1. 静态调用工具类方法
2. 在业务逻辑中直接使用工具类封装的功能

---

## 六、注解模块（Annotations）
### 类名：
- `Sensitive`、`SensitiveField`、`SensitiveEncode`、`SensitiveDecode`
- `AutoLog`、`Dict`、`PermissionData`、`DynamicTable`、`OnlineAuth`
- `AutoDict`

### 功能：
提供声明式编程支持，通过注解简化开发：
- 标注敏感字段、脱敏方式
- 标注日志记录、字典翻译
- 标注数据权限、动态表名

### 复用方式：
1. 在实体类、字段、方法上标注相应注解
2. 结合切面实现自动增强功能

---

## 七、消息推送模块（Message）
### 类名：
- `MessageDTO`、`TemplateMessageDTO`、`BusMessageDTO` 等
- `PushMessageDTO`

### 功能：
提供系统消息、模板消息、业务消息的封装与推送支持

### 复用方式：
1. 创建消息 DTO 实例
2. 调用消息推送服务发送消息

---

## 八、系统基类模块（System Base Classes）

### 类名：
- `JeecgController<T, S extends IService<T>>`
- `JeecgEntity`
- `JeecgService<T>`
- `JeecgServiceImpl<M extends BaseMapper<T>, T extends JeecgEntity>`

### 功能：
提供 **MyBatis-Plus** 增强的通用基类，包括：
- **通用控制器基类**（`JeecgController`）：封装了导出Excel、导入Excel、分页查询等通用方法
- **实体基类**（`JeecgEntity`）：包含 `id`、`createBy`、`createTime`、`updateBy`、`updateTime` 等通用字段
- **服务接口与实现基类**（`JeecgService` / `JeecgServiceImpl`）：集成 MyBatis-Plus 的 `IService` 和 `ServiceImpl`

### 复用方式：
1. 实体类继承 `JeecgEntity`
2. Service 接口继承 `JeecgService<T>`，实现类继承 `JeecgServiceImpl<M, T>`
3. Controller 继承 `JeecgController<T, S>`，可复用导出、导入等通用方法

---

## 九、查询构造器模块（Query Generator）

### 类名：
- `QueryGenerator`
- `QueryRuleEnum`
- `QueryCondition`
- `MatchTypeEnum`
- `SqlConcatUtil`

### 功能：
提供 **动态查询条件组装** 功能，支持：
- 自动解析前端查询参数，构建 `QueryWrapper`
- 支持区间查询、模糊查询、多字段排序、高级查询（`superQuery`）
- 支持数据权限规则自动注入
- 防 SQL 注入处理

### 复用方式：
1. 在 Controller 中调用 `QueryGenerator.initQueryWrapper()` 构建查询条件
2. 使用 `QueryRuleEnum` 定义查询规则
3. 在查询条件中支持 `_begin`、`_end`、`superQueryParams` 等参数格式

---

## 十、枚举字典模块（Enum Dictionary）

### 类名：
- `EnumDict` 注解
- `ResourceUtil`
- `DictModel`、`DictModelMany`、`DictQuery`


### 功能：
提供 **枚举类自动转为字典数据** 的功能：
- 通过 `@EnumDict` 注解标记枚举类
- `ResourceUtil` 自动扫描并加载枚举字典
- 支持字典翻译、批量查询字典项

### 复用方式：
1. 在枚举类上标注 `@EnumDict("dictCode")`
2. 在枚举类中实现 `getDictList()` 方法
3. 在 `ResourceUtil.BASE_SCAN_PACKAGES` 中添加枚举类所在包路径

---

## 十一、用户增强模块（User Filter Enhance）

### 类名：
- `UserFilterEnhance` 接口

### 功能：
提供 **用户数据权限扩展接口**，允许自定义获取用户ID列表的逻辑（如根据角色、部门等）

### 复用方式：
1. 实现 `UserFilterEnhance` 接口的 `getUserIds(String loginUserId)` 方法
2. 在数据权限规则中使用 `#{sys_user_id}` 等变量时自动调用

---

## 十二、Elasticsearch 集成模块（Elasticsearch Integration）

### 类名：
- `JeecgElasticsearchTemplate`
- `QueryStringBuilder`

### 功能：
提供 **Elasticsearch 操作模板类**，支持：
- 索引管理（创建、删除、查询）
- 文档 CRUD
- 条件查询、批量操作
- 支持 ES 7.x 版本兼容

### 复用方式：
1. 在配置文件中配置 `jeecg.elasticsearch.cluster-nodes`
2. 注入 `JeecgElasticsearchTemplate` 进行 ES 操作
3. 使用 `QueryStringBuilder` 构建查询字符串

---

## 十三、异常处理模块（Exception Handling）

### 类名：
- `JeecgBootException`、`JeecgBoot401Exception`、`JeecgBootBizTipException`、`JeecgBootAssertException`
- `JeecgSqlInjectionException`
- `JeecgBootExceptionHandler`

### 功能：
提供 **统一异常处理机制**，包括：
- 自定义业务异常类
- 全局异常处理器（`@RestControllerAdvice`）
- 自动记录异常日志到数据库
- 支持 Sentinel 限流异常处理

### 复用方式：
1. 在业务层抛出 `JeecgBootException` 等自定义异常
2. 全局异常自动捕获并返回统一 `Result` 格式
3. 自动记录异常日志到 `sys_log` 表

---

## 十四、填值规则模块（Fill Rule Handler）

### 类名：
- `IFillRuleHandler` 接口

### 功能：
提供 **填值规则执行接口**，用于动态生成字段值（如自动生成编号、计算字段等）

### 复用方式：
1. 实现 `IFillRuleHandler` 接口的 `execute(JSONObject params, JSONObject formData)` 方法
2. 在前端配置填值规则时指定该处理器
3. 在表单提交或数据保存时自动调用

---

## 十五、系统工具模块（System Utilities）

### 类名：
- `JwtUtil`
- `JeecgDataAutorUtils`
- `ResourceUtil`

### 功能：
提供 **系统级工具类**，支持：
- JWT Token 生成与验证
- 数据权限规则加载与用户信息获取
- 枚举字典资源加载

### 复用方式：
1. 调用 `JwtUtil` 进行 Token 相关操作
2. 使用 `JeecgDataAutorUtils` 获取当前用户的数据权限规则
3. 使用 `ResourceUtil` 获取枚举字典数据

---

## 十六、值对象模块（Value Objects）

### 类名：
- `LoginUser`、`SysUserCacheInfo`、`UserAccountInfo`
- `SysPermissionDataRuleModel`
- `ComboModel`、`SelectTreeModel`
- `DynamicDataSourceModel`
- `SysDepartModel`、`SysCategoryModel`、`SysFilesModel`

### 功能：
提供 **系统通用值对象**，用于：
- 用户信息、部门信息、文件信息等封装
- 数据权限规则模型
- 下拉框、树形选择器等前端组件数据模型

### 复用方式：
1. 在 Service 或 Controller 中直接使用这些 VO 类作为参数或返回值
2. 在数据权限处理中使用 `SysPermissionDataRuleModel`
3. 在前端组件数据对接中使用 `ComboModel`、`SelectTreeModel`

---

## 十七、密码与加密工具模块（Password & Encryption）

### 类名：
- `PasswordUtil`
- `AesEncryptUtil`
- `EncryptedString`


### 功能：
提供密码加密解密和通用加密功能：
- **PasswordUtil**：基于PBE（Password-Based Encryption）算法的密码加密解密工具，支持盐值迭代
- **AesEncryptUtil**：AES加解密工具，兼容CBC/PKCS5Padding和CBC/NoPadding两种模式
- **EncryptedString**：存储AES加密所需的密钥和初始向量常量

### 复用方式：
1. 调用`PasswordUtil.encrypt()`和`PasswordUtil.decrypt()`进行密码加密解密
2. 调用`AesEncryptUtil.resolvePassword()`解析前端加密的密码
3. 在配置文件中使用相同的密钥和IV确保加解密一致性

---

## 十八、Spring上下文工具模块（Spring Context）

### 类名：
- `SpringContextUtils`

### 功能：
提供Spring上下文和HTTP请求相关的工具方法：
- 获取Spring应用上下文和Bean
- 获取当前HTTP请求和响应对象
- 获取项目域名和Origin
- 支持微服务环境下的域名获取

### 复用方式：
1. 静态调用`SpringContextUtils.getBean()`获取Spring管理的Bean
2. 使用`SpringContextUtils.getHttpServletRequest()`获取当前请求
3. 调用`SpringContextUtils.getDomain()`获取项目根路径

---

## 十九、反射工具模块（Reflection）

### 类名：
- `ReflectHelper`

### 功能：
提供Java反射操作的封装工具：
- 动态调用对象的getter和setter方法
- Map与对象之间的相互转换
- 获取对象字段信息（名称、类型、值）
- 支持`@TableField`注解的表字段名获取
- 支持父类字段的获取

### 复用方式：
1. 使用`ReflectHelper.setAll()`将Map数据注入到对象中
2. 调用`ReflectHelper.getFieldValueByName()`获取字段值
3. 使用`ReflectHelper.transList2Entrys()`将Map列表转换为实体列表

---

## 二十、RESTful接口调用模块（RESTful Client）

### 类名：
- `RestUtil`
- `RestDesformUtil`

### 功能：
提供HTTP RESTful接口调用能力：
- **RestUtil**：通用的HTTP客户端，支持GET、POST、PUT、DELETE等方法，内置连接池和超时控制
- **RestDesformUtil**：专门用于操作desform（动态表单）数据的REST客户端

### 复用方式：
1. 使用`RestUtil.get()`、`RestUtil.post()`等方法调用外部API
2. 调用`RestUtil.request()`方法进行更灵活的HTTP请求
3. 使用`RestDesformUtil`进行动态表单数据的增删改查操作

---

## 二十一、线程池增强模块（Thread Pool）

### 类名：
- `ShiroThreadPoolExecutor`

### 功能：
提供支持Shiro安全上下文的线程池：
- 继承`ThreadPoolExecutor`，在执行任务前绑定Shiro的Subject和SecurityManager
- 确保在线程池中执行的任务能够正确获取当前登录用户信息
- 任务执行完成后自动清理线程上下文

### 复用方式：
1. 在需要异步执行且需要获取当前用户信息的场景中使用
2. 替换标准的`ThreadPoolExecutor`为`ShiroThreadPoolExecutor`
3. 配置合适的核心线程数、最大线程数等参数

---

## 二十二、SQL注入防护模块（SQL Injection Protection）

### 类名：
- `SqlInjectionUtil`
- `JdbcSecurityUtil`


### 功能：
提供SQL注入攻击防护：
- **SqlInjectionUtil**：检测和过滤SQL注入关键词，支持普通查询、字典查询、在线报表等不同场景
- **JdbcSecurityUtil**：校验JDBC连接字符串中的不安全参数，防止连接驱动漏洞

### 复用方式：
1. 在接收用户输入的SQL参数处调用`SqlInjectionUtil.filterContent()`
2. 使用`SqlInjectionUtil.getSqlInjectTableName()`和`getSqlInjectField()`获取安全的表名和字段名
3. 在配置动态数据源时使用`JdbcSecurityUtil.validate()`校验连接字符串

---

## 二十三、文件上传安全模块（File Upload Security）

### 类名：
- `SsrfFileTypeFilter`
- `StrAttackFilter`

### 功能：
提供文件上传的安全防护：
- **SsrfFileTypeFilter**：通过文件后缀白名单和文件头校验，防止上传恶意文件
- **StrAttackFilter**：过滤文件名中的特殊字符，防止路径遍历攻击

### 复用方式：
1. 在文件上传接口中调用`SsrfFileTypeFilter.checkUploadFileType()`校验文件类型
2. 在文件下载接口中调用`SsrfFileTypeFilter.checkDownloadFileType()`校验文件类型
3. 使用`StrAttackFilter.filter()`处理用户输入的文件名

---

## 二十四、动态数据源管理模块（Dynamic DataSource）

### 类名：
- `DynamicDBUtil`
- `DataSourceCachePool`
- `DbTypeUtils`
- `FreemarkerParseFactory`

### 功能：
提供多数据源动态管理和SQL模板解析：
- **DynamicDBUtil**：动态获取数据源并执行SQL操作（查询、更新等）
- **DataSourceCachePool**：数据源连接池缓存管理，支持Redis分布式缓存
- **DbTypeUtils**：数据库类型判断和方言获取
- **FreemarkerParseFactory**：基于FreeMarker的SQL模板解析，支持miniDao语法

### 复用方式：
1. 使用`DynamicDBUtil.findList()`等方法执行跨数据库查询
2. 通过`DataSourceCachePool`管理数据源缓存，避免重复创建连接
3. 使用`FreemarkerParseFactory.parseTemplateContent()`解析SQL模板

---

## 二十五、云存储集成模块（Cloud Storage）

### 类名：
- `OssBootUtil`

### 功能：
提供阿里云OSS对象存储的集成：
- 文件上传、下载、删除操作
- 支持自定义存储桶（Bucket）
- 生成文件访问URL（支持自定义域名）
- 文件安全校验和路径过滤

### 复用方式：
1. 配置OSS的endpoint、accessKeyId、accessKeySecret等参数
2. 调用`OssBootUtil.upload()`上传文件到OSS
3. 调用`OssBootUtil.deleteUrl()`删除OSS上的文件
4. 使用`OssBootUtil.getOssFile()`获取文件流

---

## 二十六、短信服务模块（SMS Service）

### 类名：
- `TencentSms`

### 功能：
提供腾讯云短信发送服务：
- 短信模板发送
- 支持动态参数替换
- 错误处理和日志记录
- 可配置的签名和模板ID

### 复用方式：
1. 配置腾讯云的SecretId、SecretKey、SdkAppId等参数
2. 调用`TencentSms.sendTencentSms()`发送短信
3. 通过`DySmsEnum`枚举配置短信模板和签名

---

## 二十七、唯一ID生成模块（Unique ID Generation）

### 类名：
- `UUIDGenerator`
- `YouBianCodeUtil`

### 功能：
提供不同类型的唯一标识生成：
- **UUIDGenerator**：基于IP、JVM、时间戳和计数器的32位UUID生成
- **YouBianCodeUtil**：按规则递增的流水号生成（如A001、A001A002格式）

### 复用方式：
1. 调用`UUIDGenerator.generate()`生成全局唯一ID
2. 使用`YouBianCodeUtil.getNextYouBianCode()`生成层级编码
3. 使用`YouBianCodeUtil.getSubYouBianCode()`生成子级编码

---

## 二十八、安全工具模块（Security Tools）

### 类名：
- `SecurityTools`
- `AbstractQueryBlackListHandler`

### 功能：
提供数据安全和查询安全控制：
- **SecurityTools**：基于RSA和AES的数据加解密、签名验签
- **AbstractQueryBlackListHandler**：查询黑名单处理，防止敏感数据泄露

### 复用方式：
1. 使用`SecurityTools.sign()`对数据进行签名加密
2. 使用`SecurityTools.valid()`验证数据签名和解密
3. 继承`AbstractQueryBlackListHandler`实现自定义查询黑名单规则

---

## 二十九、Token验证模块（Token Validation）

### 类名：
- `TokenUtils`

### 功能：
提供Token验证和用户信息获取：
- 从HTTP请求中提取Token
- Token有效性验证和自动刷新
- 获取当前登录用户信息（支持Redis缓存）
- 支持租户ID和低代码应用ID的提取

### 复用方式：
1. 在拦截器或切面中调用`TokenUtils.verifyToken()`验证Token
2. 使用`TokenUtils.getLoginUser()`获取当前用户信息
3. 调用`TokenUtils.getTenantIdByRequest()`获取租户信息

## 三十、断言检查工具模块（Assertion）

### 类名：
- `AssertUtils`
- `JeecgBootAssertException`（关联异常）

### 功能：
提供**参数校验和业务断言**功能：
- 对象空值检查（`assertEmpty`/`assertNotEmpty`）
- 相等性断言（`assertEquals`/`assertNotEquals`）
- 相同性断言（`assertSame`/`assertNotSame`）
- 布尔值断言（`assertTrue`/`assertFalse`）
- 集合包含断言（`assertIn`/`assertNotIn`）
- 数值比较断言（`assertGt`/`assertGe`/`assertLt`/`assertLe`）
- 集成`oConvertUtils`进行复杂校验

### 复用方式：
```java
// 1. 基本空值检查
AssertUtils.assertNotEmpty("用户ID不能为空", userId);

// 2. 相等性检查
AssertUtils.assertEquals("密码不一致", newPassword, confirmPassword);

// 3. 集合包含检查
AssertUtils.assertIn("状态值无效", status, "1", "2", "3");

// 4. 数值比较
AssertUtils.assertGt("数量必须大于0", quantity, 0);
```

---

## 三十一、浏览器检测工具模块（Browser Detection）

### 类名：
- `BrowserUtils`
- `BrowserType`（枚举）

### 功能：
提供**浏览器类型和终端检测**功能：
- 浏览器类型识别（IE6-11、Chrome、Firefox、Safari等）
- IE版本获取
- 移动端/PC端检测
- 浏览器语言获取
- User-Agent解析和正则匹配

### 复用方式：
```java
// 1. 获取浏览器类型
BrowserType browserType = BrowserUtils.getBrowserType(request);

// 2. 检测是否为移动端
boolean isMobile = BrowserUtils.isMobile(request);

// 3. 获取IE版本
Double ieVersion = BrowserUtils.getIeVersion(request);

// 4. 检查是否为IE浏览器
boolean isIe = BrowserUtils.isIe(request);
```

---

## 三十二、通用工具模块（Common Utilities）

### 类名：
- `CommonUtils`

### 功能：
提供**系统级通用功能集成**：
- 文件上传（支持本地、MinIO、OSS）
- 数据库类型检测
- 数据源动态获取
- 服务器地址获取
- JSON对象合并
- SQL语句解析
- 数组交集判断
- 安全日志输出

### 复用方式：
```java
// 1. 文件上传
String fileUrl = CommonUtils.upload(file, "avatar", "minio");

// 2. 获取数据库类型
DbType dbType = CommonUtils.getDatabaseTypeEnum();

// 3. 获取数据源连接
Connection conn = CommonUtils.getDataSourceConnect("slave");

// 4. 获取服务器基础URL
String baseUrl = CommonUtils.getBaseUrl(request);

// 5. 合并JSON对象
JSONObject merged = CommonUtils.mergeJSON(target, source1, source2);
```

---

## 三十三、日期范围工具模块（Date Range）

### 类名：
- `DateRangeUtils`
- `DateRangeEnum`（关联枚举）

### 功能：
提供**常见日期范围计算**：
- 今天/昨天/明天范围
- 本周/上周/下周范围
- 本月/上月/下月范围
- 过去7天范围
- 基于枚举的日期范围获取
- 使用Hutool日期工具增强

### 复用方式：
```java
// 1. 通过枚举获取日期范围
Date[] todayRange = DateRangeUtils.getDateRangeByEnum(DateRangeEnum.TODAY);

// 2. 获取具体日期范围
Date todayStart = DateRangeUtils.getTodayStartTime();
Date todayEnd = DateRangeUtils.getTodayEndTime();

// 3. 获取上周范围
Date lastWeekStart = DateRangeUtils.getLastWeekStartDay();
Date lastWeekEnd = DateRangeUtils.getLastWeekEndDay();
```

---

## 三十四、日期时间工具模块（Date Time）

### 类名：
- `DateUtils`

### 功能：
提供**全面的日期时间处理**：
- 多种格式的日期格式化/解析
- 时间戳转换
- 日期计算和差值
- 日期比较（同天、同周、同月、同年）
- 日期范围列表生成
- PropertyEditor支持（用于Spring参数绑定）
- 线程安全的SimpleDateFormat管理

### 复用方式：
```java
// 1. 格式化日期
String dateStr = DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");

// 2. 解析日期字符串
Date date = DateUtils.parseDate("2023-12-01", "yyyy-MM-dd");

// 3. 判断是否为同一天
boolean sameDay = DateUtils.isSameDay(date1, date2);

// 4. 获取两个日期之间的所有日期
List<Date> dateList = DateUtils.getDateRangeList(startDate, endDate);

// 5. 获取当前时间戳字符串
String timestamp = DateUtils.getCurrentTimestamp().toString();
```

---

## 三十五、阿里云短信服务模块（Alibaba Cloud SMS）

### 类名：
- `DySmsHelper`

### 功能：
提供**阿里云短信服务集成**：
- 短信发送（支持模板参数）
- 短信模板动态配置
- 多短信服务商支持（阿里云/腾讯云）
- 模板参数验证
- 发送结果日志记录
- 配置化管理（签名、模板ID）

### 复用方式：
```java
// 1. 准备模板参数
JSONObject params = new JSONObject();
params.put("code", "123456");

// 2. 发送短信
boolean success = DySmsHelper.sendSms("13800138000", params, DySmsEnum.LOGIN_TEMPLATE_CODE);

// 3. 通过Spring配置自定义模板和签名
// 在application.yml中配置：
// jeecg:
//   sms-template:
//     signature: "阿里云短信测试"
//     templateCode:
//       login: "SMS_123456789"
```

---

## 三十六、短信发送限制模块（SMS Rate Limit）

### 类名：
- `DySmsLimit`

### 功能：
提供**短信发送频率限制**：
- IP级别的短信发送频率控制
- 黑名单机制（异常高频发送）
- 时间窗口统计（1分钟窗口）
- 验证成功后计数清零
- 防止短信接口滥用

### 复用方式：
```java
// 1. 检查是否可以发送短信
String clientIp = IpUtils.getIpAddr(request);
if (DySmsLimit.canSendSms(clientIp)) {
    // 发送短信逻辑
    DySmsHelper.sendSms(phone, params, template);
    
    // 2. 验证成功后清空计数（如图片验证码验证通过）
    DySmsLimit.clearSendSmsCount(clientIp);
} else {
    throw new RuntimeException("发送频率过高，请稍后重试");
}
```

---

## 三十七、文件下载工具模块（File Download）

### 类名：
- `FileDownloadUtils`

### 功能：
提供**文件下载和压缩处理**：
- 单文件下载（支持HTTP和本地文件）
- 多文件打包下载（ZIP格式）
- 网络文件下载到磁盘
- 文件名处理和去重
- 快捷方式文件生成（.url格式）
- 文件流安全校验
- 目录创建和验证

### 复用方式：
```java
// 1. 单文件下载
FileDownloadUtils.downloadFile(response, "/path/to/file.pdf", "document.pdf");

// 2. 多文件打包下载
List<String> filePaths = Arrays.asList("/path/to/file1.pdf", "/path/to/file2.doc");
FileDownloadUtils.downloadFileMulti(response, filePaths, "documents");

// 3. 下载网络文件到本地
String localPath = FileDownloadUtils.download2DiskFromNet("http://example.com/file.pdf", "/local/path/file.pdf");

// 4. 获取不重名的文件
File uniqueFile = FileDownloadUtils.getUniqueFile(new File("/path/to/existing.txt"));
```

---

## 三十八、填值规则执行模块（Fill Rule Execution）

### 类名：
- `FillRuleUtil`

### 功能：
提供**动态值生成规则执行**：
- 规则代码解析和执行
- 参数变量替换（系统变量、查询参数）
- 自定义规则处理器调用
- 表单数据上下文支持
- 规则类动态加载

### 复用方式：
```java
// 1. 执行填值规则生成订单号
JSONObject formData = new JSONObject();
formData.put("userId", "1001");
String orderNo = (String) FillRuleUtil.executeRule("ORDER_NO_RULE", formData);

// 2. 自定义规则处理器实现
@Component
public class OrderNoRuleHandler implements IFillRuleHandler {
    @Override
    public Object execute(JSONObject params, JSONObject formData) {
        // 生成订单号逻辑
        return "ORD" + System.currentTimeMillis();
    }
}
```

---

## 三十九、HTML处理工具模块（HTML Processing）

### 类名：
- `HTMLUtils`

### 功能：
提供**HTML和Markdown处理**：
- HTML文本提取（去除标签）
- HTML实体解码
- Markdown转HTML
- 空格和特殊字符处理
- 基于CommonMark的Markdown解析

### 复用方式：
```java
// 1. 提取HTML纯文本
String html = "<div>Hello <b>World</b></div>";
String text = HTMLUtils.getInnerText(html); // 返回: Hello World

// 2. Markdown转HTML
String markdown = "# Title\n\nThis is **bold** text.";
String htmlContent = HTMLUtils.parseMarkdown(markdown);
// 返回: <h1>Title</h1><p>This is <strong>bold</strong> text.</p>
```

---

## 四十、Excel导入工具模块（Excel Import）

### 类名：
- `ImportExcelUtil`

### 功能：
提供**Excel导入结果处理**：
- 导入结果统计和返回
- 错误信息记录和文件生成
- 数据批量保存（带错误处理）
- 数据库唯一约束错误识别
- 错误日志文件管理

### 复用方式：
```java
// 1. 处理导入结果
try {
    List<User> userList = parseExcel(file);
    List<String> errorMessages = new ArrayList<>();
    
    // 批量保存并收集错误
    ImportExcelUtil.importDateSave(userList, UserServiceImpl.class, errorMessages, "导入失败");
    
    // 返回导入结果
    return ImportExcelUtil.imporReturnRes(errorMessages.size(), userList.size() - errorMessages.size(), errorMessages);
} catch (IOException e) {
    // 处理异常
}

// 2. 单条数据保存
ImportExcelUtil.importDateSaveOne(user, UserServiceImpl.class, errorMessages, i, "导入失败");
```

---

## 四十一、IP地址工具模块（IP Address）

### 类名：
- `IpUtils`

### 功能：
提供**IP地址相关操作**：
- 客户端IP获取（支持代理和负载均衡）
- IP格式验证
- 服务器IP获取
- 内网IP识别
- 多级代理IP解析

### 复用方式：
```java
// 1. 获取客户端IP
String clientIp = IpUtils.getIpAddr(request);

// 2. 验证IP格式
boolean isValid = IpUtils.isValidIpAddress("192.168.1.1");

// 3. 获取服务器IP
String serverIp = IpUtils.getServerIp();

// 4. 判断是否为内网IP
boolean isInner = IpUtils.isInnerIp("10.0.0.1");
```

---

## 四十二、MD5加密工具模块（MD5 Encryption）

### 类名：
- `Md5Util`

### 功能：
提供**MD5哈希计算**：
- 字符串MD5加密
- 字节数组转十六进制字符串
- 支持指定字符集编码
- 简单的哈希计算工具

### 复用方式：
```java
// 1. MD5加密（默认编码）
String md5 = Md5Util.md5Encode("password", null);

// 2. MD5加密（指定UTF-8编码）
String md5Utf8 = Md5Util.md5Encode("password", "UTF-8");

// 3. 字节数组转十六进制
byte[] bytes = {1, 2, 3, 4};
String hex = Md5Util.byteArrayToHexString(bytes);
```

---

## 四十三、MinIO对象存储模块（MinIO Storage）

### 类名：
- `MinioUtil`

### 功能：
提供**MinIO对象存储操作**：
- 文件上传（MultipartFile和InputStream）
- 文件下载和流获取
- 文件删除
- 预签名URL生成
- 存储桶管理
- 文件名安全处理

### 复用方式：
```java
// 1. 文件上传到MinIO
String fileUrl = MinioUtil.upload(file, "documents", "my-bucket");

// 2. 获取文件流
InputStream stream = MinioUtil.getMinioFile("my-bucket", "documents/file.pdf");

// 3. 生成文件访问URL（7天有效期）
String presignedUrl = MinioUtil.getObjectUrl("my-bucket", "documents/file.pdf", 7 * 24 * 60 * 60);

// 4. 删除文件
MinioUtil.removeObject("my-bucket", "documents/file.pdf");
```

---

## 四十四、类加载工具模块（Class Loading）

### 类名：
- `MyClassLoader`

### 功能：
提供**类加载和路径相关操作**：
- 动态类加载
- 包路径获取
- 应用路径获取
- 类名解析
- 资源文件定位

### 复用方式：
```java
// 1. 动态加载类
Class<?> clazz = MyClassLoader.getClassByScn("com.example.MyClass");

// 2. 获取对象的完整类名
String className = MyClassLoader.getPackPath(new User());

// 3. 获取类的应用路径
String appPath = MyClassLoader.getAppPath(MyClass.class);
```

---

## 四十五、自定义MultipartFile模块（Custom MultipartFile）

### 类名：
- `MyCommonsMultipartFile`

### 功能：
提供**自定义MultipartFile实现**：
- 支持从FileItem构造（兼容旧版CommonsMultipartFile）
- 支持InputStream构造
- 完整的MultipartFile接口实现
- Spring Boot 3+兼容

### 复用方式：
```java
// 1. 从FileItem创建（兼容旧代码）
FileItem fileItem = ...;
MultipartFile multipartFile = new MyCommonsMultipartFile(fileItem);

// 2. 从InputStream创建
InputStream stream = ...;
MultipartFile multipartFile = new MyCommonsMultipartFile(stream, "file.txt", "text/plain");

// 3. 用于文件上传处理
String fileName = multipartFile.getOriginalFilename();
byte[] content = multipartFile.getBytes();
```

---

## 四十六、类型转换工具模块（Type Conversion）

### 类名：
- `oConvertUtils`

### 功能：
提供**全面的类型转换和通用工具**：
- 空值判断（支持各种类型）
- 字符串编码转换
- 数字类型转换（带默认值）
- IP地址处理（内网判断、格式验证）
- 驼峰命名和下划线命名转换
- 集合和数组操作
- JSON处理
- 文件大小计算
- 对象属性复制（忽略空值）
- 租户ID有效性检查

### 复用方式：
```java
// 1. 空值判断
if (oConvertUtils.isEmpty(obj)) {
    // 处理空值
}
if (oConvertUtils.isNotEmpty(list)) {
    // 处理非空集合
}

// 2. 类型转换
int intValue = oConvertUtils.getInt(str, 0);
String strValue = oConvertUtils.getString(obj, "default");

// 3. 命名转换
String camelName = oConvertUtils.camelName("user_name"); // "userName"
String underlineName = oConvertUtils.camelToUnderline("userName"); // "user_name"

// 4. 集合操作
boolean isIn = oConvertUtils.isIn("value", "value1", "value2", "value3");
boolean listNotEmpty = oConvertUtils.listIsNotEmpty(list);

// 5. 复制非空属性
oConvertUtils.copyNonNullFields(sourceDto, targetEntity);
```

---

## 四十七、CORS跨域配置模块（CORS Configuration）

### 类名：
- `CorsFilterCondition`
- `WebMvcConfiguration.corsFilter()` 方法

### 功能：
提供**跨域资源共享（CORS）的智能配置**：
- 条件化加载跨域过滤器（仅单体应用启用）
- 支持所有HTTP方法和请求头
- 允许携带凭证信息（cookies）
- 灵活的原点匹配策略（支持通配符）

### 复用方式：
```java
// 1. 自动配置：在单体应用中自动启用跨域支持
// 通过判断是否配置了服务注册发现（spring.cloud.nacos.discovery）来决定是否加载

// 2. 条件注解使用
@Conditional(CorsFilterCondition.class) // 仅在满足条件时创建Bean

// 3. 自定义跨域规则
// 在WebMvcConfiguration中重写corsFilter()方法配置自定义规则
```

---

## 四十八、Druid监控页面广告移除模块（Druid Ad Removal）

### 类名：
- `DruidConfig`
- `DruidConfig.RemoveAdFilter`（内部类）

### 功能：
提供**Druid监控页面广告移除功能**：
- 自动检测并移除Druid监控页面的底部广告
- 通过JavaScript脚本替换实现
- 条件化配置（仅当启用监控页面时生效）
- 兼容Druid不同版本

### 复用方式：
```java
// 1. 自动启用：当配置了 spring.datasource.druid.stat-view-servlet.enabled=true 时自动生效

// 2. 配置监控页面路径
// 在application.yml中配置：
// spring:
//   datasource:
//     druid:
//       stat-view-servlet:
//         enabled: true
//         url-pattern: /druid/*

// 3. 自定义广告移除逻辑
// 继承DruidConfig并重写removeDruidAdFilter()方法
```

---

## 四十九、Druid防火墙配置模块（Druid Wall Configuration）

### 类名：
- `DruidWallConfigRegister`

### 功能：
提供**Druid SQL防火墙配置管理**：
- 动态修改Druid Wall配置
- 允许SELECT语句的WHERE子句是永真条件
- 在Spring Boot启动过程中修改配置
- 避免硬编码配置修改

### 复用方式：
```java
// 1. 自动配置：通过SpringApplicationRunListener在启动时自动配置

// 2. 配置原理：通过环境变量动态添加配置属性
// spring.datasource.dynamic.druid.wall.selectWhereAlwayTrueCheck = false

// 3. 扩展配置：可以修改DruidWallConfigRegister以添加更多Druid防火墙配置
```

---

## 五十、JSON序列化配置模块（JSON Serialization）

### 类名：
- `WebMvcConfiguration.objectMapper()` 方法

### 功能：
提供**Jackson ObjectMapper的统一配置**：
- 日期时间格式化（支持LocalDateTime、LocalDate、LocalTime）
- BigDecimal序列化优化
- 容错处理（忽略未知属性）
- Java 8时间API支持
- 统一的日期格式（yyyy-MM-dd HH:mm:ss）

### 复用方式：
```java
// 1. 自动配置：所有JSON序列化都使用统一配置的ObjectMapper

// 2. 实体类使用
public class User {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime; // 自动序列化为指定格式
    
    private BigDecimal amount; // 自动处理为纯数字格式
}

// 3. 获取ObjectMapper实例
@Autowired
private ObjectMapper objectMapper; // 注入配置好的ObjectMapper
```

---

## 五十一、Prometheus监控集成模块（Prometheus Integration）

### 类名：
- `WebMvcConfiguration.initPrometheusMeterRegistry()` 方法

### 功能：
提供**Prometheus监控系统集成**：
- Spring Boot Actuator监控端点配置
- MeterRegistry后处理器支持
- 避免监控配置警告
- 微服务监控集成

### 复用方式：
```java
// 1. 自动配置：通过@PostConstruct在Bean初始化后配置PrometheusMeterRegistry

// 2. 启用监控：添加Spring Boot Actuator和Micrometer依赖
// dependencies {
//     implementation 'org.springframework.boot:spring-boot-starter-actuator'
//     implementation 'io.micrometer:micrometer-registry-prometheus'
// }

// 3. 配置监控端点
// management:
//   endpoints:
//     web:
//       exposure:
//         include: health,info,prometheus
```

---

## 五十二、资源处理配置模块（Resource Handling）

### 类名：
- `WebMvcConfiguration.addResourceHandlers()` 方法

### 功能：
提供**静态资源和文件路径的统一管理**：
- 自定义资源前缀（解决Nginx转发问题）
- 本地文件路径映射
- 静态资源缓存控制（30天）
- 多资源位置支持（本地、MinIO、OSS等）

### 复用方式：
```java
// 1. 配置文件上传路径
// jeecg:
//   path:
//     upload: /opt/jeecg/upload
//     webapp: /opt/jeecg/webapp

// 2. 自定义资源前缀（用于解决前端访问问题）
// jeecg:
//   customResourcePrefixPath: /jeecg-boot

// 3. 静态资源缓存
// 所有静态资源自动设置Cache-Control: max-age=2592000 (30天)
```

---

## 五十三、Shiro免认证注解模块（Shiro Ignore Authentication）

### 类名：
- `IgnoreAuth` 注解
- `IgnoreAuthPostProcessor`
- `InMemoryIgnoreAuth`

### 功能：
提供**Shiro Token免认证的声明式支持**：
- 通过`@IgnoreAuth`注解标记免登录接口
- 启动时自动扫描并缓存免认证URL
- 支持Ant路径匹配模式
- 避免硬编码配置

### 复用方式：
```java
// 1. 在Controller方法上使用注解
@RestController
@RequestMapping("/api")
public class PublicController {
    
    @IgnoreAuth
    @GetMapping("/public/data")
    public Result<?> getPublicData() {
        // 这个方法不需要Token认证
        return Result.OK();
    }
    
    @GetMapping("/private/data")
    public Result<?> getPrivateData() {
        // 这个方法需要Token认证
        return Result.OK();
    }
}

// 2. 支持路径变量
@IgnoreAuth
@GetMapping("/article/{id}")
public Result<?> getArticle(@PathVariable String id) {
    // URL: /api/article/123 -> 匹配模式: /api/article/*
    return Result.OK();
}
```

---

## 五十四、WebSocket配置模块（WebSocket Configuration）

### 类名：
- `WebSocketConfig`

### 功能：
提供**WebSocket服务端配置**：
- ServerEndpoint端点自动注册
- WebSocket连接Token验证过滤器
- 多WebSocket端点路径配置
- 异步请求支持

### 复用方式：
```java
// 1. 创建WebSocket端点
@ServerEndpoint("/taskCountSocket/{userId}")
@Component
public class TaskCountWebSocket {
    // WebSocket处理逻辑
}

// 2. 前端连接（需要在Header中添加Token）
// const socket = new WebSocket('ws://localhost:8080/taskCountSocket/1001', ['Sec-WebSocket-Protocol', token]);

// 3. 添加新的WebSocket端点
// 在WebSocketConfig中修改getFilterRegistrationBean()方法，添加新的URL模式
```

---

## 五十五、请求体保留过滤器模块（Request Body Preservation）

### 类名：
- `RequestBodyReserveFilter`
- `BodyReaderHttpServletRequestWrapper`

### 功能：
提供**HTTP请求体多次读取支持**：
- 解决HttpServletRequest输入流只能读取一次的问题
- 支持POST请求参数提取（用于签名验证）
- 请求体内容缓存和包装

### 复用方式：
```java
// 1. 自动配置：通过FilterRegistrationBean注册到指定URL模式

// 2. 在需要多次读取请求体的场景中使用
public void handleRequest(HttpServletRequest request) {
    // 获取原始请求体
    String body = request.getReader().lines().collect(Collectors.joining());
    
    // 获取包装后的请求体（可多次读取）
    BodyReaderHttpServletRequestWrapper wrapper = new BodyReaderHttpServletRequestWrapper(request);
    String bodyAgain = wrapper.getReader().lines().collect(Collectors.joining());
}

// 3. 配置需要保留请求体的URL
// 在SignAuthConfiguration中配置signUrlsArray
```

---

## 五十六、低代码模式拦截模块（Low Code Mode Interception）

### 类名：
- `LowCodeModeConfiguration`
- `LowCodeModeInterceptor`
- `LowCodeUrlsEnum`

### 功能：
提供**低代码开发模式的权限控制**：
- 发布模式下禁止在线配置功能
- 支持白名单角色（admin等）
- 可配置的拦截URL枚举
- 智能角色权限检查

### 复用方式：
```java
// 1. 启用低代码发布模式
// jeecg:
//   firewall:
//     lowCodeMode: prod  # dev:开发模式, prod:发布模式

// 2. 添加允许开发的角色
// 在CommonConstant.allowDevRoles集合中添加角色编码

// 3. 扩展拦截URL
// 在LowCodeUrlsEnum中添加新的枚举项
LowCodeUrlsEnum.NEW_FEATURE_ADD("/new/feature/add", "新功能添加");
```

---

## 五十七、字典表白名单模块（Dictionary Table Whitelist）

### 类名：
- `IDictTableWhiteListHandler`（接口）
- `SysDictTableWhite`

### 功能：
提供**字典表查询的安全白名单机制**：
- 表级和字段级的白名单控制
- 防止SQL注入攻击
- 支持表别名处理
- 字段权限校验

### 复用方式：
```java
// 1. 实现白名单处理器
@Component
public class CustomDictTableWhiteListHandler implements IDictTableWhiteListHandler {
    
    @Override
    public boolean isPassBySql(String sql) {
        // 解析SQL，检查表和字段是否在白名单中
        return true;
    }
    
    @Override
    public boolean isPassByDict(String dictCodeString) {
        // 检查字典配置是否合法
        return true;
    }
}

// 2. 表白名单配置
SysDictTableWhite tableWhite = new SysDictTableWhite("sys_user", "u");
tableWhite.addField("id");
tableWhite.addField("username");
tableWhite.addField("realname");
tableWhite.setAll(false); // 不允许查询所有字段
```

---

## 五十八、MyBatis拦截器模块（MyBatis Interceptor）

### 类名：
- `MybatisInterceptor`

### 功能：
提供**MyBatis操作自动填充功能**：
- 自动注入创建人、创建时间
- 自动注入更新人、更新时间
- 自动注入部门编码
- 支持多租户ID自动填充
- 批量操作支持

### 复用方式：
```java
// 1. 实体类继承JeecgEntity（已包含相应字段）
public class User extends JeecgEntity {
    // 自动拥有以下字段：
    // private String createBy;
    // private LocalDateTime createTime;
    // private String updateBy;
    // private LocalDateTime updateTime;
    // private String sysOrgCode;
    // private String tenantId; // 如果启用多租户
}

// 2. 在Service中执行保存或更新操作
userService.save(user); // 自动填充createBy、createTime等字段
userService.updateById(user); // 自动填充updateBy、updateTime字段

// 3. 启用多租户支持
// 在MybatisPlusSaasConfig中设置OPEN_SYSTEM_TENANT_CONTROL = true
```

---

## 五十九、动态数据源拦截器模块（Dynamic Datasource Interceptor）

### 类名：
- `DynamicDatasourceInterceptor`

### 功能：
提供**动态数据源切换的HTTP拦截支持**：
- 根据请求参数动态切换数据源
- 请求完成后自动清理数据源上下文
- 支持多租户数据源切换场景
- 可扩展的拦截逻辑

### 复用方式：
```java
// 1. 通过URL参数切换数据源
// GET /api/users?dsName=slave

// 2. 在Controller中手动切换
@GetMapping("/data")
public Result<?> getData(@RequestParam(required = false) String dsName) {
    if (StringUtils.isNotEmpty(dsName)) {
        DynamicDataSourceContextHolder.push(dsName);
    }
    try {
        // 查询逻辑，使用指定数据源
        return Result.OK(userService.list());
    } finally {
        DynamicDataSourceContextHolder.clear();
    }
}

// 3. 配置拦截路径
// 在WebMvcConfiguration中注册拦截器并指定路径
```

---

## 六十、AutoPoi Excel配置模块（AutoPoi Excel Configuration）

### 类名：
- `AutoPoiConfig`
- `AutoPoiDictConfig`

### 功能：
提供**Excel导入导出的字典翻译支持**：
- 注解式字典配置（@Excel dicCode属性）
- 自动翻译字典值（导出时：1→男，导入时：男→1）
- 支持系统字典和表字典
- 下划线处理兼容

### 复用方式：
```java
// 1. 在实体字段上使用@Excel注解
public class UserExportVO {
    
    @Excel(name = "用户名", width = 15)
    private String username;
    
    @Excel(name = "性别", width = 15, dicCode = "sex")
    private String sex; // 导出显示"男/女"，导入时自动转换为"1/2"
    
    @Excel(name = "部门", width = 20, dicCode = "id", dicText = "depart_name", dictTable = "sys_depart")
    private String departId; // 表字典支持
}

// 2. 导出Excel
List<UserExportVO> list = userService.listForExport();
ExportParams params = new ExportParams("用户列表", "用户数据");
Workbook workbook = ExcelExportUtil.exportExcel(params, UserExportVO.class, list);

// 3. 导入Excel
ImportParams params = new ImportParams();
params.setTitleRows(1);
params.setHeadRows(1);
List<UserExportVO> list = ExcelImportUtil.importExcel(file.getInputStream(), UserExportVO.class, params);
```

---

## 六十一、REST模板配置模块（REST Template Configuration）

### 类名：
- `RestTemplateConfig`

### 功能：
提供**HTTP客户端REST模板的统一配置**：
- 连接超时和读取超时设置
- 简单的ClientHttpRequestFactory配置
- 微服务间HTTP调用支持
- 可自定义扩展

### 复用方式：
```java
// 1. 注入使用
@Autowired
private RestTemplate restTemplate;

// 2. 调用外部API
public String callExternalApi() {
    String url = "https://api.example.com/data";
    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
    return response.getBody();
}

// 3. 自定义配置
// 创建自定义的RestTemplateConfig继承类，重写restTemplate()方法
@Bean
public RestTemplate customRestTemplate() {
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    return restTemplate;
}
```

---

## 六十二、静态配置模块（Static Configuration）

### 类名：
- `StaticConfig`

### 功能：
提供**系统静态参数的统一管理和注入**：
- OSS访问密钥配置
- 邮件发送人配置
- 定时任务发送开关
- 注解式属性注入

### 复用方式：
```java
// 1. 注入使用
@Autowired
private StaticConfig staticConfig;

// 2. 获取配置值
String accessKey = staticConfig.getAccessKeyId();
String emailFrom = staticConfig.getEmailFrom();
boolean timeJobSend = staticConfig.getTimeJobSend();

// 3. 配置文件示例
// jeecg:
//   oss:
//     accessKey: your-access-key
//     secretKey: your-secret-key
// spring:
//   mail:
//     username: noreply@example.com
//     timeJobSend: true
```

---

## 六十三、Swagger3配置模块（Swagger3 Configuration）

### 类名：
- `Swagger3Config`

### 功能：
提供**OpenAPI 3.0（Swagger3）接口文档生成**：
- 自动生成API文档
- JWT Token安全配置
- 排除登录等接口的安全要求
- 路径匹配优化和缓存

### 复用方式：
```java
// 1. 自动生成：启动应用后访问 http://localhost:8080/doc.html

// 2. 配置开关
// knife4j:
//   production: false  # true:生产环境关闭文档，false:开发环境开启文档

// 3. 接口分组配置
// 可以在Swagger3Config中添加多个Docket Bean实现接口分组

// 4. 自定义排除路径
// 修改excludedPaths集合，添加不需要Token验证的接口路径
```

---

## 六十四、应用上下文工具模块（Application Context Util）

### 类名：
- `AutoPoiConfig.applicationContextUtil()` 方法

### 功能：
提供**Spring应用上下文访问支持**：
- 解决AutoPoi在非Spring环境下获取Bean的问题
- 统一的应用上下文访问入口
- 兼容性配置

### 复用方式：
```java
// 1. 在非Spring管理类中获取Bean
ApplicationContext context = ApplicationContextUtil.getContext();
CommonAPI commonApi = context.getBean(CommonAPI.class);

// 2. AutoPoi字典翻译使用
// AutoPoi通过ApplicationContextUtil获取CommonAPI，然后查询字典数据

// 3. 扩展使用
// 可以在任何需要获取Spring Bean的地方使用ApplicationContextUtil
```