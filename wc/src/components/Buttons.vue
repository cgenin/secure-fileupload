<template>

  <div class="buttons"
    :class="{disabled}">
    <button class="outline danger-white" @click="deleteAllFiles">{{ labelDeleteAll }}</button>
    <button class="outline green-white" @click="send">{{ labelSend }}</button>
  </div>

</template>
<script>
export default {
  name: 'Buttons',
  props: {
    labelDeleteAll: {
      type: String,
      default: 'Tout effacer',
    },
    labelSend: {
      type: String,
      default: 'Envoyer',
    },
    disabled: {
      type: Boolean,
      default: false
    }
  },
  methods: {
    deleteAllFiles() {
      if (this.disabled) {
        return;
      }
      this.$emit('onDeleteAll', {});
    },
    send() {
      if (this.disabled) {
        return;
      }
      this.$emit('onSend', {});
    }
  }
}
</script>
<style scoped>
@import url("../assets/disabled.css");

.buttons {
  --buttons-background-color: #fff;
  --buttons-danger-color: #db4437;
  --buttons-success-color: #7dc21e;
  width: 100%;
  display: flex;
  justify-content: space-around;
}


.buttons button {
  cursor: pointer;
  outline: none;
}

.buttons button.outline {
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

.buttons button.outline a {
  text-decoration: none;
}

.buttons button.outline:hover {
  color: var(--buttons-background-color);
  background: var(--uploader-color);
  border-color: var(--buttons-background-color);
  transition: all 0.2s linear;
}

.buttons button.outline:active {
  border-radius: 22px;
}

.buttons button.green-white {
  font-weight: 700;
  color: var(--buttons-success-color);
  border-color: var(--buttons-success-color);
  background: transparent;
}

.buttons button.green-white:hover {
  color: var(--buttons-background-color);
  background: var(--buttons-success-color);
  border-color: var(--buttons-success-color);
}

.buttons button.danger-white {
  font-weight: 700;
  color: var(--buttons-danger-color);
  border-color: var(--buttons-danger-color);
  background: transparent;
}

.buttons button.danger-white:hover {
  color: var(--buttons-background-color);
  background: var(--buttons-danger-color);
  border-color: var(--buttons-danger-color);
}


</style>