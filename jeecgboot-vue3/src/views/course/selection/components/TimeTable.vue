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
          <td v-for="(dayName, dayIndex) in days" :key="dayIndex" class="course-cell">
             <!-- Check matrix for course -->
             <div 
                v-if="matrix[dayIndex + 1][section] && matrix[dayIndex + 1][section].isHead"
                class="course-card"
                :style="{ height: getCardHeight(matrix[dayIndex + 1][section].span) }"
             >
                <div class="course-name">{{ matrix[dayIndex + 1][section].data.courseName }}</div>
                <div class="course-loc">{{ matrix[dayIndex + 1][section].data.location }}</div>
                <div class="course-teacher">T: {{ matrix[dayIndex + 1][section].data.teacherNo }}</div>
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
      // Matrix: day (1-7) -> section (1-11) -> { isHead: bool, span: number, data: any }
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

      const loadData = async () => {
         const list = await getSchedule(); // [{weekday, startSection, endSection, ...}]
         const m = initMatrix();
         
         if(list) {
             list.forEach((item: any) => {
                 const day = item.weekday;
                 const start = item.startSection;
                 const end = item.endSection;
                 const span = end - start + 1;
                 
                 // Head
                 if(m[day] && m[day][start] !== undefined) {
                     m[day][start] = { isHead: true, span: span, data: item };
                     // Occupied
                     for(let i=1; i<span; i++) {
                         if (start + i <= 11) {
                             m[day][start + i] = { isHead: false }; // Mark as occupied but not head (so v-if fails logic needs adjustment logic above only renders if isHead)
                              // Actually my v-if logic "v-if matrix... && isHead" is correct. 
                              // But I also need to make sure I don't render empty td if it's occupied by a rowspan? 
                              // Vue table rendering with rowspan is tricky.
                              // Simpler: CSS Absolute positioning inside relative TD? 
                              // Or just render a card that overflows?
                              // Let's stick to simple "One card per slot" if rowspan is hard, BUT requirement says "Continuous card".
                              // To do true rowspan in simple table loop:
                              // If I use rowspan, I need to NOT render the <td class="course-cell"> for the skipping rows.
                              // But my loop `v-for="dayIndex"` renders a TD for every column.
                              // I can't easily skip TDs in a row-major loop based on column state without complex logic.
                              // Alternative: Flex/Grid layout.
                              // Let's use Absolute positioning wrapper.
                         }
                     }
                 }
             });
         }
         
         // Re-process for simple "One TD, content z-index/absolute" or just manual rowspan Logic.
         // Let's use the default "Card covers multiple" by making the card `height: calc(100% * span + gap)`.
         // But the parent TD has fixed height?
         // Let's assume standard table.
         matrix.value = m;
      };
      
      const getCardHeight = (span: number) => {
          // Approximate height if each row is e.g. 50px
          // better: calc(100% * span + (span - 1) * border)
          // But since we are in a cell, we just want to push it down? 
          // Actually, if we use `rowspan` in `td`, we need to change the template.
          // But modifying the template loop to check rowspan is hard.
          // Strategy: Use CSS Absolute Position relative to a container, OR just use `min-height` and let it flow?
          // Requirement: "Whole card covers multiple sections".
          // Let's try height: `calc(50px * ${span})` and `position: absolute` with `z-index: 10`.
          return `calc(50px * ${span} + ${(span-1)*2}px)`; 
      };

      onMounted(loadData);

      return { days, matrix, getCardHeight };
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
  position: relative; /* For absolute cards */
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
.course-name { font-weight: bold; }
</style>
