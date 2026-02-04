<template>
  <div class="p-4">
    <Dashboard />
    
    <a-row :gutter="16">
      <a-col :span="10">
        <TimeTable ref="timetableRef" />
      </a-col>
      <a-col :span="14">
        <CourseList @refresh="onSelectionChanged" />
      </a-col>
    </a-row>
  </div>
</template>

<script lang="ts">
  import { defineComponent, ref } from 'vue';
  import { Row, Col } from 'ant-design-vue';
  import Dashboard from './components/Dashboard.vue';
  import TimeTable from './components/TimeTable.vue';
  import CourseList from './components/CourseList.vue';

  export default defineComponent({
    name: 'CourseSelectionPage',
    components: { Dashboard, TimeTable, CourseList, ARow: Row, ACol: Col },
    setup() {
      const timetableRef = ref();

      const onSelectionChanged = () => {
         // Refresh timetable
         if (timetableRef.value) {
            // timetableRef.value.loadData();
            // Need to expose loadData or trigger it.
            // Simplified: Refresh page or window.location.reload() or implement exposed method.
            window.location.reload(); 
         }
      };

      return { timetableRef, onSelectionChanged };
    },
  });
</script>
