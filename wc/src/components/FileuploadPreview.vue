<template>
  <div class="response">
    <div class="messages"><strong>{{ name }}</strong></div>
    <img v-if="isImage" alt="Preview" :src="imgData" class="file-image">
    <div class="buttons">
      <div class="floating-button"
           @click.prevent="onDelete"
           title="Supprimer">
        <p class="plus"></p>
      </div>
    </div>

  </div>
</template>
<script>
export default {
  name: 'FileuploadPreview',
  props: {
    file: {
      type: File,
      required: true
    }
  },
  computed: {
    name() {
      const {file} = this;
      return file.name || 'No Name';
    },
    isImage() {
      return (/\.(?=gif|jpg|png|jpeg)/gi).test(this.name);
    },
    imgData() {
      if (this.isImage && URL.createObjectURL) {
        return URL.createObjectURL(this.file);
      }
      return null;
    }
  },
  methods: {
    onDelete() {
      this.$emit('onDelete', this.file);
    }
  }

}
</script>
<style scoped>
@import url("../assets/skeleton.css");

.response {
  --upload-response-delete-width: 45px;
  float: left;
  clear: both;
  width: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}


.response .file-image {
  display: inline;
  margin: 0 auto .5rem auto;
  width: auto;
  height: auto;
  max-width: 180px;
}

.response .file-image.hidden {
  display: none;
}


.response .messages {
  margin-bottom: .5rem;
}

.buttons {
  width: 100px;
  height: 2px;
}

.floating-button {
  width: var(--upload-response-delete-width);
  height: var(--upload-response-delete-width);
  border-radius: 50%;
  background: var(--uploader-danger-color);
  margin-top: -45px;
  margin-left: 105px;
  cursor: pointer;
  box-shadow: 0 2px 5px #666;
}

.plus {
  color: white;
  top: 0;
  display: block;
  bottom: 0;
  left: 0;
  right: 0;
  text-align: center;
  padding: 0;
  margin: 0;
  line-height: var(--upload-response-delete-width);
  width: var(--upload-response-delete-width);
  height: var(--upload-response-delete-width);
  font-size: 38px;
  font-weight: 300;
  background: url("../assets/plus-icon.svg") center no-repeat;
  background-size: 30px 30px;
}


</style>
