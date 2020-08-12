import FileUpload from './Fileupload';

export default {
    title: 'FileUpload/Component',
    component: FileUpload,
    argTypes: {
        selectFile: { action: 'fileSelected' },
    },
};


export const Default = (args, { argTypes }) => ({
    props: Object.keys(argTypes),
    label: 'Default',
    components: { FileUpload },
    template: `
      <file-upload 
          v-bind="$props"></file-upload>`
});


export const Events = (args, { argTypes }) => ({
    props: Object.keys(argTypes),
    label: 'Events',
    components: { FileUpload },
    template: `
      <file-upload
          @onSelectFile="selectFile"
      ></file-upload>`,

});