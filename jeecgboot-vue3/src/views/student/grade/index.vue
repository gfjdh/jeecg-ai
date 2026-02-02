<template>
  <div class="p-4">
    <GradeAnalysis />

    <BaseList
      ref="baseListRef"
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
    >
      <template #toolbar>
        <Button type="primary" v-if="hasPermission('studentGrade:batchAdd')" @click="openBatchModal">批量录入</Button>
      </template>
    </BaseList>
    <BatchGradeModal @register="registerBatchModal" @success="handleBatchSuccess" />
  </div>
</template>
<script lang="ts" name="student-grade" setup>
  import { ref } from 'vue';
  import { Button } from '/@/components/Button';
  import { useModal } from '/@/components/Modal';
  import { getStudentGradeList, 
           deleteStudentGrade, 
           batchDeleteStudentGrade, 
           getExportUrl, 
           getImportUrl, 
           saveOrUpdateStudentGrade, 
           getStudentGradeById } from '/@/api/student/grade.api';
  import { columns, searchFormSchema, formSchema } from './grade.data';
  import BaseList from '../components/BaseList.vue';
  import BatchGradeModal from './BatchGradeModal.vue';
  import GradeAnalysis from './GradeAnalysis.vue';
  import { usePermission } from '/@/hooks/web/usePermission'

  const { hasPermission } = usePermission();

  const baseListRef = ref();
  const [registerBatchModal, { openModal: openBatchModal }] = useModal();

  function handleBatchSuccess() {
    baseListRef.value?.reload();
  }
</script>
