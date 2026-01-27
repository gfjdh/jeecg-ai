import { defHttp } from '/@/utils/http/axios';
import { Modal } from 'ant-design-vue';

enum Api {
  list = '/student/list',
  save = '/student/add',
  edit = '/student/edit',
  get = '/student/queryById',
  delete = '/student/delete',
  deleteBatch = '/student/deleteBatch',
}

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
