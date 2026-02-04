import { BasicColumn, FormSchema } from '/@/components/Table';

export const columns: BasicColumn[] = [
  { title: '课程号', dataIndex: 'courseId', width: 100 },
  { title: '科目', dataIndex: 'course_dictText', width: 150 },
  { title: '教师工号', dataIndex: 'teacherNo', width: 120 },
  { title: '学分', dataIndex: 'courseCredit', width: 80 },
  { title: '容量', dataIndex: 'capacity', width: 80 },
  {
    title: '类型',
    dataIndex: 'courseType_dictText',
    width: 100,
  },
  { title: '操作', dataIndex: 'action', width: 100, slots: { customRender: 'action' } },
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
