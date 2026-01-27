# JeecgBoot Vue3 前端框架可复用的工具与组件清单**，按文件路径顺序排列

---

### **一、API 接口模块**
- `src/api/common/api.ts` → 通用接口
- `src/api/demo/` → 示例接口（account、error、system、table、tree等）
- `src/api/sys/` → 系统接口（menu、upload、user等）

---

### **二、组件库（src/components/）**
#### 1. **Application** - 应用级组件
- AppDarkModeToggle.vue
- AppLocalePicker.vue
- AppLogo.vue
- AppProvider.vue
- AppSearch.vue
- AppSearchFooter.vue
- AppSearchKeyItem.vue
- AppSearchModal.vue
- useMenuSearch.ts
- useAppContext.ts

#### 2. **Authority** - 权限组件
- Authority.vue

#### 3. **Basic** - 基础组件
- BasicArrow.vue
- BasicHelp.vue
- BasicTitle.vue

#### 4. **Button** - 按钮组件
- BasicButton.vue
- JUploadButton.vue
- PopConfirmButton.vue

#### 5. **CardList** - 卡片列表组件
- CardList.vue

#### 6. **ClickOutSide** - 外部点击组件
- ClickOutSide.vue

#### 7. **CodeEditor** - 代码编辑器
- CodeEditor.vue
- codemirror/CodeMirror.vue

#### 8. **Container** - 容器组件
- LazyContainer.vue
- ScrollContainer.vue
- CollapseContainer.vue
- CollapseHeader.vue

#### 9. **ContextMenu** - 右键菜单组件
- ContextMenu.vue

#### 10. **CountDown** - 倒计时组件
- CountButton.vue
- CountdownInput.vue
- useCountdown.ts

#### 11. **CountTo** - 数字动画组件
- CountTo.vue

#### 12. **Cropper** - 图片裁剪组件
- Cropper.vue
- CropperAvatar.vue
- CopperModal.vue

#### 13. **Description** - 描述列表组件
- Description.vue
- useDescription.ts

#### 14. **Drawer** - 抽屉组件
- BasicDrawer.vue
- DrawerHeader.vue
- DrawerFooter.vue
- useDrawer.ts

#### 15. **Dropdown** - 下拉组件
- Dropdown.vue

#### 16. **Form** - 表单组件
- BasicForm.vue
- ApiRadioGroup.vue
- ApiSelect.vue
- ApiTreeSelect.vue
- FormItem.vue
- Middleware.vue
- RadioButtonGroup.vue
- JFormContainer.vue
- 数十个 Jeecg 扩展表单组件（如 JAreaLinkage、JDatePickerMultiple、JDictSelectTag、JUpload 等）

#### 17. **Icon** - 图标组件
- Icon.vue
- IconList.vue
- IconPicker.vue
- SvgIcon.vue

#### 18. **InFilter** - 内嵌筛选组件
- CascaderPcaInFilter.vue
- DatePickerInFilter.vue

#### 19. **JDragNotice** - 拖拽通知组件
- JDragNotice.vue

#### 20. **JVxeCustom** - VxeTable 自定义组件
- 包含多种单元格类型（文件、图片、用户选择、部门选择等）

#### 21. **Loading** - 加载组件
- Loading.vue
- createLoading.ts
- useLoading.ts

#### 22. **Markdown** - Markdown 编辑器组件
- Markdown.vue
- MarkdownViewer.vue

#### 23. **Menu** - 菜单组件
- BasicMenu.vue
- SimpleMenu.vue

#### 24. **Modal** - 弹窗组件
- BasicModal.vue
- JModal.vue
- 弹窗组件（Header、Footer、Close）

#### 25. **Page** - 页面组件
- PageWrapper.vue
- PageFooter.vue

#### 26. **Preview** - 预览组件
- Preview.vue

#### 27. **Qrcode** - 二维码组件
- Qrcode.vue

#### 28. **Scrollbar** - 滚动条组件
- Scrollbar.vue

#### 29. **StrengthMeter** - 密码强度组件
- StrengthMeter.vue

#### 30. **Table** - 表格组件
- BasicTable.vue
- 表格操作组件（TableAction、TableFooter、TableHeader、TableImg 等）
- 可编辑表格组件
- 列设置组件（ColumnSetting、FullScreenSetting、SizeSetting 等）

