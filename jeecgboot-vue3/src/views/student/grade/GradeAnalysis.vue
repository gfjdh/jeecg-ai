<template>
  <Card :bordered="false" title="成绩分析" class="mb-4" v-if="hasPermission('studentGrade:analysis')">
    <template #extra>
      <Select
        v-model:value="currentCourse"
        placeholder="请选择课程分析"
        style="width: 200px"
        :options="courseOptions"
        @change="handleCourseChange"
        allowClear
      />
    </template>

    <div v-if="currentCourse">
      <Row :gutter="24">
        <Col :span="6">
          <Statistic title="平均分" :value="stats.avg" :precision="1" />
        </Col>
        <Col :span="6">
          <Statistic title="中位数" :value="stats.median" :precision="1" />
        </Col>
        <Col :span="6">
          <Statistic title="最高分" :value="stats.max" :precision="1" />
        </Col>
        <Col :span="6">
          <Statistic title="最低分" :value="stats.min" :precision="1" />
        </Col>
      </Row>

      <Divider />

      <Row :gutter="24">
        <Col :span="12">
          <div ref="pieRef" style="width: 100%; height: 300px"></div>
        </Col>
        <Col :span="12">
          <div ref="barRef" style="width: 100%; height: 300px"></div>
        </Col>
      </Row>
    </div>
    <div v-else class="py-12 text-center text-gray-500">
      请选择一门课程查看成绩分布分析与统计数据
    </div>
  </Card>
</template>

<script lang="ts" setup>
  import { ref, computed, watch, nextTick, onMounted } from 'vue';
  import { Card, Row, Col, Statistic, Select, Divider } from 'ant-design-vue';
  import { useECharts } from '/@/hooks/web/useECharts';
  import { getStudentGradeList } from '/@/api/student/grade.api';
  import { usePermission } from '/@/hooks/web/usePermission'

  const { hasPermission } = usePermission();
  // 用于前端统计分析的数据缓存
  const allData = ref<any[]>([]);
  const currentCourse = ref<string | undefined>(undefined);
  
  const pieRef = ref<HTMLDivElement>(null as any);
  const barRef = ref<HTMLDivElement>(null as any);
  const { setOptions: setPieOptions } = useECharts(pieRef);
  const { setOptions: setBarOptions } = useECharts(barRef);

  const courseOptions = computed(() => {
    const map = new Map();
    allData.value.forEach((item) => {
      // 提取课程ID和课程显示文本
      if (item.course && item.course_dictText && !map.has(item.course)) {
        map.set(item.course, item.course_dictText);
      }
    });
    return Array.from(map.entries()).map(([value, label]) => ({ label, value }));
  });

  const stats = computed(() => {
    if (!currentCourse.value) return { avg: 0, median: 0, max: 0, min: 0 };

    // 过滤出当前课程的有效成绩
    const scores = allData.value
      .filter((i) => i.course === currentCourse.value && i.score !== null && i.score !== undefined)
      .map((i) => Number(i.score))
      .sort((a, b) => a - b);

    if (scores.length === 0) return { avg: 0, median: 0, max: 0, min: 0 };

    const sum = scores.reduce((a, b) => a + b, 0);
    const avg = sum / scores.length;
    const max = scores[scores.length - 1];
    const min = scores[0];

    const mid = Math.floor(scores.length / 2);
    const median = scores.length % 2 !== 0 ? scores[mid] : (scores[mid - 1] + scores[mid]) / 2;

    return { avg, median, max, min };
  });

  const updateCharts = () => {
    if (!currentCourse.value) return;
    const scores = allData.value
      .filter((i) => i.course === currentCourse.value && i.score !== null && i.score !== undefined)
      .map((i) => Number(i.score));

    const ranges = [
      { name: '不及格 (<60)', min: 0, max: 60, value: 0 },
      { name: '及格 (60-70)', min: 60, max: 70, value: 0 },
      { name: '中等 (70-80)', min: 70, max: 80, value: 0 },
      { name: '良好 (80-90)', min: 80, max: 90, value: 0 },
      { name: '优秀 (90-100)', min: 90, max: 101, value: 0 },
    ];

    scores.forEach((s) => {
      const range = ranges.find((r) => s >= r.min && s < r.max);
      if (range) range.value++;
    });

    setPieOptions({
      title: { text: '成绩分布占比', left: 'center' },
      tooltip: { trigger: 'item' },
      legend: { orient: 'vertical', left: 'left' },
      series: [
        {
          name: '成绩分布',
          type: 'pie',
          radius: '50%',
          data: ranges.map((r) => ({ value: r.value, name: r.name })),
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)',
            },
          },
        },
      ],
    });

    setBarOptions({
      title: { text: '成绩分布人数', left: 'center' },
      tooltip: { trigger: 'axis' },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: {
        type: 'category',
        data: ranges.map((r) => r.name),
        axisLabel: { interval: 0, rotate: 30 },
      },
      yAxis: { type: 'value', minInterval: 1 },
      series: [
        {
          data: ranges.map((r) => r.value),
          type: 'bar',
          showBackground: true,
          backgroundStyle: {
            color: 'rgba(180, 180, 180, 0.2)',
          },
          itemStyle: {
            color: '#3b82f6',
          },
        },
      ],
    });
  };

  const handleCourseChange = () => {
    // 触发chart更新
  };

  watch(currentCourse, () => {
    nextTick(() => {
      updateCharts();
    });
  });

  onMounted(async () => {
    // 获取足够多的数据来做基础的前端统计
    const res = await getStudentGradeList({ pageSize: 1000 });
    if (res && res.records) {
      allData.value = res.records;
    }
  });
</script>
