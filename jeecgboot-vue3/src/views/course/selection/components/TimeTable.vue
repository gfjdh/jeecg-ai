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
                :class="getCardClass(matrix[dayIndex + 1][section])"
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

      // 检查课程是否与已选课程相同
      const isSameAsSelected = (_item: any) => {
        if (!previewItem.value || !scheduleList.value.length) return false;
        
        const previewList = previewItem.value.scheduleList || [previewItem.value];
        
        // 检查预览课程是否在已选课程列表中
        return scheduleList.value.some(selected => {
          return previewList.some((preview: any) => {
            // 根据课程唯一标识比较，这里假设id或course字段是唯一的
            return (preview.id && preview.id === selected.id) || 
                   (preview.course && preview.course === selected.course) ||
                   // 或者通过时间和教师等综合判断
                   (preview.weekday === selected.weekday && 
                    preview.startSection === selected.startSection && 
                    preview.endSection === selected.endSection &&
                    preview.teacherNo === selected.teacherNo);
          });
        });
      };

      // 检查课程是否有时间冲突
      const hasTimeConflict = (item: any) => {
        if (!scheduleList.value.length) return false;
        
        const day = item.weekday;
        const start = item.startSection;
        const end = item.endSection;
        
        // 检查是否有时间重叠的已选课程
        return scheduleList.value.some(selected => {
          // 跳过同一天的课程检查（已通过isSameAsSelected处理）
          if (selected.weekday !== day) return false;
          
          // 检查时间段是否重叠
          return !(end < selected.startSection || start > selected.endSection);
        });
      };

      const buildMatrix = () => {
         const m = initMatrix();
         
         const addItem = (item: any, isPreview: boolean, _state?: string) => {
             const day = item.weekday;
             const start = item.startSection;
             const end = item.endSection;
             if (!day || !start || !end) return;

             const span = end - start + 1;
             
             if(m[day] && m[day][start] !== undefined) {
                 // 默认状态
                 let cardState = 'normal';
                 
                 if (isPreview) {
                   // 检查是否是已选课程
                   if (isSameAsSelected(item)) {
                     cardState = 'selected';
                   } 
                   // 检查是否有时间冲突
                   else if (hasTimeConflict(item)) {
                     cardState = 'conflict';
                   } else {
                     cardState = 'preview';
                   }
                 }
                 
                 m[day][start] = { 
                   isHead: true, 
                   span: span, 
                   data: item, 
                   isPreview: isPreview,
                   state: cardState
                 };
                 
                 for(let i=1; i<span; i++) {
                     if (start + i <= 11) {
                         // 预览课程与已有课程重叠时，跳过非头部单元格
                         if (isPreview && m[day][start + i] && m[day][start + i].isHead) {
                             continue;
                         }
                         m[day][start + i] = { 
                           isHead: false,
                           state: cardState
                         }; 
                     }
                 }
             }
         };

         // 添加已选课程
         if(scheduleList.value) {
             scheduleList.value.forEach((item: any) => addItem(item, false, 'normal'));
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

      // 获取卡片类名
      const getCardClass = (cell: any) => {
        if (!cell) return '';
        
        const classes = ['course-card'];
        
        if (cell.state) {
          classes.push(`${cell.state}-card`);
        }
        
        return classes.join(' ');
      };

      const getCardHeight = (span: number) => {
          return `calc(50px * ${span} + ${(span-1)*2}px)`; 
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

      onMounted(loadData);

      return { 
        days, 
        matrix, 
        getCardHeight, 
        getCardClass,
        refresh, 
        setPreview 
      };
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
  padding: 4px;
  font-size: 12px;
  overflow: hidden;
  z-index: 1;
}

/* 正常已选课程 - 蓝色 */
.normal-card {
  background-color: #e6f7ff !important;
  border-left: 3px solid #1890ff !important;
}

/* 选中已选课程 - 绿色 */
.selected-card {
  background-color: #f6ffed !important;
  border-left: 3px solid #52c41a !important;
  z-index: 20;
}

/* 预览课程冲突 - 红色 */
.conflict-card {
  background-color: #fff2f0 !important;
  border-left: 3px solid #ff4d4f !important;
  z-index: 10;
}

/* 无冲突且未选课的选中课程 - 橙色 */
.preview-card {
  background-color: #fffbe6 !important;
  border-left: 3px solid #faad14 !important;
  z-index: 10;
}

.course-name { 
  font-weight: bold; 
  margin-bottom: 2px;
}
.course-loc { 
  font-size: 11px;
  margin-bottom: 2px;
}
.course-teacher { 
  font-size: 11px;
  color: #666;
}
</style>
