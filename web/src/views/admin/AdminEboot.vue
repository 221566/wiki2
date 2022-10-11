<template>
  <a-layout>
    <a-layout-content
            :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <p>
        <a-button type="primary" @click="add()" size="large">
          新增
        </a-button>
      </p>
      <a-table
              :columns="columns"
              :row-key="record => record.id"
              :data-source="ebooks"
              :pagination="pagination"
              :loading="loading"
              @change="handleTableChange"
      >
        <template #bodyCell="{ text: cover }">
          <img v-if="cover" :src="cover" alt="avatar" />
        </template>
        <template v-slot:action="{ text, record }">
          <a-space size="small">

            <a-button type="primary" @click="edit(record)">
              编辑
            </a-button>

            <a-popconfirm
                    title="删除后不可恢复，确认删除?"
                    ok-text="是"
                    cancel-text="否"
                    @confirm="handleDelete(record.id)"
            >
              <a-button type="danger">
                删除
              </a-button>
            </a-popconfirm>

          </a-space>
        </template>
      </a-table>
    </a-layout-content>
  </a-layout>
  <a-modal
          v-model:visible="modalVisible"
          title="电子书"
          :confirm-loading="modalLoading"
          @ok="handleModalOk"
  >
    <a-form :model="ebook" :label-col="{span: 6}" :wrapper-col="{ span: 18 }">
      <a-form-item label="封面">
        <a-input v-model:value="ebook.cover" />
      </a-form-item>
      <a-form-item label="名称">
        <a-input v-model:value="ebook.name" />
      </a-form-item>
      <a-form-item label="分类一">
        <a-input v-model:value="ebook.category1Id" />
      </a-form-item>
      <a-form-item label="描述">
        <a-input v-model:value="ebook.desc" type="tect"/>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script lang="ts">
  import { defineComponent, onMounted, ref } from 'vue';
  import axios from 'axios';

  export default defineComponent({
    name: 'AdminEbook',
    setup() {
      const ebooks = ref();
      const pagination = ref({
        current: 1,
        pageSize: 4,
        total: 0
      });
      const loading = ref(false);

      const columns = [
        {
          title: '封面',
          dataIndex: 'cover',
          slots: { customRender: 'cover' }
        },
        {
          title: '名称',
          dataIndex: 'name'
        },
        {
          title: '分类',
          key:'category1Id',
          dataIndex: 'category1Id',
        },
        {
          title: '文档数',
          dataIndex: 'category2Id',
        },
        {
          title: '阅读数',
          dataIndex: 'category3Id'
        },
        {
          title: '点赞数',
          dataIndex: 'category3Id'
        },
        {
          title: 'Action',
          key: 'action',
          slots: { customRender: 'action' }
        }
      ];

      /**
       * 数据查询
       **/
      const handleQuery = (params: any) => {
        loading.value = true;
        axios.get("/ebook/selectEbook",{
          params: {
            page: params.page,
            size: params.size
          }
        }).then((response) =>{
          loading.value = false;
          const data = response.data;
          ebooks.value = data.content.list;

          pagination.value.current = params.page;
          pagination.value.total = data.content.total;
        });
      };
      const ebook = ref({});
      const modalVisible = ref(false);
      const modalLoading = ref(false);
      const handleModalOk = () => {
        modalLoading.value = true;
        axios.post("/ebook/save",ebook.value).then((response) =>{
          const data = response.data;
          if (data.success){
            modalVisible.value = false;
            modalLoading.value = false;
            //重新加载列表
            handleQuery({
              page:pagination.value.current,//重新查询分页组件当前所在的页码
              size:pagination.value.pageSize
            });
          }
        });
      };

      const edit = (record: any) => {
        modalVisible.value = true;
        ebook.value = record
      };

      const add = () => {
        modalVisible.value = true;
        ebook.value = {};
      };

      const handleDelete = (id: number) => {
        axios.delete("/ebook/delete/"+id).then((response) =>{
          const data = response.data;
          if (data.success){
            //重新加载列表
            handleQuery({
              page:pagination.value.current,//重新查询分页组件当前所在的页码
              size:pagination.value.pageSize
            });
          }
        });
      };
      /**
       * 表格点击页码时触发
       */
      const handleTableChange = (pagination: any) => {
        console.log("看看自带的分页参数都有啥：" + pagination);
        handleQuery({
          page: pagination.current,
          size: pagination.pageSize
        });
      };
      onMounted(() => {
        handleQuery({
          page:1,
          size:pagination.value.pageSize
        });
      });
      return {
        ebooks,
        pagination,
        columns,
        loading,
        handleTableChange,

        add,
        edit,
        handleDelete,

        modalVisible,
        modalLoading,
        handleModalOk,
        ebook
      }
    }
  });
</script>