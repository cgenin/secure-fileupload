<template>
  <div class="secure-fileupload-container">
    <buttons :show="hasFile"
             :disabled="disabled || loading"
             @onDeleteAll="onDeleteAll"
             @onSend="onSend"
    />
    <progress-bar :show="loading || result" :value="progress"></progress-bar>
    <file-upload v-if="!result"
                 :accept="extensions"
                 :multiple="multiple"
                 :disabled="disabled"
                 v-bind="labels"
                 @change="onChangeFile"
                 @setDeleteAll="onInitDeleteAll"
    />
    <result-panel v-if="result" :success="result.success" @click="onFinalize"/>
  </div>
</template>
<script>
import FileUpload from './Fileupload';
import Buttons from './Buttons';
import ProgressBar from './ProgressBar';
import ResultPanel from './ResultPanel';
import axios from 'axios';
import { createEndPoint } from './utils';

export default {
  name: 'SecureFileUpload',
  props: {
    multiple: {
      type: Boolean,
      default: false,
    },
    disabled: {
      type: Boolean,
      default: false,
    },
    url: {
      type: String,
      default: ''
    },
    token: {
      type: String,
      default: ''
    },
    accept: {
      type: String,
      required: true
    },
    labels: {
      type: Object,
      default: () => ({})
    }
  },
  components: { ResultPanel, Buttons, FileUpload, ProgressBar },
  data() {
    return {
      loading: false,
      files: [],
      result: null,
      deleteAll: null,
      progress: 0,
    };
  },
  computed: {
    hasFile() {
      const { files } = this;
      return files && files.length > 0;
    },
    extensions() {
      if (!this.accept) {
        return [];
      }
      return this.accept.split(',');
    }
  },
  methods: {
    onChangeFile(files) {
      this.files = files;
    },
    onInitDeleteAll(fn) {
      this.deleteAll = fn;
    },
    onDeleteAll() {
      this.deleteAll();
    },
    uploadProgress(progressEvent) {
      this.progress = Math.round((progressEvent.loaded * 100) / progressEvent.total);
    },
    async onSend() {
      try {
        const { loading, disabled, files, uploadProgress, url, token } = this;
        if (loading || disabled) {
          return;
        }
        this.loading = true;
        this.progress = 0;
        const endPoint = createEndPoint(url, token);

        const onUploadProgress = uploadProgress.bind(this);
        const config = {
          onUploadProgress,
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        };
        let data = new FormData()
        for (const f in files) {
          data.append('file', f);
        }
        await axios.post(endPoint, data, config);
        this.result = { success: true };
        this.deleteAll();
        this.$emit('onSuccess', this.result);

      } catch (e) {
        console.error(e);
        this.result = { success: false };
        this.deleteAll();
        this.$emit('onError', this.result);
      } finally {
        this.loading = false;
      }
    },
    onFinalize(state = {}) {
      this.$emit('onClose', state);
      this.result = null;
    }
  }

}
</script>
<style scoped>
.secure-fileupload-container {
  --sfc-danger-color: #db4437;
  --sfc-success-color: #7dc21e;
  --sfc-color: #0076be;
  --sfc-text-color: #757573;
  --sfc-progress-color: #393f9f;
  --sfc-background-color: #fff;
  --sfc-border-color: #efeeff;
  --sfc-max-width: 360px;

  display: flex;
  flex-direction: column;
  align-items: center;

  width: 100%;
  max-width: var(--sfc-max-width);
}
</style>