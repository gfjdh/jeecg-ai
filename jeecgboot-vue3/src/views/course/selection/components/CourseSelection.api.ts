import { defHttp } from '/@/utils/http/axios';

enum Api {
  Summary = '/course/studentCourseRush/summary',
  Schedule = '/course/studentCourseRush/schedule',
  Available = '/course/studentCourseRush/available',
  Rush = '/course/studentCourseRush/rush',
  RushStatus = '/course/studentCourseRush/rush/status',
}

export const getSummary = () => {
  return defHttp.get({ url: Api.Summary });
};

export const getSchedule = () => {
  return defHttp.get({ url: Api.Schedule });
};

export const getAvailableCourses = (params?: { subject?: string }) => {
  return defHttp.get({ url: Api.Available, params });
};

export const rushCourse = (courseId: string) => {
  return defHttp.post({ url: Api.Rush, data: { courseId } });
};

export const getRushStatus = (courseId: string) => {
  return defHttp.get({ url: Api.RushStatus, params: { courseId } });
};
