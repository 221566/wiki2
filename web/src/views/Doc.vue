<template>
    <a-layout>
        <a-layout-content :style="{background: '#fff',padding: '24px',margin: 0,minHeight: '280'}">
           <h3 v-if = "level1.length === 0">对不起，找不到相关文档</h3>
            <a-row>
                <a-col :span="6">
                   <a-tree
                       v-if="level1.length > 0"
                       :tree-data="level1"
                       @select="onSelect"
                       :replaceFields="{title: 'name',key: 'id', value: 'id'}"
                       :defaultExpandAll="true"
                       :defaultSelectedKeys="defaultSelectedKeys"
                   >
                   </a-tree>
               </a-col>
               <a-col :span="18">
                   <div>
                       <h2>{{doc.name}}</h2>
                       <div>
                           <span>阅读数:{{doc.viewCount}}</span>&nbsp;&nbsp;
                           <span>点赞数:{{doc.voteCount}}</span>
                       </div>
                   </div>
                    <div :innerHTML="html"></div>
               </a-col>
           </a-row>
        </a-layout-content>
    </a-layout>
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
            const docs = ref();
            const html = ref();
            //初始化定义，初始的时候放一个空
            const defaultSelectedKeys = ref();
            defaultSelectedKeys.value = [];
            const doc = ref();
            doc.value = {};

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

            //内容查询
            const handleQueryContent = (id: number) => {
                axios.get("/doc/findContent/"+id).then((response) =>{
                    const data = response.data;
                    if(data.success){
                        html.value = data.content
                    }else {
                        message.error(data.message)
                    }
                });
            };

            /**
             * 数据查询
             **/
            const handleQuery = () => {
                axios.get("/doc/all/"+route.query.ebookId).then((response) =>{
                    const data = response.data;
                    if(data.success){
                        docs.value = data.content;

                        level1.value = [];
                        level1.value = Tool.array2Tree(docs.value, 0);
                        //判断这个电子书舒服有内容，有的话在去查询
                        if (Tool.isNotEmpty(level1)) {
                            //把这个节点设置成选择状态
                            defaultSelectedKeys.value = [level1.value[0].id];
                            //根据这个节点去查内容
                            handleQueryContent(level1.value[0].id);
                            //初始显示文档信息
                            doc.value = level1.value[0];
                        }
                    }else {
                        message.error(data.message)
                    }
                });
            };

            const onSelect = (selectedKeys: any, info: any) => {
                console.log('selected', selectedKeys, info);
                if (Tool.isNotEmpty(selectedKeys)) {
                    // 选中某一节点时，加载该节点的文档信息.props对应的一行数据
                    doc.value = info.selectedNodes[0].props;
                    // 加载内容
                    handleQueryContent(selectedKeys[0]);
                }
            };

            /**
             * 表格点击页码时触发
             */

            onMounted(() => {
                handleQuery();
            });
            return {
                level1,
                html,
                onSelect,
                defaultSelectedKeys,
                doc
                // handleQuery,
            }
        }
    });
</script>