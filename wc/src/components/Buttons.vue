<template>

  <div class="buttons"
       :class="{disabled}">
    <button v-if="show" :disabled="disabled" class="outline danger-white" @click="deleteAllFiles">{{
        labelDeleteAll
      }}
    </button>
    <button v-if="show" :disabled="disabled" class="outline green-white" @click="send">{{ labelSend }}</button>
    <span v-if="!show">&nbsp;</span>
  </div>

</template>
<script>
export default {
  name: 'Buttons',
  props: {
    show: {
      type: Boolean,
      default: true
    },
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
      this.$emit('onDeleteAll', {});
    },
    send() {
      this.$emit('onSend', {});
    }
  }
}
</script>
<style scoped>
@import url("../assets/disabled.css");

.buttons {
  --buttons-background-color: var(--sfc-background-color, #e3e3e3);
  --buttons-danger-color: var(--sfc-danger-color, #d44034);
  --buttons-success-color: var(--sfc-success-color, #649d19);
  --buttons-height: 40px;
  width: 100%;
  display: flex;
  justify-content: space-around;
  margin: .5em;
  height: var(--buttons-height);
  min-height: var(--buttons-height);
}


.buttons button {
  cursor: pointer;
  outline: none;
}

.buttons button.outline {
  position: relative;
  z-index: 3;
  background: transparent;
  color: var(--progress-color);
  font-size: 14px;
  border-color: var(--progress-color);
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
  background: var(--progress-color);
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