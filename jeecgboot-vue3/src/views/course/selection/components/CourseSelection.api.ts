import { defHttp } from '/@/utils/http/axios';

enum Api {
  Summary = '/course/studentCourseSelection/summary',
  Schedule = '/course/studentCourseSelection/schedule',
  Available = '/course/studentCourseSelection/available',
  Rush = '/course/studentCourseSelection/rush',
  RushStatus = '/course/studentCourseSelection/rush/status',
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
