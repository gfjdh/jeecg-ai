import { BasicColumn, FormSchema } from '/@/components/Table';

export const columns: BasicColumn[] = [
  {
    title: '科目',
    dataIndex: 'course_dictText',
    width: 120,
  },
  {
    title: '教师工号',
    dataIndex: 'teacherNo',
    width: 100,
  },
  {
    title: '类型',
    dataIndex: 'courseType_dictText',
    width: 50,
  },
  {
    title: '学分',
    dataIndex: 'courseCredit',
    width: 50,
  },
  {
    title: '已选/容量',
    dataIndex: 'capacity',
    width: 80,
    customRender: ({ record }) => {
      // 显示 已选/容量
      return `${record.selectedCount || 0}/${record.capacity}`;
    },
  },
  {
    title: '星期',
    dataIndex: 'weekday_dictText',
    width: 50,
  },
  {
    title: '节次',
    dataIndex: 'startSection',
    customRender: ({ record }) => {
      return `${record.startSection} - ${record.endSection}节`;
    },
    width: 50,
  },
  {
    title: '地点',
    dataIndex: 'location',
    width: 50,
  },
  { title: '操作', dataIndex: 'action', width: 50, slots: { customRender: 'action' } },
];

export const searchFormSchema: FormSchema[] = [
  { 
    field: 'subject', 
    label: '科目', 
    component: 'JDictSelectTag', 
    componentProps: 
    { 
      dictCode: 'course' 
    } 
  },
  { 
    field: 'courseType', 
    label: '类型', 
    component: 'JDictSelectTag', 
    componentProps: 
    { 
      dictCode: 'course_type', 
      stringToNumber: true 
    } 
  }
];
