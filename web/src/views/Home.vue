<template>
  <a-layout>
    <a-layout-sider width="200" style="background: #fff">
      <a-menu
              v-model:selectedKeys="selectedKeys2"
              v-model:openKeys="openKeys"
              mode="inline"
              :style="{ height: '100%', borderRight: 0 }"
              @click="handleClick"
      >
        <a-menu-item key="welcome">
          <MailOutlined />
          <span>欢迎</span>
        </a-menu-item>
        <a-sub-menu v-for="item in level1" :key="item.id">
          <template v-slot:title>
            <span><user-outlined />{{item.name}}</span>
          </template>
          <a-menu-item v-for="child in item.children" :key="child.id">
            <MailOutlined /><span>{{child.name}}</span>
          </a-menu-item>
        </a-sub-menu>
        <a-menu-item key="tip" :disabled="true">
          <span>以上菜单在分类管理配置</span>
        </a-menu-item>
      </a-menu>
    </a-layout-sider>
    <a-layout-content
            :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <div class="welcome" v-show="isShowWelcome">
        <TheWelcome></TheWelcome>
      </div>
      <a-list v-show="!isShowWelcome" item-layout="vertical" size="large" :grid="{ gutter: 16, column: 3 }" :pagination="pagination" :data-source="ebooks">

        <template #renderItem="{ item }">
          <a-list-item key="item.name">
            <template #actions>
              <span>
              <component v-bind:is="'FileOutlined'" style="margin-right: 8px" />
              {{ item.docCount }}
              </span>
              <span>
                <component v-bind:is="'UserOutlined'" style="margin-right: 8px" />
                {{ item.viewCount }}
              </span>
              <span>
                <component v-bind:is="'LikeOutlined'" style="margin-right: 8px" />
                {{ item.voteCount }}
              </span>
            </template>

            <a-list-item-meta :description="item.description">
              <template #title>
<!--                <a :href="item.href">{{ item.name }}</a>-->
                <router-link :to="'/doc?ebookId='+item.id">
                  {{item.name}}
                </router-link>
              </template>
              <template #avatar><a-avatar :src="item.cover" /></template>
            </a-list-item-meta>
          </a-list-item>
        </template>
      </a-list>
    </a-layout-content>
  </a-layout>
</template>

<script lang="ts">
import { defineComponent,onMounted,ref,reactive,toRef } from 'vue';
import axios from "axios";
import { message } from 'ant-design-vue';
import {Tool} from "@/util/tool";
import TheWelcome from '@/components/the-welcome.vue';

const listData : any = [];

for (let i = 0; i < 23; i++) {
  listData.push({
    href: 'https://www.antdv.com/',
    title: `ant design vue part ${i}`,
    avatar: 'https://joeschmoe.io/api/v1/random',
    description: 'Ant Design, a design language for background applications, is refined by Ant UED Team.',
    content: 'We supply a series of design principles, practical patterns and high quality design resources (Sketch and Axure), to help people create their product prototypes beautifully and efficiently.',
  });
}

export default defineComponent({
  name: 'Home',
  components: {
    TheWelcome
  },
  setup(){
    console.log("setup");
    const ebooks = ref();
    const ebooks1 = reactive({books:[]});

    // const openKeys =  ref();
    const level1 =  ref();
    let categorys: any;
    /**
     * 查询所有分类
     **/
    const handleQueryCategory = () => {
      axios.get("/category/all").then((response) => {
        const data = response.data;
        if (data.success) {
          categorys = data.content;
          console.log("原始数组：", categorys);

          // 加载完分类后，将侧边栏全部展开
          // openKeys.value = [];
          // for (let i = 0; i < categorys.length; i++) {
          //   openKeys.value.push(categorys[i].id)
          // }

          level1.value = [];
          level1.value = Tool.array2Tree(categorys, 0);
          console.log("树形结构：", level1.value);
        } else {
          message.error(data.message);
        }
      });
    };

    const isShowWelcome = ref(true);
    let categoryIdd2 = 0;

    const handleQueryEbook = () =>{
      console.log("onMounted");
      axios.get("/ebook/selectEbook",{
        params:{
          page: 1,
          size: 1000,
          categoryIdd2: categoryIdd2
        }
      }).then((response)=>{
        const data = response.data;
        ebooks.value = data.content.list
        ebooks1.books = data.content
        // console.log(response)
      });
    }

    const handleClick = (value: any) =>{
      console.log("menu click",value)
      if (value.key === 'welcome'){
        isShowWelcome.value = true;
      }else {
        categoryIdd2 = value.key;
        isShowWelcome.value = false;
        handleQueryEbook();
      }
      //上面是正常用法，下面是简化用法alt加entrr生成的
      // isShowWelcome.value = value.key === 'welcome';
    };



    onMounted(()=>{
      handleQueryCategory();
      // handleQueryEbook();
    });
    return {
      ebooks,
      ebooks2: toRef(ebooks1,"books"),
      listData,
       pagination : {
        onChange: (page: any) => {
          console.log(page);
        },
        pageSize: 3,
      },
      level1,
      isShowWelcome,
      handleClick
    //  actions : [{
    //   type: 'StarOutlined',
    //   text: '156',
    // }, {
    //   type: 'LikeOutlined',
    //   text: '156',
    // }, {
    //   type: 'MessageOutlined',
    //   text: '2',
    // }],
    }
  }
});
</script>

<style scoped>
  .ant-avatar{
    width: 50px;
    height: 50px;
    line-height: 50px;
    border-radius: 8%;
    margin: 5px 0;
  }
</style>