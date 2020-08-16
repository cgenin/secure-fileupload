<template>
  <div class="progress-container">
    <progress class="progress" v-if="show" :value="value">
      <span>0</span>%
    </progress>
  </div>
</template>
<script>
export default {
  name: 'ProgressBar',
  props: {
    show: {
      type: Boolean,
      default: true,
    },
    value: {
      type: Number,
    }

  }
}
</script>
<style scoped>

.progress-container {
  --progress-color: var(--sfc-color, #454cad);
  --progress-progress-color: var(--sfc-progress-color, #393f90);
  --progress-background-color: var(--sfc-background-color, #fff);
  --progress-border-color: var(--sfc-border-color, #eeeeee);
  --progress-min-width: var(--sfc-max-width, 180px);

  display: flex;
  justify-content: center;
  width: 100%;
}

.progress-container progress {
  display: inline;
  clear: both;
  margin: 0 auto;
  width: 100%;
  max-width: var(--progress-min-width);
  height: 8px;
  border: 0;
  border-radius: 4px;
  background-color: var(--progress-border-color);
  overflow: hidden;

}

.progress-container .progress[value]::-webkit-progress-bar {
  border-radius: 4px;
  background-color: var(--progress-border-color);
}

.progress-container .progress[value]::-webkit-progress-value {
  background: -webkit-gradient(linear, left top, right top, from(var(--progress-progress-color)), color-stop(50%, var(--progress-color)));
  background: linear-gradient(to right, var(--progress-progress-color) 0%, var(--progress-color) 50%);
  border-radius: 4px;
}

.progress-container .progress[value]::-moz-progress-bar {
  background: linear-gradient(to right, var(--progress-progress-color) 0%, var(--progress-color) 50%);
  border-radius: 4px;
}
</style>