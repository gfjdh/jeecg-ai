import { BasicColumn, FormSchema } from '/@/components/Table';

// 列配置
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
    dataIndex: 'sex_dictText',
    width: 80,
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
    dataIndex: 'major_dictText',
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

// 查询表单配置
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
  {
    field: 'sex',
    label: '性别',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'sex',
      stringToNumber: true,
      placeholder: '请选择性别',
    },
    colProps: { span: 6 },
  },
  {
    field: 'birthday',
    label: '出生日期',
    component: 'DatePicker',
    componentProps: {
      valueFormat: 'YYYY-MM-DD'
    },
    colProps: { span: 6 },
  },
  {
    field: 'major',
    label: '专业',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'major',
      placeholder: '请选择专业',
    },
    colProps: { span: 6 },
  },
  {
    field: 'className',
    label: '班级',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'year',
    label: '年级',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'phone',
    label: '手机号',
    component: 'Input',
    colProps: { span: 6 },
  },
];

// 表单配置
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
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'sex',
      stringToNumber: true,
      placeholder: '请选择性别',
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
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'major',
      placeholder: '请选择专业',
    },
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
