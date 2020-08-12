<template>
  <div class="uploader"
       :class="{'full-width': fullWidth}">
    <div v-if="files.length>0" class="buttons">
      <button class="outline danger-white" @click="deleteAllFiles">Tout effacer</button>
      <button class="outline green-white">Envoyer</button>
    </div>
    <label class="file-drag"
           :class="{hover:dragover,'modal-body file-upload':!dragover }"
           @dragover="fileDragHover"
           @dragleave="fileDragHover"
           @drop="fileSelectHandler"
    >
      <input ref="input"
             type="file"
             name="fileUpload"
             accept="image/*"
             @change="fileSelectHandler"
             :multiple="multiple"
      />

      <div v-if="displayUpload" class="start">
        <i class="icon-download" :title="description" aria-hidden="true"></i>
        <div>{{ description }}</div>
        <div class="notimage hidden">Please select an image</div>
        <span class="file-upload-btn btn btn-primary">
            {{ buttonLabel }}
          </span>
      </div>
      <div class="previews">
        <fileupload-preview :key="'key-'+index+'-'+f.name"
                            :file="f"
                            v-for="(f, index) in files"
                            @onDelete="deleteOneFilehandler"
        />
      </div>
      <progress class="progress hidden" id="file-progress" value="0">
        <span>0</span>%
      </progress>
    </label>
  </div>
</template>
<script>
import { isXhr2, uuidv4 } from './utils';
import FileuploadPreview from './FileuploadPreview';

export default {
  name: 'file-upload',
  props: {
    multiple: {
      type: Boolean,
      default: false,
    },
    fullWidth: {
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
  },
  created() {
    this.inputName = uuidv4();
  },
  computed: {
    displayUpload() {
      return this.multiple || this.files.length === 0;
    }
  },
  methods: {
    fileSelectHandler(e) {
      const filesList = e.target.files || e.dataTransfer.files;
      this.fileDragHover(e);
      this.files = [...filesList];
      this.$emit('onSelectFile', { files: this.files });

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
      if (this.files.length === 1) {
        this.files = [];
        return;
      }
      const tmp = this.files.filter(f => f !== file2Delete);
      this.files = [...tmp];
    },
    deleteAllFiles() {
      this.files = [];
    }

  }
}
</script>
<style scoped>

.uploader {
  --uploader-danger-color: #db4437;
  --uploader-success-color: #7dc21e;
  --uploader-color: #454cad;
  --uploader-progress-color: #393f90;
  --uploader-background-color: #fff;
  --uploader-border-color: #eeeeee;
  --uploader-text-color: #5f6982;

}

.uploader {
  display: block;
  clear: both;
  margin: 0 auto;
  width: 100%;
  max-width: 600px;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
  font-size: 15px;
  line-height: 1.46667;
  letter-spacing: 0.1px;
  color: rgb(72, 72, 72);
  background-color: var(--uploader-background-color);
}

.uploader.full-width {
  max-width: 100%;
}

.uploader label {
  float: left;
  clear: both;
  width: 100%;
  padding: 2rem 1.5rem;
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


.uploader .notimage {
  display: block;
  float: left;
  clear: both;
  width: 100%;
}

.uploader .notimage.hidden {
  display: none;
}

.uploader progress,
.uploader .progress {
  display: inline;
  clear: both;
  margin: 0 auto;
  width: 100%;
  max-width: 180px;
  height: 8px;
  border: 0;
  border-radius: 4px;
  background-color: var(--uploader-border-color);
  overflow: hidden;
}

.uploader .progress[value]::-webkit-progress-bar {
  border-radius: 4px;
  background-color: var(--uploader-border-color);
}

.uploader .progress[value]::-webkit-progress-value {
  background: -webkit-gradient(linear, left top, right top, from(var(--uploader-progress-color)), color-stop(50%, var(--uploader-color)));
  background: linear-gradient(to right, var(--uploader-progress-color) 0%, var(--uploader-color) 50%);
  border-radius: 4px;
}

.uploader .progress[value]::-moz-progress-bar {
  background: linear-gradient(to right, var(--uploader-progress-color) 0%, var(--uploader-color) 50%);
  border-radius: 4px;
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

.uploader .buttons {
  display: flex;
  justify-content: space-around;
}

button {
  cursor: pointer;
  outline: none;
}

button.outline {
  position: relative;
  z-index: 3;
  background: transparent;
  color: var(--uploader-color);
  font-size: 14px;
  border-color: var(--uploader-color);
  border-style: solid;
  border-width: 2px;
  border-radius: 22px;
  padding: 10px 40px;
  text-transform: uppercase;
  transition: all 0.2s linear;
}

button.outline a {
  text-decoration: none;
}

button.outline:hover {
  color: var(--uploader-background-color);
  background: var(--uploader-color);
  border-color: var(--uploader-background-color);
  transition: all 0.2s linear;
}

button.outline:active {
  border-radius: 22px;
}

button.green-white {
  font-weight: 700;
  color: var(--uploader-success-color);
  border-color: var(--uploader-success-color);
  background: transparent;
}

button.green-white:hover {
  color: var(--uploader-background-color);
  background: var(--uploader-success-color);
  border-color: var(--uploader-success-color);
}

button.danger-white {
  font-weight: 700;
  color: var(--uploader-danger-color);
  border-color: var(--uploader-danger-color);
  background: transparent;
}

button.danger-white:hover {
  color: var(--uploader-background-color);
  background: var(--uploader-danger-color);
  border-color: var(--uploader-danger-color);
}


</style>