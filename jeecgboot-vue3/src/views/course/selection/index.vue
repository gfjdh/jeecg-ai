<template>
  <div class="p-4">
    <Dashboard ref="dashboardRef" />
    <a-row :gutter="16">
      <a-col :span="10">
        <TimeTable ref="timetableRef" />
      </a-col>
      <a-col :span="14">
        <CourseList @refresh="onSelectionChanged" @preview="onPreview" />
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
      const dashboardRef = ref();

      const onSelectionChanged = () => {
         if (timetableRef.value) {
            timetableRef.value.refresh();
         }
         if (dashboardRef.value) {
            dashboardRef.value.refresh();
         }
      };

      const onPreview = (record: any) => {
         if (timetableRef.value) {
            timetableRef.value.setPreview(record);
         }
      };

      return { timetableRef, dashboardRef, onSelectionChanged, onPreview };
    },
  });
</script>
