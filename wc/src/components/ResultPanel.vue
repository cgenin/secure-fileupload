<template>
  <div class="result-panel">
    <div v-if="success" class="success-box">
      <div class="shadow-container">

        <div class="ok-icon"></div>
      </div>
      <div class="shadow-container">
        <div class="shadow scale"></div>
      </div>
      <div class="message">
        <h1 class="alert">{{ successMessage }}</h1>
      </div>
      <div class="shadow-container">
        <button class="button-box" @click="onClick">
          <h1 class="green">{{ successLabelButton }}</h1>
        </button>
      </div>
    </div>
    <div v-if="!success" class="error-box">
      <div class="shadow-container">
        <div class="ko-icon"></div>
      </div>
      <div class="shadow-container">
        <div class="shadow move"></div>
      </div>
      <div class="message">
        <h1 class="alert">{{ errorMessage }}</h1>
        <p>
          {{ errorDescription }}
        </p>
      </div>
      <div class="shadow-container">
        <button class="button-box" @click="onClick">
          <h1 class="red">{{ errorLabelButton }}</h1>
        </button>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  name: 'ResultPanel',
  props: {
    success: {
      type: Boolean,
      required: true
    },
    labels: {
      type: Object,
      default: () => ({})
    }
  },
  computed: {
    successMessage() {
      return this.labels.successMessage || 'Succés !'
    },
    successLabelButton() {
      return this.labels.successLabelButton || 'Continuer'
    },
    errorMessage() {
      return this.labels.errorMessage || 'Erreur !'
    },
    errorLabelButton() {
      return this.labels.errorLabelButton || 'Réessayer'
    },
    errorDescription() {
      return this.labels.errorDescription || 'Les fichiers n\'ont pas pu être transférés sur le serveur'
    }
  },
  methods: {
    onClick() {
      const { success } = this;
      this.$emit('click', { success });
    }
  }
}
</script>
<style scoped>
.result-panel {
  --result-background-color: var(--sfc-background-color, #fff);
  --result-danger-color: var(--sfc-danger-color, #d44034);
  --result-success-color: var(--sfc-success-color, #649d19);
  --result-border-color: var(--sfc-border-color, #eeeeee);

  margin: .5em;
  width: 100%;
  display: flex;
  justify-content: center;
}

.result-panel .ko-icon,
.result-panel .ok-icon {
  margin-top: 1em;
  width: 100px;
  height: 100px;

}

.result-panel .ko-icon {
  background: url("../assets/error-icon.svg") center no-repeat;

}

.result-panel .ok-icon {
  background: url("../assets/ok-icon.svg") center no-repeat;
}

h1 {
  font-size: 1.5em;
  font-weight: 100;
  letter-spacing: 3px;
  padding-top: 5px;
  color: var(--result-background-color);
  padding-bottom: 5px;
  text-transform: uppercase;
}

.button-box h1 {
  margin-top: auto;
  margin-bottom: auto;
}

.green {
  color: var(--result-success-color);
}

.red {
  color: var(--result-danger-color);
}

.alert {
  font-weight: 700;
  letter-spacing: 5px;
}

p {
  margin: 5px;
  font-size: .8em;
  font-weight: 100;
  color: var(--result-background-color);
  letter-spacing: 1px;
}

button {
  cursor: pointer;
}

.success-box,
.error-box {
  width: 300px;
  height: 350px;
  display: flex;
  flex-direction: column;
  align-items: stretch;
}

.success-box {

  background: linear-gradient(to bottom right, #89d72c 40%, var(--result-success-color) 100%);
  border-radius: 20px;
  box-shadow: 5px 5px 20px var(--result-border-color);
  perspective: 40px;
}

.error-box {
  background: linear-gradient(to bottom left, #f14561 40%, var(--result-danger-color) 100%);
  border-radius: 20px;
  box-shadow: 5px 5px 20px var(--result-border-color);
}

.shadow-container {
  display: flex;
  justify-content: center;
}

.shadow {
  margin-top: 1em;
  width: 100px;
  height: 10px;
  opacity: .5;
  background: #777777;

  border-radius: 50%;
  z-index: 1;
}

.scale {
  animation: scale 1s ease-in infinite;
}

.move {
  animation: move 10s ease-in-out infinite;
}

.message {
  width: 100%;
  text-align: center;
}

.button-box {
  margin-block-end: auto;
  padding: .5em;
  background: var(--result-background-color);
  width: 75%;
  border-radius: 20px;
  outline: 0;
  border: none;
  box-shadow: 2px 2px 10px rgba(119, 119, 119, 0.5);
  transition: all .5s ease-in-out;
}

.button-box:hover {
  background: #efefef;
  transform: scale(1.05);
  transition: all .3s ease-in-out;
}

@keyframes bounce {
  50% {
    transform: translateY(-10px);
  }
}

@keyframes scale {
  50% {
    transform: scale(0.9);
  }
}

@keyframes roll {
  0% {
    transform: rotate(0deg);
    left: 25%;
  }
  50% {
    left: 60%;
    transform: rotate(168deg);
  }
  100% {
    transform: rotate(0deg);
    left: 25%;
  }
}

@keyframes move {
  0% {
    margin-left: 0;
    margin-right: 0;
  }

  25% {
    margin-left: -25%;
    margin-right: 0;
  }

  50% {
    margin-left: 0;
    margin-right: 0;

  }
  75% {
    margin-left: 0;
    margin-right: -25%;
  }

  100% {
    margin-left: 0;
    margin-right: 0;
  }
}
</style>