<template>
  <div class="p-4 bg-white mb-4">
    <h3 class="mb-4">当前课表</h3>
    <table class="timetable">
      <thead>
        <tr>
          <th>节次/星期</th>
          <th v-for="day in days" :key="day">{{ day }}</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="section in 11" :key="section">
          <td class="section-col">{{ section }}</td>
          <td v-for="(_day, dayIndex) in days" :key="dayIndex" class="course-cell">
             <div 
                v-if="matrix[dayIndex + 1][section] && matrix[dayIndex + 1][section].isHead"
                class="course-card"
                :class="{ 'preview-card': matrix[dayIndex + 1][section].isPreview }"
                :style="{ height: getCardHeight(matrix[dayIndex + 1][section].span) }"
             >
                <div class="course-name">{{ matrix[dayIndex + 1][section].data.course_dictText || matrix[dayIndex + 1][section].data.course }}</div>
                <div class="course-loc">{{ matrix[dayIndex + 1][section].data.location }}</div>
                <div class="course-teacher">T: {{ matrix[dayIndex + 1][section].data.teacherNo_dictText || matrix[dayIndex + 1][section].data.teacherNo }}</div>
             </div>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script lang="ts">
  import { defineComponent, ref, onMounted } from 'vue';
  import { getSchedule } from './CourseSelection.api';

  export default defineComponent({
    name: 'TimeTable',
    setup() {
      const days = ['周一', '周二', '周三', '周四', '周五', '周六', '周日'];
      const initMatrix = () => {
        const m: any = {};
        for(let d=1; d<=7; d++) {
            m[d] = {};
            for(let s=1; s<=11; s++) {
                m[d][s] = null;
            }
        }
        return m;
      };

      const matrix = ref<any>(initMatrix());
      const scheduleList = ref<any[]>([]);
      const previewItem = ref<any>(null);

      const buildMatrix = () => {
         const m = initMatrix();
         const addItem = (item: any, isPreview: boolean) => {
             const day = item.weekday;
             const start = item.startSection;
             const end = item.endSection;
             if (!day || !start || !end) return;

             const span = end - start + 1;
             
             if(m[day] && m[day][start] !== undefined) {
                 m[day][start] = { isHead: true, span: span, data: item, isPreview: isPreview };
                 for(let i=1; i<span; i++) {
                     if (start + i <= 11) {
                         // 修复预览课程与已有课程重叠时，已有课程消失的问题
                         if (isPreview && m[day][start + i] && m[day][start + i].isHead) {
                             continue;
                         }
                         m[day][start + i] = { isHead: false }; 
                     }
                 }
             }
         };

         // 添加已选课程
         if(scheduleList.value) {
             scheduleList.value.forEach((item: any) => addItem(item, false));
         }
         // 添加预览课程
         if (previewItem.value) {
             if (previewItem.value.scheduleList && previewItem.value.scheduleList.length > 0) {
                 previewItem.value.scheduleList.forEach((item: any) => addItem(item, true));
             } else {
                 addItem(previewItem.value, true);
             }
         }
         matrix.value = m;
      };

      const loadData = async () => {
         const res: any = await getSchedule(); 
         const list = res.records || res;
         scheduleList.value = list || [];
         buildMatrix();
      };
      
      const refresh = () => {
          loadData();
      };

      const setPreview = (item: any) => {
          previewItem.value = item;
          buildMatrix();
      };

      const getCardHeight = (span: number) => {
          return `calc(50px * ${span} + ${(span-1)*2}px)`; 
      };

      onMounted(loadData);

      return { days, matrix, getCardHeight, refresh, setPreview };
    },
  });
</script>

<style scoped>
.timetable {
  width: 100%;
  border-collapse: collapse;
}
.timetable th, .timetable td {
  border: 1px solid #eee;
  text-align: center;
  position: relative; 
  height: 50px;
}
.section-col {
  width: 50px;
  background: #fafafa;
}
.course-cell {
  vertical-align: top;
  padding: 0;
}
.course-card {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  background-color: #e6f7ff;
  border-left: 3px solid #1890ff;
  padding: 4px;
  font-size: 12px;
  overflow: hidden;
  z-index: 1;
}
.course-card.preview-card {
  background-color: #fffbe6 !important;
  border-left: 3px solid #faad14 !important;
  z-index: 10;
}
.course-name { font-weight: bold; }
</style>
