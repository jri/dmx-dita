package systems.dmx.dita;

import static systems.dmx.dita.Constants.*;

import systems.dmx.core.model.AssocModel;
import systems.dmx.core.model.ChildTopicsModel;
import systems.dmx.core.model.SimpleValue;
import systems.dmx.core.model.TopicModel;
import systems.dmx.core.osgi.PluginActivator;
import systems.dmx.core.service.Inject;
import systems.dmx.core.service.event.PreCreateAssoc;
import systems.dmx.core.service.event.PreCreateTopic;
import systems.dmx.core.storage.spi.DMXTransaction;
import systems.dmx.core.util.DMXUtils;
import systems.dmx.topicmaps.TopicmapsService;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;

import java.util.logging.Logger;



@Path("/dita")
@Consumes("application/json")
@Produces("application/json")
public class DITAPlugin extends PluginActivator implements PreCreateTopic, PreCreateAssoc {

    // ---------------------------------------------------------------------------------------------- Instance Variables

    @Inject
    private TopicmapsService tmService;

    private Logger logger = Logger.getLogger(getClass().getName());

    // -------------------------------------------------------------------------------------------------- Public Methods

    // DITA Service

    @PUT
    @Path("/process/{id}/topicmap/{topicmapId}")
    public void process(@PathParam("id") long processorId, @PathParam("topicmapId") long topicmapId) {
        try {
            new DITAProcess(processorId, topicmapId, tmService, dmx).run();
        } catch (Exception e) {
            throw new RuntimeException("DITA processing failed, processorId=" + processorId + ", topicmapId=" +
                topicmapId, e);
        }
    }

    // Hooks

    @Override
    public void init() {
        DMXTransaction tx = dmx.beginTx();
        try {
            DITAProcess.getTranstypes().forEach(transtype -> {
                dmx.createTopic(mf.newTopicModel(DITA_OUTPUT_FORMAT, new SimpleValue(transtype)));
            });
            tx.success();
        } catch (Exception e) {
            logger.warning("ROLLBACK! (" + this + ")");
            throw new RuntimeException("Creating \"Output Format\" topics failed", e);
        } finally {
            tx.finish();
        }
    }

    // Listeners

    @Override
    public void preCreateTopic(TopicModel topic) {
        if (topic.getTypeUri().equals(DITA_PROCESSOR)) {
            ChildTopicsModel ctm = topic.getChildTopicsModel();
            if (ctm.getString(DITA_OUTPUT_FORMAT, "").equals("")) {
                ctm.put(DITA_OUTPUT_FORMAT, "html5");
            }
        }
    }

    @Override
    public void preCreateAssoc(AssocModel assoc) {
        // DITA Topic <-> DITA Topic
        DMXUtils.associationAutoTyping(assoc, DITA_TOPIC, DITA_TOPIC, SEQUENCE, ROLE_PREDECESSOR, ROLE_SUCCESSOR);
    }
}
