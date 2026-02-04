import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
    {
    title: '年级',
    align:"center",
    dataIndex: 'year'
   },
   {
    title: '班号',
    align:"center",
    dataIndex: 'classId'
   },
   {
    title: '课程号',
    align:"center",
    dataIndex: 'courseId'
   },
   {
    title: '课程类型',
    align:"center",
    dataIndex: 'courseType_dictText'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
  {
      label: "年级",
      field: 'year',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "班号",
      field: 'classId',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "课程号",
      field: 'courseId',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "课程类型",
      field: 'courseType',
      component: 'JDictSelectTag',
      componentProps: {
        dictCode: 'course_type',
        placeholder: '请选择课程类型',
      },
      //colProps: {span: 6},
 	},
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '年级',
    field: 'year',
    component: 'Input',
  },
  {
    label: '班号',
    field: 'classId',
    component: 'Input',
  },
  {
    label: '课程号',
    field: 'courseId',
    component: 'Input',
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

// 高级查询数据
export const superQuerySchema = {
  year: {title: '年级',order: 3,view: 'text', type: 'string',},
  classId: {title: '班号',order: 0,view: 'text', type: 'string',},
  courseId: {title: '课程号',order: 1,view: 'text', type: 'string',},
  courseType: {title: '课程类型',order: 2,view: 'text', type: 'string',},
};

/**
* 流程表单调用这个方法获取formSchema
* @param param
*/
export function getBpmFormSchema(_formData): FormSchema[]{
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}
