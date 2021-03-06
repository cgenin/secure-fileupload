<template>
  <div class="uploader"
       :class="{disabled, skeleton:loading}">
    <label class="file-drag"
           :class="{hover: dragover}"
           @dragover="fileDragHover"
           @dragleave="fileDragHover"
           @drop="fileSelectHandler"
    >
      <input ref="input"
             type="file"
             name="fileUpload"
             :accept="acceptStr"
             @change="fileSelectHandler"
             :multiple="multiple"
             :disabled="disabled"
      />

      <div v-if="displayUpload" class="start">
        <i class="icon-download" :title="description" aria-hidden="true"></i>
        <div>{{ description }}</div>
        <span class="file-upload-btn btn btn-primary">
            {{ buttonLabel }}
          </span>
      </div>
      <div class="previews">
        <fileupload-preview :key="'key-'+index+'-'+f.name"
                            :file="f"
                            :extensions="extensions"
                            :disabled="disabled"
                            v-for="(f, index) in files"
                            @onDelete="deleteOneFilehandler"
        />
      </div>
    </label>
  </div>
</template>
<script>
import { accept, convert2Extensions, isXhr2, uuidv4 } from './utils';
import FileuploadPreview from './FileuploadPreview';

export default {
  name: 'file-upload',
  props: {
    multiple: {
      type: Boolean,
      default: false,
    },
    loading: {
      type: Boolean,
      default: false,
    },
    description: {
      type: String,
      default: 'Sélectionner un fichier ou effectuer un glisser-déposer'
    },
    buttonLabel: {
      type: String,
      default: 'Sélectionner un fichier'
    },
    disabled: {
      type: Boolean,
      default: false
    },
    accept: {
      type: Array,
      required: true
    }
  },
  components: { FileuploadPreview },
  data() {
    return {
      inputName: 'file-upload',
      isXhr2: false,
      dragover: false,
      files: [],
    };
  },
  mounted() {
    this.isXhr2 = isXhr2();
    this.$emit('setDeleteAll', this.deleteAllFiles);
  },
  created() {
    this.inputName = uuidv4();
  },
  computed: {
    displayUpload() {
      return this.files.length === 0;
    },
    extensions() {
      return convert2Extensions(this.accept);
    },
    acceptStr() {
      return accept(this.extensions);
    }
  },
  methods: {
    fileSelectHandler(e) {
      if (this.disabled || this.loading) {
        return;
      }

      const filesList = e.target.files || e.dataTransfer.files;
      this.fileDragHover(e);
      this.files = [...filesList];
      this.$emit('onSelectFile', { files: this.files });
      this.onChange();
    },
    fileDragHover(e) {
      if (e) {
        e.stopPropagation();
        e.preventDefault();
        if (!this.isXhr2) {
          return;
        }
        this.dragover = e.type === 'dragover';
      }
    },
    deleteOneFilehandler(file2Delete) {
      if (this.disabled || this.loading) {
        return;
      }
      if (this.files.length === 1) {
        this.files = [];
        this.onChange();
        return;
      }
      const tmp = this.files.filter(f => f !== file2Delete);
      this.files = [...tmp];
      this.onChange();
    },
    deleteAllFiles() {
      if (this.disabled) {
        return;
      }
      this.files = [];
      this.onChange();
    },
    onChange() {
      this.$emit('change', this.files);
    }

  }
}
</script>
<style scoped>
@import url("../assets/skeleton.css");
@import "../assets/disabled.css";

.uploader {
  --uploader-danger-color: var(--sfc-danger-color, #d44034);
  --uploader-color: var(--sfc-color, #454cad);
  --uploader-background-color: var(--sfc-background-color, #fff);
  --uploader-border-color: var(--sfc-border-color, #eeeeee);
  --uploader-text-color: var(--sfc-text-color, #5f6982);
}

.uploader {
  display: block;
  clear: both;
  margin: 0 auto;
  width: 100%;
  /*max-width: 600px;*/
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
  font-size: 15px;
  line-height: 1.46667;
  letter-spacing: 0.1px;
  color: rgb(72, 72, 72);
  background-color: var(--uploader-background-color);
}

.uploader label {
  float: left;
  clear: both;
  width: 100%;
  padding: .5rem 1.5rem;
  text-align: center;
  background: var(--uploader-background-color);
  border-radius: 7px;
  border: 3px solid var(--uploader-border-color);
  -webkit-transition: all .2s ease;
  transition: all .2s ease;
  -webkit-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
  user-select: none;
}

.uploader label:hover {
  border-color: var(--uploader-color);
}

.uploader label.hover {
  border: 3px solid var(--uploader-color);
  box-shadow: inset 0 0 0 6px var(--uploader-border-color);
}


.uploader .start {
  float: left;
  clear: both;
  width: 100%;
}

.uploader .start.hidden {
  display: none;
}

.uploader .start i.icon-download {
  display: block;
  text-indent: -9999px;
  width: 50px;
  height: 50px;
  -webkit-transition: all .2s ease-in-out;
  transition: all .2s ease-in-out;
  background: url("../assets/download-icon.svg");
  background-size: 50px 50px;
  margin: auto auto 1rem;
  -webkit-transform: scale(0.8);
  transform: scale(0.8);
  opacity: 0.3;


}


.uploader input[type="file"] {

  width: 100% !important;
  display: block !important;
  position: fixed !important;
  left: -9999px !important;
  padding: 8px 10px !important;
  transition: background 300ms ease 0s, border 300ms ease 0s, color 300ms ease 0s !important;
  border-width: 2px !important;
  border-style: solid !important;
  border-color: var(--uploader-border-color);
  border-radius: 4px !important;
}

.uploader div {
  margin: 0 0 .5rem 0;
  color: var(--uploader-text-color);
}

.btn {
  display: inline-block;
  margin: .5rem .5rem 1rem .5rem;
  clear: both;
  font-family: inherit;
  font-weight: 700;
  font-size: 14px;
  text-decoration: none;
  text-transform: initial;
  border-radius: .2rem;
  outline: none;
  padding: 0 1rem;
  height: 36px;
  line-height: 36px;
  color: var(--uploader-background-color);
  -webkit-transition: all 0.2s ease-in-out;
  transition: all 0.2s ease-in-out;
  box-sizing: border-box;
  background: var(--uploader-color);
  border-color: var(--uploader-color);
  cursor: pointer;
}

</style>