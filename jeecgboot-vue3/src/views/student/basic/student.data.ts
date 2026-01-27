import { BasicColumn, FormSchema } from '/@/components/Table';

export const columns: BasicColumn[] = [
  {
    title: '学号',
    dataIndex: 'studentNo',
    width: 120,
    align: 'left',
  },
  {
    title: '姓名',
    dataIndex: 'name',
    width: 120,
    align: 'left',
  },
  {
    title: '性别',
    dataIndex: 'sex',
    width: 80,
    customRender: ({ text }) => {
      return text === 1 ? '男' : (text === 2 ? '女' : '未知');
    },
  },
  {
    title: '出生日期',
    dataIndex: 'birthday',
    width: 150,
    customRender: ({ text }) => {
      return !text ? "" : (text.length > 10 ? text.substr(0, 10) : text);
    },
  },
  {
    title: '专业',
    dataIndex: 'major',
    width: 150,
  },
  {
    title: '班级',
    dataIndex: 'className',
    width: 120,
  },
  {
    title: '年级',
    dataIndex: 'year',
    width: 100,
  },
  {
    title: '手机号',
    dataIndex: 'phone',
    width: 150,
  },
];

export const searchFormSchema: FormSchema[] = [
  {
    field: 'name',
    label: '姓名',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'studentNo',
    label: '学号',
    component: 'Input',
    colProps: { span: 6 },
  },
];

export const formSchema: FormSchema[] = [
  {
    label: '主键',
    field: 'id',
    component: 'Input',
    show: false,
  },
  {
    field: 'studentNo',
    label: '学号',
    component: 'Input',
    required: true,
  },
  {
    field: 'name',
    label: '姓名',
    component: 'Input',
    required: true,
  },
  {
    field: 'sex',
    label: '性别',
    component: 'Select',
    componentProps: {
      options: [
        { label: '男', value: 1 },
        { label: '女', value: 2 },
      ],
    },
  },
  {
    field: 'birthday',
    label: '出生日期',
    component: 'DatePicker',
    componentProps: {
      valueFormat: 'YYYY-MM-DD'
    }
  },
  {
    field: 'major',
    label: '专业',
    component: 'Input',
  },
  {
    field: 'className',
    label: '班级',
    component: 'Input',
  },
  {
    field: 'year',
    label: '年级',
    component: 'Input',
  },
  {
    field: 'phone',
    label: '手机号',
    component: 'Input',
  },
];
