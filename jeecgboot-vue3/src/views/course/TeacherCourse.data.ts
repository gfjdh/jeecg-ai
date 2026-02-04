import { BasicColumn } from '/@/components/Table';
import { FormSchema } from '/@/components/Table';
import { rules } from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { JVxeTypes, JVxeColumn } from '/@/components/jeecg/JVxeTable/types'
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
  {
    title: '课程号',
    align: "center",
    dataIndex: 'courseId'
  },
  {
    title: '科目',
    align: "center",
    dataIndex: 'course_dictText'
  },
  {
    title: '教师工号',
    align: "center",
    dataIndex: 'teacherNo'
  },
  {
    title: '课程学分',
    align: "center",
    dataIndex: 'courseCredit'
  },
  {
    title: '课程容量',
    align: "center",
    dataIndex: 'capacity'
  },
  {
    title: '课程类型',
    align: "center",
    dataIndex: 'courseType_dictText'
  },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
  {
    label: "课程号",
    field: "courseId",
    component: 'Input',
    //colProps: {span: 6},
  },
  {
    label: "科目",
    field: "course",
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'course',
      placeholder: '请选择课程',
    },
  },
  {
    label: "教师工号",
    field: "teacherNo",
    component: 'Input',
    //colProps: {span: 6},
  },
  {
    label: "课程学分",
    field: "courseCredit",
    component: 'InputNumber',
    //colProps: {span: 6},
  },
  {
    label: "课程容量",
    field: "capacity",
    component: 'InputNumber',
    //colProps: {span: 6},
  },
  {
    label: "课程类型",
    field: "courseType",
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'course_type',
      placeholder: '请选择课程类型',
    },
  },
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '课程号',
    field: 'courseId',
    component: 'Input',
  },
  {
    label: '科目',
    field: 'course',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'course',
      placeholder: '请选择课程',
    },
  },
  {
    label: '教师工号',
    field: 'teacherNo',
    component: 'Input',
  },
  {
    label: '课程学分',
    field: 'courseCredit',
    component: 'InputNumber',
  },
  {
    label: '课程容量',
    field: 'capacity',
    component: 'InputNumber',
  },
  {
    label: '课程类型',
    field: 'courseType',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'course_type',
      placeholder: '请选择课程类型',
    },
  },
  // TODO 主键隐藏字段，目前写死为ID
  {
    label: '',
    field: 'id',
    component: 'Input',
    show: false
  },
];
//子表单数据
//子表表格配置
export const classTimeColumns: JVxeColumn[] = [
  {
    title: '对应课程号',
    key: 'courseId',
    width: "200px",
    placeholder: '请输入${title}',
    defaultValue: '',
  },
  {
    title: 'weekday',
    key: 'weekday',
    type: JVxeTypes.select,
    dictCode: 'weekday',
    width: "200px",
    placeholder: '请选择上课星期',
    defaultValue: '',
  },
  {
    title: 'location',
    key: 'location',
    type: JVxeTypes.input,
    width: "200px",
    placeholder: '请输入上课地点',
    defaultValue: '',
  },
  {
    title: 'start_section',
    key: 'startSection',
    type: JVxeTypes.inputNumber,
    width: "200px",
    placeholder: '请输入上课开始节次',
    defaultValue: '',
  },
  {
    title: 'end_section',
    key: 'endSection',
    type: JVxeTypes.inputNumber,
    width: "200px",
    placeholder: '请输入上课结束节次',
    defaultValue: '',
  },
]


// 高级查询数据
export const superQuerySchema = {
  courseId: { title: '课程号', order: 0, view: 'text', type: 'string', },
  course: { title: '科目', order: 1, view: 'text', type: 'string', },
  teacherNo: { title: '教师工号', order: 2, view: 'text', type: 'string', },
  courseCredit: { title: '课程学分', order: 3, view: 'number', type: 'number', },
  capacity: { title: '课程容量', order: 4, view: 'number', type: 'number', },
  courseType: { title: '课程类型', order: 5, view: 'number', type: 'number', },
  //子表高级查询
  classTime: {
    title: '课程时间安排',
    view: 'table',
    fields: {
      courseId: { title: '对应课程号', order: 0, view: 'text', type: 'string', },
      weekday: { title: 'weekday', order: 1, view: 'number', type: 'number', },
      location: { title: 'location', order: 2, view: 'text', type: 'string', },
      startSection: { title: 'start_section', order: 3, view: 'number', type: 'number', },
      endSection: { title: 'end_section', order: 4, view: 'number', type: 'number', },
    }
  },
};

/**
* 流程表单调用这个方法获取formSchema
* @param param
*/
export function getBpmFormSchema(_formData): FormSchema[] {
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}
