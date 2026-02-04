import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '学生学号',
    align:"center",
    dataIndex: 'studentNo'
   },
   {
    title: '课程号',
    align:"center",
    dataIndex: 'courseId'
   },
   {
    title: '课程学分',
    align:"center",
    dataIndex: 'courseCredit'
   },
   {
    title: '课程类型',
    align:"center",
    dataIndex: 'courseType_dictText'
   },
   {
    title: '修读状态',
    align:"center",
    dataIndex: 'studyStatus_dictText'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
	{
      label: "学生学号",
      field: 'studentNo',
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
      label: "课程学分",
      field: 'courseCredit',
      component: 'InputNumber',
      //colProps: {span: 6},
 	},
	{
      label: "课程类型",
      field: 'courseType',
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"course_type"
      },
      //colProps: {span: 6},
 	},
	{
      label: "修读状态",
      field: 'studyStatus',
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"study_status"
      },
      //colProps: {span: 6},
 	},
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '学生学号',
    field: 'studentNo',
    component: 'Input',
  },
  {
    label: '课程号',
    field: 'courseId',
    component: 'Input',
  },
  {
    label: '课程学分',
    field: 'courseCredit',
    component: 'InputNumber',
  },
  {
    label: '课程类型',
    field: 'courseType',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"course_type"
     },
  },
  {
    label: '修读状态',
    field: 'studyStatus',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"study_status"
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
  studentNo: {title: '学生学号',order: 0,view: 'text', type: 'string',},
  courseId: {title: '课程号',order: 1,view: 'text', type: 'string',},
  courseCredit: {title: '课程学分',order: 2,view: 'number', type: 'number',},
  courseType: {title: '课程类型',order: 3,view: 'number', type: 'number',dictCode: 'course_type',},
  studyStatus: {title: '修读状态',order: 4,view: 'number', type: 'number',dictCode: 'study_status',},
};

/**
* 流程表单调用这个方法获取formSchema
* @param param
*/
export function getBpmFormSchema(_formData): FormSchema[]{
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}