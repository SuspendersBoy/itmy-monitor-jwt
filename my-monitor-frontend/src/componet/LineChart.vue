<script setup>
  import { ref, onMounted, onBeforeUnmount, watch } from 'vue';
  import * as echarts from 'echarts';

  const chartRef = ref(null);
  let chartInstance = null;

  // 接收外部传入的数据
  const props = defineProps({
    data: []
  });

  // 格式化数据
  const formatChartData = () => {
    return {
      xAxisData: props.data.map(item => item.data),
      seriesData: props.data.map(item => item.cpuUsage)
    };
  };

  // 初始化图表
  const initChart = () => {
    if (!chartRef.value) return;
    chartInstance = echarts.init(chartRef.value);
    // 图表配置
    const chartData = formatChartData();
    const option = {
      title: {
        text: "cpu历史使用率",
        left: 'center'
      },
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'cross',
          crossStyle: {
            color: '#9c2727'
          }
        }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: chartData.xAxisData
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          name: '数据值',
          type: 'line',
          data: chartData.seriesData,
          smooth: true,
          areaStyle: {}
        }
      ]
    };

    chartInstance.setOption(option);

    // 监听窗口大小变化，调整图表
    window.addEventListener('resize', handleResize);
  };

  // 处理窗口大小变化
  const handleResize = () => {
    chartInstance?.resize();
  };

  // 更新图表数据
  const updateChart = () => {
    if (!chartInstance) return;

    const chartData = formatChartData();
    chartInstance.setOption({
      xAxis: {
        data: chartData.xAxisData
      },
      series: [
        {
          data: chartData.seriesData
        }
      ]
    });
  };

  // 生命周期钩子
  onMounted(() => {
    initChart();
  });

  onBeforeUnmount(() => {
    if (chartInstance) {
      window.removeEventListener('resize', handleResize);
      chartInstance.dispose();
      chartInstance = null;
    }
  });

  // 监听数据变化
  watch(() => props.data, () => {
    updateChart();
  });
</script>

<template>
  <div ref="chartRef" class="chart-container" style="width: 500px; height: 350px;"></div>
</template>

<style scoped>
</style>