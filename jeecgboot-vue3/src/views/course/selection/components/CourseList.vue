<template>
  <div class="p-4 bg-white">
    <h3 class="mb-4">可选课程 (选课)</h3>
    
    <div class="mb-4">
      <a-input-search
        v-model:value="searchText"
        placeholder="搜索科目..."
        enter-button
        @search="loadCourses"
        style="width: 300px"
      />
    </div>

    <a-table :columns="columns" :data-source="courses" row-key="id" :loading="loading">
       <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'action'">
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
          <template v-else-if="column.key === 'courseType'">
              {{ record.courseType === 1 ? '必修' : '选修' }}
          </template>
       </template>
    </a-table>
  </div>
</template>

<script lang="ts">
  import { defineComponent, ref, onMounted } from 'vue';
  import { Input, Table, Button, message } from 'ant-design-vue';
  import { getAvailableCourses, rushCourse, getRushStatus } from './CourseSelection.api';

  export default defineComponent({
    name: 'CourseList',
    components: {
      AInputSearch: Input.Search,
      ATable: Table,
      AButton: Button
    },
    setup() {
      const searchText = ref('');
      const courses = ref([]);
      const loading = ref(false);

      const columns = [
        { title: '课程号', dataIndex: 'courseId', key: 'courseId' },
        { title: '科目', dataIndex: 'course', key: 'course' },
        { title: '教师工号', dataIndex: 'teacherNo', key: 'teacherNo' },
        { title: '学分', dataIndex: 'courseCredit', key: 'courseCredit' },
        { title: '容量', dataIndex: 'capacity', key: 'capacity' },
        { title: '类型', dataIndex: 'courseType', key: 'courseType' },
        { title: '操作', key: 'action' },
      ];

      const loadCourses = async () => {
         loading.value = true;
         try {
           const res = await getAvailableCourses({ subject: searchText.value });
           courses.value = (res || []).map((c: any) => ({...c, rushing: false}));
         } finally {
           loading.value = false;
         }
      };

      const pollStatus = async (courseId: string, record: any) => {
          try {
             const res = await getRushStatus(courseId);
             if (res === 'PENDING') {
                 message.loading('排队中...', 1);
                 setTimeout(() => pollStatus(courseId, record), 1000);
             } else if (res === 'SUCCESS') {
                 message.success('选课成功！');
                 record.rushing = false;
                 // Refresh schedule?
                 // emit('refresh');
             } else {
                 message.error(res); // FAILED: ...
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
            // Result is text message "Queued..." or Error
            if (res && res.includes('Queued')) {
                message.info('请求已进入队列');
                pollStatus(record.courseId, record);
            } else {
                // Should not happen if error is caught by axios interceptor?
                // Usually Result.error throws? 
                // Depends on defHttp config. Assuming returns data or throws.
                message.warning(res || '未知状态');
                record.rushing = false;
            }
         } catch (e) {
            record.rushing = false;
         }
      };

      onMounted(loadCourses);

      return { searchText, courses, loading, columns, loadCourses, handleRush };
    },
  });
</script>
