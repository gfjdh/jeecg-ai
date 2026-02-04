<template>
  <div class="p-4 bg-white mb-4">
    <a-row :gutter="16">
      <a-col :span="6">
        <a-statistic title="已选必修学分" :value="stats.selectedCompulsory" :precision="1" />
      </a-col>
      <a-col :span="6">
        <a-statistic title="已选选修学分" :value="stats.selectedElective" :precision="1" />
      </a-col>
      <a-col :span="6">
        <a-statistic title="已修必修学分" :value="stats.completedCompulsory" :precision="1" />
      </a-col>
      <a-col :span="6">
        <a-statistic title="已修选修学分" :value="stats.completedElective" :precision="1" />
      </a-col>
    </a-row>
  </div>
</template>

<script lang="ts">
  import { defineComponent, ref, onMounted } from 'vue';
  import { Statistic, Row, Col } from 'ant-design-vue';
  import { getSummary } from './CourseSelection.api';

  export default defineComponent({
    name: 'Dashboard',
    components: { AStatistic: Statistic, ARow: Row, ACol: Col },
    setup() {
      const stats = ref({
        selectedCompulsory: 0,
        selectedElective: 0,
        completedCompulsory: 0,
        completedElective: 0,
      });

      onMounted(async () => {
        const res = await getSummary();
        if (res && res.records && res.records.length > 0) {
          stats.value = res.records[0];
        }
      });

      return { stats };
    },
  });
</script>
