import ProgressBar from './ProgressBar';

export default {
    title: 'Components/Progress',
    component: ProgressBar,
    argTypes: {

    },
};


export const Default = (args, { argTypes }) => ({
    props: Object.keys(argTypes),
    label: 'Default',
    components: { ProgressBar },
    template: `
      <progress-bar v-bind="$props"></progress-bar>`
});

