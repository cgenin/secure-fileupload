import FileUpload from './Fileupload';

export default {
    title: 'Components/FileUpload',
    component: FileUpload,
    argTypes: {
        selectFile: { action: 'fileSelected' },
        change: { action: 'change' },
    },
};


export const Default = (args, { argTypes }) => ({
    props: Object.keys(argTypes),
    label: 'Default',
    components: { FileUpload },
    template: `
      <file-upload
          @change="change"
          @onSelectFile="selectFile"
          v-bind="$props"></file-upload>`
});