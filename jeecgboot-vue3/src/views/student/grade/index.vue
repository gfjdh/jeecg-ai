<template>
  <div>
    <BasicTable @register="registerTable" :rowSelection="rowSelection">
      <template #tableTitle>
        <a-button type="primary" preIcon="ant-design:plus-outlined" @click="handleAdd">新增</a-button>
        <a-button type="primary" preIcon="ant-design:export-outlined" @click="onExportXls"> 导出</a-button>
        <a-upload name="file" :showUploadList="false" :customRequest="(file) => onImportXls(file)">
          <a-button type="primary" preIcon="ant-design:import-outlined">导入</a-button>
        </a-upload>
        <a-dropdown v-if="selectedRowKeys.length > 0">
          <template #overlay>
            <a-menu>
              <a-menu-item key="1" @click="batchHandleDelete">
                <Icon icon="ant-design:delete-outlined"></Icon>
                删除
              </a-menu-item>
            </a-menu>
          </template>
          <a-button>批量操作
            <Icon icon="ant-design:down-outlined"></Icon>
          </a-button>
        </a-dropdown>
      </template>
      <template #action="{ record }">
        <TableAction :actions="getActions(record)" />
      </template>
    </BasicTable>
    <GradeModal @register="registerModal" @success="reload" />
  </div>
</template>
<script lang="ts" name="student-grade" setup>

  import { BasicTable, TableAction } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { Icon } from '/@/components/Icon';
  import { getStudentGradeList, deleteStudentGrade, batchDeleteStudentGrade, getExportUrl, getImportUrl } from '/@/api/student/grade.api';
  import { columns, searchFormSchema } from './grade.data';
  import GradeModal from './GradeModal.vue';
  import { useHeaderFilter } from '../components/useHeaderFilter';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { useMethods } from '/@/hooks/system/useMethods';
  import { useMessage } from '/@/hooks/web/useMessage';

  // 注册弹窗
  const [registerModal, { openModal }] = useModal();
  const { handleExportXls, handleImportXls } = useMethods();
  const { createMessage } = useMessage();

  // 列表页面公共参数、方法
  const { columns: processedColumns } = useHeaderFilter(columns);
  const { tableContext } = useListPage({
    tableProps: {
      title: '学生成绩列表',
      api: getStudentGradeList,
      columns: processedColumns,
      formConfig: {
        schemas: searchFormSchema,
      },
      showIndexColumn: true,
      rowKey: 'id',
      beforeFetch: (params) => {
        // 模糊查询处理
        searchFormSchema.forEach((item) => {
          if (item.component === 'Input' && params[item.field]) {
            params[item.field] = '*' + params[item.field] + '*';
          }
        });
        return params;
      },
    },
  });
  
  // 解构列表上下文
  const [registerTable, { reload }, { rowSelection, selectedRowKeys }] = tableContext;

  /**
   * 操作项配置
   * @param record
   */
  function getActions(record) {
    return [
      {
        label: '编辑',
        onClick: handleEdit.bind(null, record),
      },
      {
        label: '删除',
        popConfirm: {
          title: '是否确认删除',
          confirm: handleDelete.bind(null, record),
        },
      },
    ];
  }

  /**
   * 新增事件
   */
  function handleAdd() {
    openModal(true, {
      isUpdate: false,
    });
  }

  /**
   * 编辑事件
   */
  function handleEdit(record) {
    openModal(true, {
      record,
      isUpdate: true,
    });
  }

  /**
   * 删除事件
   */
  function handleDelete(record) {
    deleteStudentGrade({ id: record.id }, reload);
  }

  /**
   * 导出事件
   */
  function onExportXls() {
    if (selectedRowKeys.value.length === 0) {
      createMessage.warning("请选择要导出的数据");
      return;
    }
    handleExportXls("学生成绩列表", getExportUrl, { selections: selectedRowKeys.value.join(",") });
  }

  /**
   * 导入事件
   */
  function onImportXls(file) {
    handleImportXls(file, getImportUrl, reload);
  }
  
  /**
   * 批量删除事件
   */
  function batchHandleDelete() {
    batchDeleteStudentGrade({ ids: selectedRowKeys.value.join(',') }, reload);
  }
</script>
