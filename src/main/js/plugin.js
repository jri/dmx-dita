export default ({store, axios: http}) => ({

  contextCommands: {
    topic: [{
      label: 'Run',
      handler: id => {
        const topicmapId = store.getters.topicmapId
        console.log('DITA processing', id, topicmapId)
        http.put(`/dita/process/${id}/topicmap/${topicmapId}`)
      }
    }]
  }
})
