<template>
  <div class="p-4 bg-white">
    <h3 class="mb-4">可选课程 (选课)</h3>
    <BasicTable @register="registerTable" @row-click="handleRowClick">
      <template #action="{ record }">
         <a-button 
           v-if="!selectedCourseIds.has(record.courseId)"
           type="primary" 
           size="small" 
           :loading="record.rushing"
           @click.stop="handleRush(record)"
         >
           选课
         </a-button>
         <a-button 
           v-else
           type="primary" 
           danger
           size="small" 
           :loading="record.dropping"
           @click.stop="handleDrop(record)"
         >
           退课
         </a-button>
      </template>
    </BasicTable>
  </div>
</template>

<script lang="ts">
  import { defineComponent, ref, onMounted } from 'vue';
  import { BasicTable, useTable } from '/@/components/Table';
  import { getAvailableCourses, rushCourse, dropCourse, getRushStatus, getSchedule } from './CourseSelection.api';
  import { message, Button } from 'ant-design-vue';
  import { columns, searchFormSchema } from './CourseList.data';

  export default defineComponent({
    name: 'CourseList',
    components: { BasicTable, AButton: Button },
    emits: ['refresh', 'preview'],
    setup(_, { emit }) {
      const selectedCourseIds = ref<Set<string>>(new Set());

      const [registerTable, { reload }] = useTable({
        title: '可选课程',
        api: getAvailableCourses,
        columns,
        useSearchForm: true,
        locale: {
          emptyText: '当前不是选课时间或没有可选课程',
        },
        afterFetch: (data) => {
             if (!data) return [];
             const map = new Map<string, any>();
             data.forEach((item: any) => {
                 if (map.has(item.courseId)) {
                     const existing = map.get(item.courseId);
                     existing.scheduleList.push(item);
                 } else {
                     const newItem = { ...item, scheduleList: [item] };
                     map.set(item.courseId, newItem);
                 }
             });
             return Array.from(map.values());
        },
        formConfig: {
            labelWidth: 80,
            schemas: searchFormSchema,
            actionColOptions: { 
                span: 6,
                style: { paddingLeft: '20px' }
            }
        }
      });

      // 获取已选课程ID列表
      const fetchSelectedCourses = async () => {
        try {
          const res = await getSchedule();
          if (res && res.records) {
             selectedCourseIds.value = new Set(res.records.map((item: any) => item.courseId));
          }
        } catch (e) {
          console.error(e);
        }
      };

      onMounted(() => {
        fetchSelectedCourses();
      });

      const pollStatus = async (courseId: string, record: any) => {
          try {
             const res = await getRushStatus(courseId);
             if (res === 'PENDING' || res === '') {
                 message.loading('排队中...', 1);
                 setTimeout(() => pollStatus(courseId, record), 1000);
             } else if (res === 'SUCCESS: 选课成功' || res === 'SUCCESS: 已选该课程') {
                 record.rushing = false;
                 await fetchSelectedCourses();
                 reload();
                 emit('refresh');
             } else {
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
            if (res && res.includes('排队')) { 
                pollStatus(record.courseId, record);
            } else if (res === '') {
                pollStatus(record.courseId, record);
            } else {
                message.warning(res || '未知状态');
                record.rushing = false;
            }
         } catch (e) {
            record.rushing = false;
         }
      };

      const handleDrop = async (record: any) => {
         record.dropping = true;
         try {
            await dropCourse(record.courseId);
            record.dropping = false;
            await fetchSelectedCourses();
            reload();
            emit('refresh');
         } catch (e) {
            record.dropping = false;
         }
      };

      const handleRowClick = (record: any) => {
        emit('preview', record);
      };
      
      return { registerTable, handleRush, handleDrop, handleRowClick, selectedCourseIds };
    },
  });
</script>
