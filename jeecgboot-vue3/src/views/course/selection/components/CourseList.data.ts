import { BasicColumn, FormSchema } from '/@/components/Table';

export const columns: BasicColumn[] = [
  {
    title: '科目',
    dataIndex: 'course_dictText',
    width: 150,
  },
  {
    title: '教师',
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
    title: '容量',
    dataIndex: 'capacity',
    width: 50,
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