#### 31. **Time** - 时间组件
- Time.vue

#### 32. **Tinymce** - 富文本编辑器
- Editor.vue
- ImgUpload.vue

#### 33. **Transition** - 过渡动画组件
- CollapseTransition.vue
- ExpandTransition.ts

#### 34. **Tree** - 树形组件
- BasicTree.vue
- Tree.vue（备份）
- TreeIcon.vue

#### 35. **Upload** - 上传组件
- BasicUpload.vue
- FileList.vue
- UploadModal.vue
- useUpload.ts

#### 36. **Verify** - 验证组件
- DragVerify.vue
- ImgRotate.vue

#### 37. **VirtualScroll** - 虚拟滚动组件
- VirtualScroll.vue

#### 38. **Chart** - 图表组件
- Bar.vue、LineMulti.vue、Pie.vue、Radar.vue、Gauge.vue 等十余种图表

#### 39. **Jeecg 扩展组件**
- AIcon.vue
- ExcelButton.vue
- JPrompt.vue
- JVxeTable 组件（增强版可编辑表格）
- OnLine 组件（在线报表、搜索表单）
- UserAvatar.vue
- CaptchaModal.vue
- Comment 组件（评论、文件上传、历史记录）
- ThirdApp 组件（第三方应用对接）

---

### **三、自定义指令（src/directives/）**
- clickOutside.ts
- loading.ts
- permission.ts
- repeatClick.ts
- ripple

---

### **四、工具函数（src/utils/）**
- `areaData/pcaUtils.ts` → 省市区工具
- `auth/index.ts` → 权限工具
- `cache/` → 缓存工具（内存、持久化）
- `cipher.ts` → 加密工具
- `color.ts` → 颜色工具
- `dateUtil.ts` → 日期工具
- `dict/` → 字典工具
- `domUtils.ts` → DOM 工具
- `encryption/` → 加密工具
- `env.ts` → 环境变量工具
- `file/` → 文件处理工具
- `helper/` → 树形数据、验证器等辅助工具
- `http/axios/` → 封装 axios 请求
- `is.ts` → 类型判断工具
- `lib/echarts.ts` → ECharts 封装
- `log.ts` → 日志工具
- `mitt.ts` → 事件总线
- `propTypes.ts` → 属性类型定义
- `uuid.ts` → UUID 生成工具

---

### **五、Hooks（src/hooks/）**
- `component/` → 组件相关 hooks
- `core/` → 核心 hooks（useAttrs、useContext、useLockFn 等）
- `event/` → 事件 hooks（useBreakpoint、useEventListener 等）
- `jeecg/` → Jeecg 扩展 hooks
- `setting/` → 系统设置 hooks
- `system/` → 系统 hooks（useAutoAdapt、useListPage 等）
- `web/` → Web 通用 hooks（useWatermark、useECharts、usePermission 等）

---

### **六、枚举定义（src/enums/）**
- CompTypeEnum.ts
- DateTypeEnum.ts
- appEnum.ts
- breakpointEnum.ts
- cacheEnum.ts
- exceptionEnum.ts
- httpEnum.ts
- jeecgEnum.ts
- menuEnum.ts
- pageEnum.ts
- roleEnum.ts
- sizeEnum.ts

---

### **七、全局配置与工具**
- `src/settings/` → 项目配置（设计、组件、加密、多语言等）
- `src/store/` → Pinia 状态管理模块
- `src/router/` → 路由配置与守卫
- `src/locales/` → 多语言文件
- `src/logics/` → 业务逻辑（错误处理、主题切换等）
- `src/qiankun/` → 微前端支持

---

### **八、布局与页面组件（src/layouts/）**
- 默认布局组件（Header、Sider、Tabs、Footer、Setting）
- 多标签页组件
- 锁屏组件
- 错误页面组件

---

### **九、业务视图组件（src/views/）**
- 包含 Dashboard、Monitor、OpenAPI、System、Super（AI相关）等模块的业务组件，可根据需要复用。

---

### **十、全局注册组件**
- `src/components/registerGlobComp.ts` → 全局组件注册入口
