<template>
  <div class="p-4 bg-white">
    <h3 class="mb-4">可选课程 (选课)</h3>
    <BasicTable @register="registerTable">
      <template #action="{ record }">
         <a-button 
           type="primary" 
           danger 
           size="small" 
           :loading="record.rushing"
           @click="handleRush(record)"
         >
           选课
         </a-button>
      </template>
    </BasicTable>
  </div>
</template>

<script lang="ts">
  import { defineComponent } from 'vue';
  import { BasicTable, useTable } from '/@/components/Table';
  import { getAvailableCourses, rushCourse, getRushStatus } from './CourseSelection.api';
  import { message, Button } from 'ant-design-vue';
  import { columns, searchFormSchema } from './CourseList.data';

  export default defineComponent({
    name: 'CourseList',
    components: { BasicTable, AButton: Button },
    setup() {
      const [registerTable, { reload }] = useTable({
        title: '可选课程',
        api: getAvailableCourses,
        columns,
        useSearchForm: true,
        formConfig: {
            labelWidth: 80,
            schemas: searchFormSchema
        }
      });

      const pollStatus = async (courseId: string, record: any) => {
          try {
             const res = await getRushStatus(courseId);
             if (res === 'PENDING') {
                 message.loading('排队中...', 1);
                 setTimeout(() => pollStatus(courseId, record), 1000);
             } else if (res === 'SUCCESS') {
                 message.success('选课成功！');
                 record.rushing = false;
                 reload();
             } else {
                 message.error(res); // 失败: ...
                 record.rushing = false;
             }
          } catch (e) {
             record.rushing = false; 
          }
      };

      const handleRush = async (record: any) => {
         record.rushing = true;
         try {
            const res = await rushCourse(record.courseId);
            if (res && res.includes('Queued')) {
                message.info('请求已进入队列');
                pollStatus(record.courseId, record);
            } else {
                message.warning(res || '未知状态');
                record.rushing = false;
            }
         } catch (e) {
            record.rushing = false;
         }
      };
      
      return { registerTable, handleRush };
    },
  });
</script>
