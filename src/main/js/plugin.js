export default ({store, axios: http}) => ({

  contextCommands: {
    topic: topic => {
      if (topic.typeUri === 'dmx.dita.processor') {
        return [{
          label: 'Run',
          handler: id => {
            const topicmapId = store.getters.topicmapId
            http.put(`/dita/process/${id}/topicmap/${topicmapId}`)
          }
        }]
      }
    }
  }
})
