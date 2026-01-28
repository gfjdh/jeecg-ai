import { defHttp } from '/@/utils/http/axios';
import { Modal } from 'ant-design-vue';

enum Api {
  list = '/student/list',
  save = '/student/add',
  edit = '/student/edit',
  get = '/student/queryById',
  delete = '/student/delete',
  deleteBatch = '/student/deleteBatch',
  exportXls = '/student/exportXls',
  importExcel = '/student/importExcel',
  checkByStudentNo = '/student/checkByStudentNo',
}

/**
 * 导出api
 * @param params
 */
export const getExportUrl = Api.exportXls;

/**
 * 导入api
 */
export const getImportUrl = Api.importExcel;

/**
 * 查询列表
 * @param params
 */
export const getStudentList = (params) => {
  return defHttp.get({ url: Api.list, params });
};

/**
 * 保存或者更新
 * @param params
 */
export const saveOrUpdateStudent = (params, isUpdate) => {
  let url = isUpdate ? Api.edit : Api.save;
  return defHttp.post({ url: url, params });
};

/**
 * 查询详情
 * @param params
 */
export const getStudentById = (params) => {
  return defHttp.get({ url: Api.get, params });
};

/**
 * 单条删除
 * @param params
 */
export const deleteStudent = (params, handleSuccess) => {
  return defHttp.delete({ url: Api.delete, params }, { joinParamsToUrl: true }).then(() => {
    handleSuccess();
  });
};

/**
 * 批量删除
 * @param params
 */
export const batchDeleteStudent = (params, handleSuccess) => {
  Modal.confirm({
    title: '确认删除',
    content: '是否删除选中数据',
    okText: '确认',
    cancelText: '取消',
    onOk: () => {
      return defHttp.delete({ url: Api.deleteBatch, params }, { joinParamsToUrl: true }).then(() => {
        handleSuccess();
      });
    },
  });
};

/**
 * 根据学号校验是否存在
 * @param params
 */
export const checkStudentExist = (params) => {
  return defHttp.get({ url: Api.checkByStudentNo, params }, { isTransformResponse: true });
};

/**
 * 获取年级列表
 */
export const getYearList = () => {
  return defHttp.get({ url: '/sys/dict/getDictItems/student_info,year,year' });
};

/**
 * 根据年级获取班级
 * @param year
 */
export const getClassListByYear = (year) => {
  return defHttp.get({ url: `/sys/dict/getDictItems/student_info,class_name,class_name,year='${year}'` });
};
