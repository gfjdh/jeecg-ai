import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '专业ID',
    align:"center",
    dataIndex: 'majorId'
   },
   {
    title: '适用入学年份',
    align:"center",
    dataIndex: 'startYear'
   },
   {
    title: '选修学分要求',
    align:"center",
    dataIndex: 'requiredElectiveCredits'
   },
   {
    title: '必修学分要求',
    align:"center",
    dataIndex: 'requiredMajorCredits'
   },
   {
    title: '选课开始时间',
    align:"center",
    dataIndex: 'courseSelectionBegin'
   },
   {
    title: '选课结束时间',
    align:"center",
    dataIndex: 'courseSelectionEnd'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '专业ID',
    field: 'majorId',
    component: 'InputNumber',
  },
  {
    label: '适用入学年份',
    field: 'startYear',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: false},
                 { pattern: /^-?\d+\.?\d*$/, message: '请输入数字!'},
          ];
     },
  },
  {
    label: '选修学分要求',
    field: 'requiredElectiveCredits',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: false},
                 { pattern: /^-?\d+\.?\d*$/, message: '请输入数字!'},
          ];
     },
  },
  {
    label: '必修学分要求',
    field: 'requiredMajorCredits',
    component: 'InputNumber',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: false},
                 { pattern: /^-?\d+\.?\d*$/, message: '请输入数字!'},
          ];
     },
  },
  {
    label: '选课开始时间',
    field: 'courseSelectionBegin',
    component: 'DatePicker',
    componentProps: {
       showTime: true,
       valueFormat: 'YYYY-MM-DD HH:mm:ss'
     },
  },
  {
    label: '选课结束时间',
    field: 'courseSelectionEnd',
    component: 'DatePicker',
    componentProps: {
       showTime: true,
       valueFormat: 'YYYY-MM-DD HH:mm:ss'
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

// 高级查询数据
export const superQuerySchema = {
  majorId: {title: '专业ID',order: 0,view: 'number', type: 'number',},
  startYear: {title: '适用入学年份',order: 1,view: 'text', type: 'string',},
  requiredElectiveCredits: {title: '选修学分要求',order: 2,view: 'number', type: 'number',},
  requiredMajorCredits: {title: '必修学分要求',order: 3,view: 'number', type: 'number',},
  courseSelectionBegin: {title: '选课开始时间',order: 4,view: 'datetime', type: 'string',},
  courseSelectionEnd: {title: '选课结束时间',order: 5,view: 'datetime', type: 'string',},
};

/**
* 流程表单调用这个方法获取formSchema
* @param param
*/
export function getBpmFormSchema(_formData): FormSchema[]{
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}