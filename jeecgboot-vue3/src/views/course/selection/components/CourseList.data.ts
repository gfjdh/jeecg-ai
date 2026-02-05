import { BasicColumn, FormSchema } from '/@/components/Table';
import { h } from 'vue';

export const columns: BasicColumn[] = [
  {
    title: '课程编号',
    dataIndex: 'courseId',
    width: 80,
  },
  {
    title: '科目',
    dataIndex: 'course_dictText',
    width: 120,
  },
  {
    title: '教师',
    dataIndex: 'teacherNo_dictText',
    width: 80,
  },
  {
    title: '类型',
    dataIndex: 'courseType_dictText',
    width: 40,
  },
  {
    title: '学分',
    dataIndex: 'courseCredit',
    width: 40,
  },
  {
    title: '已选/容量',
    dataIndex: 'capacity',
    width: 50,
    customRender: ({ record }) => {
      // 显示 已选/容量
      return `${record.selectedCount || 0}/${record.capacity}`;
    },
  },
  {
    title: '时间',
    dataIndex: 'startSection',
    customRender: ({ record }) => {
      if (record.scheduleList && record.scheduleList.length > 0) {
        return h('div', record.scheduleList.map(item => h('div', `${item.weekday_dictText} ${item.startSection} - ${item.endSection}节`)));
      }
      return `${record.weekday_dictText} ${record.startSection} - ${record.endSection}节`;
    },
    width: 100,
  },
  {
    title: '地点',
    dataIndex: 'location',
    customRender: ({ record }) => {
      if (record.scheduleList && record.scheduleList.length > 0) {
        return h('div', record.scheduleList.map(item => h('div', item.location)));
      }
      return record.location;
    },
    width: 60,
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
