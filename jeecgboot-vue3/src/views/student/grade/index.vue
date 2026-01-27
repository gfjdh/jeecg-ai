<template>
  <BaseList
    tableTitle="学生成绩列表"
    :listApi="getStudentGradeList"
    :columns="columns"
    :searchFormSchema="searchFormSchema"
    :deleteApi="deleteStudentGrade"
    :batchDeleteApi="batchDeleteStudentGrade"
    :exportUrl="getExportUrl"
    :importUrl="getImportUrl"
    exportName="学生成绩列表"
    modalTitlePrefix="成绩"
    :modalFormSchema="formSchema"
    :modalSaveApi="saveOrUpdateStudentGrade"
    :modalGetDetailApi="getStudentGradeById"
  />
</template>
<script lang="ts" name="student-grade" setup>
  import { getStudentGradeList, 
           deleteStudentGrade, 
           batchDeleteStudentGrade, 
           getExportUrl, 
           getImportUrl, 
           saveOrUpdateStudentGrade, 
           getStudentGradeById } from '/@/api/student/grade.api';
  import { columns, searchFormSchema, formSchema } from './grade.data';
  import BaseList from '../components/BaseList.vue';
</script>
