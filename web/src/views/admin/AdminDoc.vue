<template>
  <a-layout>
    <a-layout-content
            :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <a-row :gutter="24">
        <a-col :span="8">
          <p>
            <a-form
                    layout="inline"
                    :model="param">
              <a-form-item>
                <a-input v-model:value="param.name" placeholder="名称">
                </a-input>
              </a-form-item>
              <a-form-item>
                <a-button type="primary" @click="handleQuery()">
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
                  v-if="level1.length > 0"
                  :columns="columns"
                  :row-key="record => record.id"
                  :data-source="level1"
                  :loading="loading"
                  :pagination = "false"
                  size="small"
                  :defaultExpandAllRows="true"
          >
            <template #name="{ text, record }">
              {{record.sort}} {{text}}
            </template>
            <template v-slot:action="{ text, record }">
              <a-space size="small">

                <a-button type="primary" @click="edit(record)" size="small">
                  编辑
                </a-button>

                <a-popconfirm
                        title="删除后不可恢复，确认删除?"
                        ok-text="是"
                        cancel-text="否"
                        @confirm="handleDelete(record.id)"
                >
                  <a-button type="danger" size="small">
                    删除
                  </a-button>
                </a-popconfirm>

              </a-space>
            </template>
          </a-table>
        </a-col>
        <a-col :span="16">
          <p>
            <a-form layout="inline" :model="param">
              <a-form-item>
                <a-button type="primary" @click="handleModalOk()">
                  保存
                </a-button>
              </a-form-item>
            </a-form>
          </p>

          <a-form :model="doc" layout="vertical">
            <a-form-item>
              <a-input v-model:value="doc.name" placeholder="名称"/>
            </a-form-item>

            <a-form-item>
              <a-tree-select
                      v-model:value="doc.parent"
                      show-search
                      style="width: 100%"
                      :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
                      placeholder="请选择父文档"
                      allow-clear
                      tree-default-expand-all
                      :tree-data="treeSelectData"
                      :replaceFields="{title: 'name',key: 'id', value: 'id'}"
              >
              </a-tree-select>
            </a-form-item>

            <!--      <a-form-item label="父分类">-->
            <!--&lt;!&ndash;        <a-input v-model:value="doc.parent" />&ndash;&gt;-->
            <!--        <a-select-->
            <!--                ref="select"-->
            <!--                v-model:value="doc.parent"-->
            <!--        >-->
            <!--          <a-select-option value="0">无</a-select-option>-->
            <!--          <a-select-option v-for="c in level1" :key="c.id" :value="c.id" :disabled="doc.id === c.id">{{c.name}}</a-select-option>-->
            <!--        </a-select>-->
            <!--      </a-form-item>-->
            <a-form-item>
              <a-input v-model:value="doc.sort" placeholder="顺序"/>
            </a-form-item>
            <a-form-item>
              <a-button type="primary" @click="handlePreviewContent()">
                <EyeOutlined /> 内容预览
              </a-button>
            </a-form-item>
            <a-form-item label="内容">
              <div id="content"></div>
            </a-form-item>
          </a-form>
        </a-col>
      </a-row>

      <a-drawer width="900" placement="right" :closable="false" :visible="drawerVisible" @close="onDrawerClose">
        <div class="wangeditor" :innerHTML="previewHtml"></div>
      </a-drawer>

    </a-layout-content>
  </a-layout>
<!--  <a-modal-->
<!--          v-model:visible="modalVisible"-->
<!--          title="文档"-->
<!--          :confirm-loading="modalLoading"-->
<!--          @ok="handleModalOk"-->
<!--  >-->
<!--    -->
<!--  </a-modal>-->
</template>

