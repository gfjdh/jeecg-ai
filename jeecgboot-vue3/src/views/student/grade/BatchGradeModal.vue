<template>
  <BasicModal v-bind="$attrs" @register="registerModal" title="批量成绩录入" width="900px" @ok="handleSubmit" okText="关闭" :showCancelBtn="false">
    <div class="p-4">
      <div class="mb-4 flex items-center">
        <span class="mr-2">选择课程：</span>
        <JDictSelectTag v-model:value="formState.course" dictCode="course" placeholder="请选择课程" class="w-48 mr-4" />
        
        <span class="mr-2">年级：</span>
        <Select 
           v-model:value="formState.year" 
           placeholder="选择年级" 
           style="width: 120px; margin-right: 16px;"
           :options="yearOptions"
           :loading="yearLoading"
           @change="handleYearChange"
        />
        
        <span class="mr-2">班级：</span>
        <Select
           v-model:value="formState.className" 
           :disabled="!formState.year"
           placeholder="请选择班级" 
           style="width: 200px; margin-right: 16px;" 
           :options="classOptions"
           :loading="classLoading"
        />
        
        <Button type="primary" @click="loadStudents" :loading="loading">加载学生</Button>
      </div>

      <JVxeTable
        ref="tableRef"
        rowNumber
        rowSelection
        keep-source
        keyboardEdit
        :columns="columns"
        :dataSource="dataSource"
        :height="500"
        :loading="loading"
        :editConfig="{ trigger: 'click', mode: 'cell', showIcon: true }"
        :mouseConfig="{ selected: true }"
        :keyboardConfig="{ isArrow: true, isEnter: true, isTab: true, isEdit: true }"
        @blur="handleEditClosed"
      />
    </div>
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, reactive, onMounted } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { JVxeTable } from '/@/components/jeecg/JVxeTable';
  import { JVxeTypes, JVxeColumn } from '/@/components/jeecg/JVxeTable/types';
  import { Button, message, Select } from 'ant-design-vue';
  import { JDictSelectTag } from '/@/components/Form';
  import { getStudentList, getYearList, getClassListByYear } from '/@/api/student/student.api';
  import { saveOrUpdateStudentGrade } from '/@/api/student/grade.api';

  const emit = defineEmits(['success', 'register']);
  const [registerModal, { setModalProps, closeModal }] = useModalInner(() => {
     setModalProps({ showCancelBtn: false });
  });
  
  const loading = ref(false);
  const formState = reactive({
    course: '',
    year: undefined,
    className: undefined
  });
  const dataSource = ref<any[]>([]);

  // 年级班级相关
  const yearLoading = ref(false);
  const classLoading = ref(false);
  const yearOptions = ref<any[]>([]);
  const classOptions = ref<any[]>([]);

  // 辅助函数：去重
  function uniqBy(arr, key) {
    const seen = new Set();
    return arr.filter(item => {
      const k = item[key];
      return seen.has(k) ? false : seen.add(k);
    });
  }

  // 初始化加载年级
  onMounted(async () => {
    yearLoading.value = true;
    try {
      // 使用通用字典接口查询所有年级，前端去重
      const res = await getYearList();
      if (res) {
         const unique = uniqBy(res, 'value');
         // 排序
         unique.sort((a, b) => a.value.localeCompare(b.value));
         yearOptions.value = unique.map(item => ({ label: item.value, value: item.value }));
      }
    } catch (e) {
      console.error(e);
    } finally {
      yearLoading.value = false;
    }
  });

  // 处理年级变化
  async function handleYearChange(val) {
    formState.className = undefined;
    classOptions.value = [];
    if (!val) return;

    classLoading.value = true;
    try {
      // 根据年级查询班级
      const res = await getClassListByYear(val);
      if (res) {
         const unique = uniqBy(res, 'value');
         // 排序
         unique.sort((a, b) => a.value.localeCompare(b.value));
         classOptions.value = unique.map(item => ({ label: item.value, value: item.value }));
      }
    } catch (e) {
      console.error(e);
      message.error('加载班级列表失败');
    } finally {
      classLoading.value = false;
    }
  }


  const columns: JVxeColumn[] = [
    {
      title: '学号',
      key: 'studentNo',
      width: 150,
      type: JVxeTypes.normal,
      sortable: true
    },
    {
      title: '姓名',
      key: 'name',
      width: 150,
      type: JVxeTypes.normal,
      sortable: true
    },
    {
      title: '班级',
      key: 'className',
      width: 150,
      type: JVxeTypes.normal,
      sortable: true
    },
    {
      title: '成绩',
      key: 'score',
      width: 150,
      type: JVxeTypes.inputNumber,
      sortable: true,
      validateRules: [
        { required: true, message: '${title}不能为空' },
        { pattern: /^((100)|([1-9]?[0-9])(\.[0-9]{1,2})?)$/, message: '请输入0-100之间的数字' }
      ],
      placeholder: '请输入成绩'
    }
  ];

  async function loadStudents() {
    if (!formState.className) {
      message.warning('请输入班级名称');
      return;
    }
    if (!formState.course) {
       message.warning('请选择课程');
       return;
    }

    loading.value = true;
    try {
      // 查询学生列表
      const res = await getStudentList({ year: formState.year, className: formState.className, pageSize: 1000 });
      const list = res.records || [];
      
      if (list.length === 0) {
        message.warning('未找到该班级的学生');
        dataSource.value = [];
        return;
      }

      dataSource.value = list.map(item => ({
        id: item.id, // 唯一标识，JVxeTable需要
        studentNo: item.studentNo,
        name: item.name,
        className: item.className,
        course: formState.course,
        score: null
      }));
    } catch (e) {
      console.error(e);
      message.error('加载学生失败');
    } finally {
      loading.value = false;
    }
  }

  // 自动保存处理
  async function handleEditClosed(event) {
    const { row, column } = event;
    // 只有成绩列变化时才保存
    if (column.key === 'score') {
       await saveOne(row);
    }
  }

  // 单条保存
  async function saveOne(row) {
      if (row.score === null || row.score === '' || row.score === undefined) return;
      // 简单数字校验
      if (isNaN(Number(row.score)) || Number(row.score) < 0 || Number(row.score) > 100) {
          message.warning(`学号 ${row.studentNo} 成绩无效`);
          return;
      }
      
      try {
          await saveOrUpdateStudentGrade({
            studentNo: row.studentNo,
            course: formState.course,
            score: row.score
          }, false);
      } catch (e) {
          console.error(e);
      }
  }

  async function handleSubmit() {
    closeModal();
    emit('success');
  }
</script>
