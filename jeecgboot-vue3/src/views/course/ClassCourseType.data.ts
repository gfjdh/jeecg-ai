import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '班号',
    align:"center",
    dataIndex: 'class'
   },
   {
    title: '课程号',
    align:"center",
    dataIndex: 'courseId'
   },
   {
    title: '课程类型',
    align:"center",
    dataIndex: 'courseType'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '班号',
    field: 'class',
    component: 'InputNumber',
  },
  {
    label: '课程号',
    field: 'courseId',
    component: 'InputNumber',
  },
  {
    label: '课程类型',
    field: 'courseType',
    component: 'InputNumber',
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
  class: {title: '班号',order: 0,view: 'number', type: 'number',},
  courseId: {title: '课程号',order: 1,view: 'number', type: 'number',},
  courseType: {title: '课程类型',order: 2,view: 'number', type: 'number',},
};

/**
* 流程表单调用这个方法获取formSchema
* @param param
*/
export function getBpmFormSchema(_formData): FormSchema[]{
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}