import ResultPanel from './ResultPanel';

export default {
    title: 'Components/ResultPanel',
    component: ResultPanel,
    argTypes: {
        click: { action: 'click' },
    },
};


export const Default = (args, { argTypes }) => ({
    props: Object.keys(argTypes),
    label: 'Default',
    components: { ResultPanel },
    template: `
      <div style="display: flex; justify-content: space-around">
      <result-panel :success="true" v-bind="$props" @click="click"></result-panel>
      <result-panel :success="false" v-bind="$props" @click="click"></result-panel>
      </div>`
});

