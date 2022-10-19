<template>
  <a-layout>
    <a-layout-content
            :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <p>
        <a-form
                layout="inline"
                :model="param">
          <a-form-item>
            <a-input v-model:value="param.name" placeholder="名称">
            </a-input>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="handleQuery({page: 1, size: pagination.pageSize})">
              查询
            </a-button>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="add()">
              新增
            </a-button>
          </a-form-item>
        </a-form>
      </p>
      <a-table
              :columns="columns"
              :row-key="record => record.id"
              :data-source="users"
              :pagination="pagination"
              :loading="loading"
              @change="handleTableChange"
      >
        <template #bodyCell="{ text: cover }">
          <img v-if="cover" :src="cover" alt="avatar" />
        </template>
        <template v-slot:category="{ text, record }">
          <span>{{ getCategoryName(record.category1Id) }} / {{ getCategoryName(record.category2Id) }}</span>
        </template>
        <template v-slot:action="{ text, record }">
          <a-space size="small">

<!--            <router-link :to="'/admin/doc?userId='+record.id">-->
<!--              <a-button type="primary">-->
<!--                文档管理-->
<!--              </a-button>-->
<!--            </router-link>-->
            <a-button type="primary" @click="edit(record)">
              编辑
            </a-button>
            <a-button type="primary" @click="resetPassword(record)">
              重置密码
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
          title="用户信息"
          :confirm-loading="modalLoading"
          @ok="handleModalOk"
  >
    <a-form :model="user" :label-col="{span: 6}" :wrapper-col="{ span: 18 }">
      <a-form-item label="登录名">
        <a-input v-model:value="user.loginName" :disabled="!!user.id"/>
      </a-form-item>
      <a-form-item label="昵称">
        <a-input v-model:value="user.name" />
      </a-form-item>
      <a-form-item label="密码" v-show="!user.id">
        <a-input v-model:value="user.password"/>
      </a-form-item>
<!--      <a-form-item label="描述">-->
<!--        <a-input v-model:value="user.desc" type="tect"/>-->
<!--      </a-form-item>-->
    </a-form>
  </a-modal>

  <a-modal
          v-model:visible="resetmodalVisible"
          title="重置密码"
          :confirm-loading="resetmodalLoading"
          @ok="resethandleModalOk"
  >
    <a-form :model="user" :label-col="{span: 6}" :wrapper-col="{ span: 18 }">
      <a-form-item label="密码">
        <a-input v-model:value="user.password"/>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script lang="ts">
  import { defineComponent, onMounted, ref } from 'vue';
  import axios from 'axios';
  import { message } from 'ant-design-vue';
  import {Tool} from "@/util/tool";
  declare let hexMd5: any;
  declare let KEY: any;

  export default defineComponent({
    name: 'AdminUser',
    setup() {
      const param = ref();
      param.value = {};
      const users = ref();
      const pagination = ref({
        current: 1,
        pageSize: 4,
        total: 0
      });
      const loading = ref(false);

      const columns = [
        {
          title: '登陆名',
          dataIndex: 'loginName'
        },
        {
          title: '名称',
          dataIndex: 'name'
        },
        {
          title: '密码',
          dataIndex: 'password'
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
        axios.get("/user/selectUser",{
          params: {
            page: params.page,
            size: params.size,
            name: param.value.name
          }
        }).then((response) =>{
          loading.value = false;
          const data = response.data;
          if(data.success){
            users.value = data.content.list;

            pagination.value.current = params.page;
            pagination.value.total = data.content.total;
          }else {
            message.error(data.message)
          }
        });
      };

      /**
       * 数组，[100, 101]对应：前端开发 / Vue
       */
      const categoryIds = ref();
      const user = ref();
      const modalVisible = ref(false);
      const modalLoading = ref(false);
      const handleModalOk = () => {
        modalLoading.value = true;
        user.value.category1Id = categoryIds.value[0];
        user.value.category2Id = categoryIds.value[1];
        user.value.password = hexMd5(user.value.password + KEY);
        axios.post("/user/save",user.value).then((response) =>{
          modalLoading.value = false;
          const data = response.data;
          if (data.success){
            modalVisible.value = false;
            //重新加载列表
            handleQuery({
              page:pagination.value.current,//重新查询分页组件当前所在的页码
              size:pagination.value.pageSize
            });
          }else {
            message.error(data.message);
          }
        });
      };

      const edit = (record: any) => {
        modalVisible.value = true;
        user.value = Tool.copy(record);
        categoryIds.value = [user.value.category1Id, user.value.category2Id]
      };

      const add = () => {
        modalVisible.value = true;
        user.value = {};
      };

      const handleDelete = (id: number) => {
        axios.delete("/user/delete/"+id).then((response) =>{
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

      const level1 =  ref();
      let categorys: any;

      const getCategoryName = (cid: number) => {
        // console.log(cid)
        let result = "";
        categorys.forEach((item: any) => {
          if (item.id === cid) {
            // return item.name; // 注意，这里直接return不起作用
            result = item.name;
          }
        });
        return result;
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

      const resetmodalVisible = ref(false);
      const resetmodalLoading = ref(false);
      const resethandleModalOk = () => {
        resetmodalLoading.value = true;
        user.value.password = hexMd5(user.value.password + KEY);
        axios.post("/user/resetPassword",user.value).then((response) =>{
          resetmodalLoading.value = false;
          const data = response.data;
          if (data.success){
            resetmodalVisible.value = false;
            //重新加载列表
            handleQuery({
              page:pagination.value.current,//重新查询分页组件当前所在的页码
              size:pagination.value.pageSize
            });
          }else {
            message.error(data.message);
          }
        });
      };

      const resetPassword = (record: any) => {
        resetmodalVisible.value = true;
        user.value = Tool.copy(record);
        user.value.password = null;
      };

      onMounted(() => {
        handleQuery({
          page: 1,
          size:pagination.value.pageSize
        });
      });
      return {
        param,
        users,
        pagination,
        columns,
        loading,
        handleTableChange,
        handleQuery,
        getCategoryName,

        add,
        edit,
        handleDelete,
        categoryIds,
        level1,

        modalVisible,
        modalLoading,
        handleModalOk,
        user,

        resetmodalVisible,
        resetmodalLoading,
        resethandleModalOk,
        resetPassword,
      }
    }
  });
</script>