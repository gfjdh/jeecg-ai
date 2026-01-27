import { BasicColumn, FormSchema } from '/@/components/Table';
import { checkStudentExist } from '/@/api/student/student.api';

// 列配置
export const columns: BasicColumn[] = [
  {
    title: '学号',
    dataIndex: 'studentNo',
    width: 120,
    align: 'left',
  },
  {
    title: '课程',
    dataIndex: 'course_dictText',
    width: 120,
    align: 'left',
  },
  {
    title: '成绩',
    dataIndex: 'score',
    width: 120,
    align: 'left',
  },
];

// 查询表单配置
export const searchFormSchema: FormSchema[] = [
  {
    field: 'studentNo',
    label: '学号',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'course',
    label: '课程',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'course',
      placeholder: '请选择课程',
    },
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
    dynamicRules: () => {
      return [
        { required: true, message: '请输入学号' },
        {
          validator: (_, value) => {
            if (!value) return Promise.resolve();
            return new Promise((resolve, reject) => {
              checkStudentExist({ studentNo: value }).then((exist) => {
                if (exist) {
                  resolve();
                } else {
                  reject('该学号在学生信息表中不存在');
                }
              }).catch(() => {
                reject('校验学号失败，请稍后重试');
              });
            });
          },
        },
      ];
    },
  },
  {
    field: 'course',
    label: '课程',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'course',
      placeholder: '请选择课程',
    },
    required: true,
  },
  {
    field: 'score',
    label: '成绩',
    component: 'InputNumber',
    componentProps: {
      precision: 1, // 保留一位小数
      step: 0.1,
      max: 100,
      min: 0,
    },
    required: true,
  },
];
