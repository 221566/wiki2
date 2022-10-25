<template>
    <div>

        <a-row>
            <a-col :span="24">
                <a-card>
                    <a-row>
                        <a-col :span="8">
                            <a-statistic title="总阅读量" :value="statistic.viewCount">
                                <template #suffix>
                                    <UserOutlined />
                                </template>
                            </a-statistic>
                        </a-col>
                        <a-col :span="8">
                            <a-statistic title="总点赞量" :value="statistic.voteCount">
                                <template #suffix>
                                    <like-outlined />
                                </template>
                            </a-statistic>
                        </a-col>
                        <a-col :span="8">
                            <a-statistic title="点赞率" :value="statistic.voteCount / statistic.viewCount * 100"
                                         :precision="2"
                                         suffix="%"
                                         :value-style="{ color: '#cf1322' }">
                                <template #suffix>
                                    <like-outlined />
                                </template>
                            </a-statistic>
                        </a-col>
                    </a-row>
                </a-card>
            </a-col>
        </a-row>
        <br>
        <a-row :gutter="16">
            <a-col :span="12">
                <a-card>
                    <a-row>
                        <a-col :span="12">
                            <a-statistic title="今日阅读" :value="statistic.todayViewCount" style="margin-right: 50px">
                                <template #suffix>
                                    <UserOutlined />
                                </template>
                            </a-statistic>
                        </a-col>
                        <a-col :span="12">
                            <a-statistic title="今日点赞" :value="statistic.todayVoteCount">
                                <template #suffix>
                                    <like-outlined />
                                </template>
                            </a-statistic>
                        </a-col>
                    </a-row>
                </a-card>
            </a-col>
            <a-col :span="12">
                <a-card>
                    <a-row>
                        <a-col :span="12">
                            <a-statistic
                                    title="预计今日阅读"
                                    :value="statistic.todayViewIncrease"
                                    :value-style="{ color: '#0000ff' }"
                            >
                                <template #suffix>
                                    <UserOutlined />
                                </template>
                            </a-statistic>
                        </a-col>
                        <a-col :span="12">
                            <a-statistic
                                    title="预计今日阅读增长"
                                    :value="statistic.todayViewIncreaseRateAbs"
                                    :precision="2"
                                    suffix="%"
                                    class="demo-class"
                                    :value-style="statistic.todayViewIncreaseRate < 0 ? { color: '#3f8600' } : { color: '#cf1322' }"
                            >
                                <template #prefix>
                                    <arrow-down-outlined v-if="statistic.todayViewIncreaseRate < 0"/>
                                    <arrow-up-outlined v-if="statistic.todayViewIncreaseRate >= 0"/>
                                </template>
                            </a-statistic>
                        </a-col>
                    </a-row>
                </a-card>
            </a-col>
        </a-row>

    </div>
</template>

<script lang="ts">
    import {defineComponent,ref, onMounted} from 'vue';
    import axios from 'axios';

    export default defineComponent({
        name: 'the-welcome',

        setup() {
            const statistic = ref();
            statistic.value = {};
            const getStatistic = () => {
                axios.get('/EbookSnapshot/getStatistic').then((response) => {
                    /**
                     * 取到值后赋给statisticResp这个变量，这个变量是个数值接口返回的数据是昨天和今天所以这是个数值
                     * 接口返回数组[昨天，今天]所以昨天是第0条今天是第一条
                     */
                    const data = response.data;
                    if (data.success) {
                        const statisticResp = data.content;
                        statistic.value.viewCount = statisticResp[1].viewCount;
                        statistic.value.voteCount = statisticResp[1].voteCount;
                        statistic.value.todayViewCount = statisticResp[1].viewIncrease;
                        statistic.value.todayVoteCount = statisticResp[1].voteIncrease;

                        // 按分钟计算当前时间点，占一天的百分比
                        /**
                         * （now.getHours()获取当前小时换算成分钟的话就是*60，当前时间点对应的分钟now.getMinutes()）一天过去了
                         * 多少分钟 /（60*24）一天有多少分钟
                         * 这样就算出了一天过去了百分之几
                         */
                        const now = new Date();
                        const nowRate = (now.getHours() * 60 + now.getMinutes()) / (60 * 24);
                        // console.log(nowRate)
                        statistic.value.todayViewIncrease = parseInt(String(statisticResp[1].viewIncrease / nowRate));
                        // todayViewIncreaseRate：今日预计增长率=（今天预计-昨天）/昨天*100
                        statistic.value.todayViewIncreaseRate = (statistic.value.todayViewIncrease - statisticResp[0].viewIncrease) / statisticResp[0].viewIncrease * 100;
                        //因为这样减的话有可能是正直也有可能是负值，所以这里要加一个绝对值Math.abs（）通过这个函数对刚才得到的数值做绝对值计算
                        statistic.value.todayViewIncreaseRateAbs = Math.abs(statistic.value.todayViewIncreaseRate);
                    }
                });
            };

            onMounted(() =>{
                getStatistic();
            });

            return {

            }
        }
    });
</script>
