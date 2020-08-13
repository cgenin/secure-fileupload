import Buttons from './Buttons';

export default {
    title: 'Components/Buttons',
    component: Buttons,
    argTypes: {
        deleteAllFiles: { action: 'DeleteAllfiles' },
        send: { action: 'Send' },
    },
};


export const Default = (args, { argTypes }) => ({
    props: Object.keys(argTypes),
    label: 'Default',
    components: { Buttons },
    template: `
      <Buttons
          @onDeleteAll="deleteAllFiles"
          @onSend="send"
          v-bind="$props"></Buttons>`
});

