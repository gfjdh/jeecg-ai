<template>
  <BasicDrawer v-bind="$attrs" @register="registerDrawer" title="学生详情" width="50%">
    <Description @register="registerDesc" />
    <div style="margin-top: 20px; display: flex; flex-direction: column; align-items: center;">
       <span style="font-size: 16px; font-weight: bold; margin-bottom: 10px;">学生信息二维码</span>
       <QrCode :value="studentInfo" :width="200" v-if="studentInfo"/>
       <span v-else>暂无学号信息</span>
    </div>
  </BasicDrawer>
</template>
<script lang="ts" setup>
  import { ref, unref, onMounted } from 'vue';
  import { BasicDrawer, useDrawerInner } from '/@/components/Drawer';
  import { Description, useDescription } from '/@/components/Description';
  import { QrCode } from '/@/components/Qrcode';
  import { getStudentById } from '/@/api/student/student.api';
  import { initDictOptions } from '/@/utils/dict';
  import { filterDictText } from '/@/utils/dict/JDictSelectUtil';
  
  const studentInfo = ref('');
  const sexOptions = ref<any[]>([]);
  const majorOptions = ref<any[]>([]);

  onMounted(async () => {
    // 初始化字典配置
    const sexRes = await initDictOptions('sex');
    if (sexRes) sexOptions.value = sexRes;

    const majorRes = await initDictOptions('major');
    if (majorRes) majorOptions.value = majorRes;
  });

  const [registerDesc, { setDescProps }] = useDescription({
    column: 2,
    schema: [
      { field: 'createBy', label: '创建人' },
      { field: 'createTime', label: '创建时间' },
      { field: 'updateBy', label: '更新人' },
      { field: 'updateTime', label: '更新时间' },
      { field: 'studentNo', label: '学号' },
      { field: 'name', label: '姓名' },
      { 
        field: 'sex', 
        label: '性别',
        render: (val) => filterDictText(unref(sexOptions), val),
      },
      { field: 'birthday', label: '出生日期' },
      { 
        field: 'major', 
        label: '专业',
        render: (val) => filterDictText(unref(majorOptions), val),
      },
      { field: 'className', label: '班级' },
      { field: 'year', label: '年级' },
      { field: 'phone', label: '手机号' },
      { field: 'id', label: 'ID' },
    ],
  });

  const [registerDrawer] = useDrawerInner(async (data) => {
    const res = await getStudentById({ id: data.id });
    setDescProps({ data: res });
    studentInfo.value = JSON.stringify(res);
  });
</script>
