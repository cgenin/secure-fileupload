<template>
  <div id="app">
    <secure-file-upload :accept="['JPG','PNG']"
                        token="700ceaa4-457c-4cee-b6f1-a5d6f1ffbf43"></secure-file-upload>
  </div>
</template>

<script>
import SecureFileUpload from './components/SecureFileUpload'

export default {
  name: 'App',
  components: {
    SecureFileUpload
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
  display: flex;
  justify-content: center;
}
</style>
