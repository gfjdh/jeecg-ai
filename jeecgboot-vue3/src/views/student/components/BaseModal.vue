<template>
  <BasicModal v-bind="$attrs" @register="registerModal" :title="getTitle" @ok="handleSubmit" :width="700">
    <BasicForm @register="registerForm" />
  </BasicModal>
</template>
<script lang="ts" setup>
  import { ref, computed, unref, PropType } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, useForm, FormSchema } from '/@/components/Form/index';
  
  const emit = defineEmits(['success', 'register']);
  
  const props = defineProps({
    titlePrefix: { type: String, required: true },
    formSchema: { type: Array as PropType<FormSchema[]>, required: true },
    saveApi: { type: Function, required: true },
    getDetailApi: { type: Function, required: true },
  });

  const isUpdate = ref(true);
  
  const [registerForm, { resetFields, setFieldsValue, validate }] = useForm({
    labelWidth: 100,
    schemas: props.formSchema,
    showActionButtonGroup: false,
    actionColOptions: {
      span: 24,
    },
  });
  
  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    await resetFields();
    setModalProps({ confirmLoading: false });
    isUpdate.value = !!data?.isUpdate;
    if (unref(isUpdate)) {
      let record = await props.getDetailApi({ id: data.record.id });
      await setFieldsValue({
        ...record,
      });
    }
  });
  
  const getTitle = computed(() => (!unref(isUpdate) ? `新增${props.titlePrefix}` : `编辑${props.titlePrefix}`));
  
  async function handleSubmit() {
    try {
      const values = await validate();
      setModalProps({ confirmLoading: true });
      await props.saveApi(values, isUpdate.value);
      closeModal();
      emit('success');
    } finally {
      setModalProps({ confirmLoading: false });
    }
  }
</script>
