<template>
  <BaseList
    tableTitle="学生列表"
    :listApi="getStudentList"
    :columns="columns"
    :searchFormSchema="searchFormSchema"
    :deleteApi="deleteStudent"
    :batchDeleteApi="batchDeleteStudent"
    :exportUrl="getExportUrl"
    :importUrl="getImportUrl"
    exportName="学生列表"
    modalTitlePrefix="学生信息"
    :modalFormSchema="formSchema"
    :modalSaveApi="saveOrUpdateStudent"
    :modalGetDetailApi="getStudentById"
    :hasDetail="true"
    @viewDetail="handleViewDetail"
  />
  <StudentDetailDrawer @register="registerDrawer" />
</template>
<script lang="ts" name="student-basic" setup>
  import { getStudentList, 
           deleteStudent, 
           batchDeleteStudent, 
           getExportUrl, 
           getImportUrl, 
           saveOrUpdateStudent, 
           getStudentById } from '/@/api/student/student.api';
  import { columns, searchFormSchema, formSchema } from './student.data';
  import BaseList from '../components/BaseList.vue';
  import StudentDetailDrawer from './StudentDetailDrawer.vue';
  import { useDrawer } from '/@/components/Drawer';

  const [registerDrawer, { openDrawer }] = useDrawer();

  function handleViewDetail(record: any) {
    openDrawer(true, record);
  }
</script>
