package systems.dmx.dita;

import systems.dmx.core.service.CoreService;
import systems.dmx.core.RelatedTopic;
import systems.dmx.core.Topic;
import systems.dmx.topicmaps.TopicmapsService;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;



public class TopicmapNavigation {

    // ---------------------------------------------------------------------------------------------- Instance Variables

    private long topicmapId;
    private TopicmapsService tmService;
    private CoreService dmx;

    private Logger logger = Logger.getLogger(getClass().getName());

    // ---------------------------------------------------------------------------------------------------- Constructors

    public TopicmapNavigation(long topicmapId, TopicmapsService tmService, CoreService dmx) {
        this.topicmapId = topicmapId;
        this.tmService = tmService;
        this.dmx = dmx;
    }

    // -------------------------------------------------------------------------------------------------- Public Methods

    public RelatedTopic getRelatedTopic(long topicId, String assocTypeUri, String myRoleTypeUri,
                                        String othersRoleTypeUri, String othersTopicTypeUri) {
        List<RelatedTopic> topics =
            dmx.getTopic(topicId).getRelatedTopics(assocTypeUri, myRoleTypeUri, othersRoleTypeUri, othersTopicTypeUri)
            .stream().filter(this::isInTopicmap).collect(Collectors.toList());
        switch (topics.size()) {
        case 0:
            return null;
        case 1:
            return topics.get(0);
        default:
            throw new RuntimeException("Ambiguity");
        }
    }

    // ------------------------------------------------------------------------------------------------- Private Methods

    private boolean isInTopicmap(Topic topic) {
        return tmService.getTopicMapcontext(topicmapId, topic.getId()) != null;
    }
}