<script lang="ts">
  import { defineComponent, onMounted, ref,createVNode } from 'vue';
  import axios from 'axios';
  import {message, Modal} from 'ant-design-vue';
  import {Tool} from "@/util/tool";
  import {useRoute} from "vue-router";
  import ExclamationCircleOutlined from "@ant-design/icons-vue/ExclamationCircleOutlined";
  import E from 'wangeditor'

  export default defineComponent({
    name: 'AdminDoc',
    setup() {
      const route = useRoute()
      console.log("路由：", route);
      console.log("route.path：", route.path);
      console.log("route.query：", route.query);
      console.log("route.param：", route.params);
      console.log("route.fullPath：", route.fullPath);
      console.log("route.name：", route.name);
      console.log("route.meta：", route.meta);
      const param = ref();
      param.value = {};
      const docs = ref();

      const loading = ref(false);

      // 因为树选择组件的属性状态，会随当前编辑的节点而变化，所以单独声明一个响应式变量
      const treeSelectData = ref();
      treeSelectData.value = [];

      const columns = [
        {
          title: '名称',
          dataIndex: 'name',
          slots: { customRender: 'name' }
        },
        {
          title: 'Action',
          key: 'action',
          slots: { customRender: 'action' }
        }
      ];
      /**
       * 一级分类树，children属性就是二级分类
       * [{
       *   id: "",
       *   name: "",
       *   children: [{
       *     id: "",
       *     name: "",
       *   }]
       * }]
       */
      const level1 = ref(); // 一级分类树，children属性就是二级分类
      level1.value = [];
      /**
       * 数据查询
       **/
      const handleQuery = () => {
        loading.value = true;
        axios.get("/doc/all/" + route.query.ebookId).then((response) =>{
          loading.value = false;
          const data = response.data;
          if(data.success){
            docs.value = data.content;
            console.log("原始数组：", docs.value);

            level1.value = [];
            level1.value = Tool.array2Tree(docs.value, 0);
            console.log("树形结构：", level1);

            // 父文档下拉框初始化，相当于点击新增
            treeSelectData.value = Tool.copy(level1.value) || [];
            // 为选择树添加一个"无"
            treeSelectData.value.unshift({id: 0, name: '无'});
          }else {
            message.error(data.message)
          }
        });
      };


      const doc = ref();
      doc.value = {
        ebookId : route.query.ebookId
      };
      const modalVisible = ref(false);
      const modalLoading = ref(false);
      const editor = new E('#content');
      editor.config.zIndex = 0;

      const handleModalOk = () => {
        modalLoading.value = false;
        doc.value.content = editor.txt.html();
        axios.post("/doc/save",doc.value).then((response) =>{
          const data = response.data;
          if (data.success){
            // modalVisible.value = false;
            message.success("保存成功");
            //重新加载列表
            handleQuery();
          }else {
            message.error(data.message);
          }
        });
      };

      //内容查询
      const handleQueryContent = () => {
        axios.get("/doc/findContent/"+doc.value.id).then((response) =>{
          loading.value = false;
          const data = response.data;
          if(data.success){
            editor.txt.html(data.content);
          }else {
            message.error(data.message)
          }
        });
      };

      const edit = (record: any) => {
        editor.txt.html("");
        modalVisible.value = true;
        doc.value = Tool.copy(record);
        handleQueryContent();

        // 不能选择当前节点及其所有子孙节点，作为父节点，会使树断开
        treeSelectData.value = Tool.copy(level1.value);
        setDisable(treeSelectData.value, record.id);

        // 为选择树添加一个"无"unshift往数组前面添加一个元素，push是往数组后面添加一个元素
        treeSelectData.value.unshift({id: 0, name: '无'});

        // setTimeout(function () {
        //   editor.create();
        // },100)
      };

      const add = () => {
        editor.txt.html("");
        modalVisible.value = true;
        doc.value = {
          ebookId : route.query.ebookId
        };

        treeSelectData.value = Tool.copy(level1.value);

        // 为选择树添加一个"无"
        treeSelectData.value.unshift({id: 0, name: '无'});
        // setTimeout(function () {
        //   editor.create();
        // },100)

      };



      /**
       * 将某节点及其子孙节点全部置为disabled
       */
      const setDisable = (treeSelectData: any, id: any) => {
        // console.log(treeSelectData, id);
        // 遍历数组，即遍历某一层节点
        for (let i = 0; i < treeSelectData.length; i++) {
          const node = treeSelectData[i];
          if (node.id === id) {
            // 如果当前节点就是目标节点
            console.log("disabled", node);
            // 将目标节点设置为disabled
            node.disabled = true;

            // 遍历所有子节点，将所有子节点全部都加上disabled
            const children = node.children;
            if (Tool.isNotEmpty(children)) {
              for (let j = 0; j < children.length; j++) {
                setDisable(children, children[j].id)
              }
            }
          } else {
            // 如果当前节点不是目标节点，则到其子节点再找找看。
            const children = node.children;
            if (Tool.isNotEmpty(children)) {
              setDisable(children, id);
            }
          }
        }
      };

      const deleteIds: Array<string> = [];
      const deleteNames: Array<string> = [];
     const getDeleteIds = (treeSelectData: any, id: any) => {
        // console.log(treeSelectData, id);
        // 遍历数组，即遍历某一层节点
        for (let i = 0; i < treeSelectData.length; i++) {
          const node = treeSelectData[i];
          if (node.id === id) {
            // 如果当前节点就是目标节点
            console.log("delete", node);
            // 将目标id放入结果集ids
            // node.disabled = true;
            deleteIds.push(id);
            deleteNames.push(node.name);

            // 遍历所有子节点
            const children = node.children;
            if (Tool.isNotEmpty(children)) {
              for (let j = 0; j < children.length; j++) {
                getDeleteIds(children, children[j].id)
              }
            }
          } else {
            // 如果当前节点不是目标节点，则到其子节点再找找看。
            const children = node.children;
            if (Tool.isNotEmpty(children)) {
              getDeleteIds(children, id);
            }
          }
        }
      };

      const handleDelete = (id: number) => {
        console.log(level1.value,id)
        deleteIds .length = 0;
        deleteNames.length = 0;
        getDeleteIds(level1.value,id);
        Modal.confirm({
          title: '重要提醒',
          icon: createVNode(ExclamationCircleOutlined),
          content: '将删除：【' + deleteNames.join("，") + "】删除后不可恢复，确认删除？",
          onOk() {
            // console.log(ids)
            axios.delete("/doc/delete/" + deleteIds.join(",")).then((response) => {
              const data = response.data; // data = commonResp
              if (data.success) {
                // 重新加载列表
                handleQuery();
              } else {
                message.error(data.message);
              }
            });
          },
        });
      };

      // ----------------富文本预览--------------
      const drawerVisible = ref(false);
      const previewHtml = ref();
      const handlePreviewContent = () => {
        const html = editor.txt.html();
        previewHtml.value = html;
        drawerVisible.value = true;
      };
      const onDrawerClose = () => {
        drawerVisible.value = false;
      };

      /**
       * 表格点击页码时触发
       */

      onMounted(() => {
        handleQuery();
        editor.create();
      });
      return {
        level1,
        param,
        // docs,
        columns,
        loading,
        handleQuery,

        add,
        edit,
        handleDelete,

        modalVisible,
        modalLoading,
        handleModalOk,
        doc,
        treeSelectData,
        drawerVisible,
        previewHtml,
        handlePreviewContent,
        onDrawerClose,
      }
    }
  });
</script>