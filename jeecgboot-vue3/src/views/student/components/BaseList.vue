<template>
  <div>
    <BasicTable @register="registerTable" :rowSelection="rowSelection">
      <template #tableTitle>
        <slot name="toolbar"></slot>
        <Button type="primary" preIcon="ant-design:plus-outlined" @click="handleAdd">新增</Button>
        <Button type="primary" preIcon="ant-design:export-outlined" @click="onExportXls"> 导出</Button>
        <a-upload name="file" :showUploadList="false" :customRequest="(file) => onImportXls(file)">
          <Button type="primary" preIcon="ant-design:import-outlined">导入</Button>
        </a-upload>
        <Dropdown
          v-if="selectedRowKeys.length > 0"
          :trigger="['click']"
          :dropMenuList="[
            {
              text: '删除',
              icon: 'ant-design:delete-outlined',
              event: '1',
              popConfirm: {
                title: '是否确认删除',
                confirm: batchHandleDelete,
              },
            },
          ]"
        >
          <Button>
            批量操作
            <Icon icon="ant-design:down-outlined" />
          </Button>
        </Dropdown>
      </template>
      <template #action="{ record }">
        <TableAction :actions="getActions(record)" />
      </template>
    </BasicTable>
    <BaseModal 
      @register="registerModal" 
      @success="reload" 
      :titlePrefix="modalTitlePrefix"
      :formSchema="modalFormSchema"
      :saveApi="modalSaveApi"
      :getDetailApi="modalGetDetailApi"
    />
  </div>
</template>
<script lang="ts" setup>
  import { PropType } from 'vue';
  import { BasicTable, TableAction, BasicColumn } from '/@/components/Table';
  import { useModal } from '/@/components/Modal';
  import { Icon } from '/@/components/Icon'; // 图标列表在 src\components\Icon\src\IconPicker.vue
  import { Button } from '/@/components/Button';
  import { Dropdown } from '/@/components/Dropdown';
  import { useColumnSorter } from './useHeaderSorter';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { useMethods } from '/@/hooks/system/useMethods';
  import { FormSchema } from '/@/components/Form/index';
  import BaseModal from './BaseModal.vue';

  const emit = defineEmits(['viewDetail']);

  const props = defineProps({
    // 列表配置
    tableTitle: { type: String, required: true },
    listApi: { type: Function as PropType<(...arg: any) => Promise<any>>, required: true },
    columns: { type: Array as PropType<BasicColumn[]>, required: true },
    searchFormSchema: { type: Array as PropType<FormSchema[]>, required: true },
    
    // 删除接口
    deleteApi: { type: Function, required: true },
    batchDeleteApi: { type: Function, required: true },
    
    // 导入导出配置
    exportUrl: { type: String, required: true },
    importUrl: { type: String, required: true },
    exportName: { type: String, required: true },

    // 弹窗配置
    modalTitlePrefix: { type: String, required: true },
    modalFormSchema: { type: Array as PropType<FormSchema[]>, required: true },
    modalSaveApi: { type: Function, required: true },
    modalGetDetailApi: { type: Function, required: true },
    hasDetail: { type: Boolean, default: false },
  });

  const [ registerModal, { openModal }] = useModal();
  const { handleExportXls, handleImportXls } = useMethods();

  const { columns: processedColumns } = useColumnSorter(props.columns);
  const { tableContext } = useListPage({
    tableProps: {
      title: props.tableTitle,
      api: props.listApi,
      columns: processedColumns,
      formConfig: {
        schemas: props.searchFormSchema,
      },
      showIndexColumn: true,
      rowKey: 'id',
      beforeFetch: (params) => {
        props.searchFormSchema.forEach((item) => {
          if (item.component === 'Input' && params[item.field]) {
            params[item.field] = '*' + params[item.field] + '*';
          }
        });
        return params;
      },
    },
  });
  
  const [registerTable, { reload }, { rowSelection, selectedRowKeys }] = tableContext;

  function getActions(record) {
    const actions = [
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
    if (props.hasDetail) {
      actions.unshift({
        label: '详情',
        onClick: handleDetail.bind(null, record),
      });
    }
    return actions;
  }

  function handleDetail(record) {
    emit('viewDetail', record);
  }

  function handleAdd() {
    openModal(true, {
      isUpdate: false,
    });
  }

  function handleEdit(record) {
    openModal(true, {
      record,
      isUpdate: true,
    });
  }

  function handleDelete(record) {
    props.deleteApi({ id: record.id }, reload);
  }

  function onExportXls() {
    handleExportXls(props.exportName, props.exportUrl, { selections: selectedRowKeys.value.join(",") });
  }

  function onImportXls(file) {
    handleImportXls(file, props.importUrl, reload);
  }

  function batchHandleDelete() {
    props.batchDeleteApi({ ids: selectedRowKeys.value.join(',') }, reload);
  }

  defineExpose({ reload });
</script>
