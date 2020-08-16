import SecureFileUpload from './SecureFileUpload';
import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';

const mock = new MockAdapter(axios);

export default {
    title: 'Components/SecureFileUpload',
    component: SecureFileUpload,
    argTypes: {
        onClose: { action: 'onclose' },
        onSuccess: { action: 'onsuccess' },
        onError: { action: 'onerror' },
    },
};

mock.onPost('/upload/success').reply(200, {});
mock.onPost('/upload/failed').reply(400, {});

export const Success = (args, { argTypes }) => ({
    props: Object.keys(argTypes),
    label: 'Success',
    components: { SecureFileUpload },
    mounted() {

    },
    template: `
      <div style="display: flex; justify-content: space-around">
      <secure-file-upload v-bind="$props" token="success" :accept="['pdf', 'jpg']"
                          @onSuccess="onSuccess" @onError="onError" @onClose="onClose"/>
      </div>`
});

export const Error = (args, { argTypes }) => ({
    props: Object.keys(argTypes),
    label: 'Error',
    components: { SecureFileUpload },
    mounted() {

    },
    template: `
      <div style="display: flex; justify-content: space-around">
      <secure-file-upload v-bind="$props" token="failed" :accept="['pdf', 'jpg']"
                          @onSuccess="onSuccess" @onError="onError" @onClose="onClose"/>
      </div>`
});

