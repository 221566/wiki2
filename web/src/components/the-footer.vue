<template>
    <a-layout-footer style="text-align: center">
        java电子书<span v-show="user.id">,欢迎：{{user.name}}</span>
    </a-layout-footer>
</template>

<script lang="ts">
    import { defineComponent,computed } from 'vue';
    import store from "@/store";

    export default defineComponent({
        name: 'the-footer',
        setup(){
            const user = computed(() => store.state.user)

            return{
                user
            }
        }
        //props用于父子组件之间传递数据
        // props: {
        //     msg: String,
        // },
    });
</script>