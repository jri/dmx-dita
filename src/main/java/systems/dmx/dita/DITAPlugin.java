package systems.dmx.dita;

import systems.dmx.core.Topic;
import systems.dmx.core.osgi.PluginActivator;
import systems.dmx.core.service.Inject;
import systems.dmx.topicmaps.TopicmapsService;

import org.dita.dost.Processor;
import org.dita.dost.ProcessorFactory;
import org.dita.dost.util.Configuration;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.logging.Logger;



@Path("/dita")
@Consumes("application/json")
@Produces("application/json")
public class DITAPlugin extends PluginActivator implements DITAConstants {

    // ------------------------------------------------------------------------------------------------------- Constants

    // DITA-OT base directory
    private static final File DITA_DIR = new File("/usr/local/Cellar/dita-ot/3.4/libexec");

    // ---------------------------------------------------------------------------------------------- Instance Variables

    @Inject
    private TopicmapsService tmService;

    private TopicmapNavigation tmNav;   // TODO: thread-safety

    private Logger logger = Logger.getLogger(getClass().getName());

    // -------------------------------------------------------------------------------------------------- Public Methods

    @PUT
    @Path("/process/{id}/topicmap/{topicmapId}")
    public void process(@PathParam("id") long processorId, @PathParam("topicmapId") long topicmapId) {
        try {
            tmNav = new TopicmapNavigation(topicmapId, tmService, dmx);
            findStartTopic(processorId);
            _process();
        } catch (Exception e) {
            throw new RuntimeException("DITA processing failed, processorId=" + processorId + ", topicmapId=" +
                topicmapId, e);
        }
    }

    // ------------------------------------------------------------------------------------------------- Private Methods

    private Topic findStartTopic(long processorId) {
        try {
            Topic topic = tmNav.getRelatedTopic(processorId, PROCESSOR_START, ROLE_TYPE_PROCESSOR, ROLE_TYPE_START, null);
            if (topic == null) {
                throw new RuntimeException("No start topic defined");
            }
            return topic;
        } catch (Exception e) {
            throw new RuntimeException("Finding start topic failed", e);
        }
    }

    private void _process() {
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            ClassLoader bundleClassloader = getClass().getClassLoader();
            Thread.currentThread().setContextClassLoader(bundleClassloader);
            logDebugInfo(currentClassLoader, bundleClassloader);
            //
            ProcessorFactory pf = ProcessorFactory.newInstance(DITA_DIR);
            pf.setBaseTempDir(new File("/Users/jri/Documents/Test/dita-ot/tmp"));
            // Create a processor using the factory and configure the processor
            Processor p = pf.newProcessor("html5")
                .setInput(new File("/usr/local/Cellar/dita-ot/3.4/libexec/docsrc/samples/sequence.ditamap"))
                .setOutputDir(new File("/Users/jri/Documents/Test/dita-ot"));
                //.setProperty("nav-toc", "partial");
            //
            p.run();
        } catch (Exception e) {
            throw new RuntimeException("Running DITA-OT failed", e);
        } finally {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
    }

    private void logDebugInfo(ClassLoader currentClassLoader, ClassLoader bundleClassloader) {
        logger.info("### org.osgi.framework.bootdelegation=" + System.getProperty("org.osgi.framework.bootdelegation"));
        logger.info("### org.osgi.framework.system.packages.extra=" +
            System.getProperty("org.osgi.framework.system.packages.extra") + "\n");
        logger.info("### Current ClassLoader=" + currentClassLoader + ", parent=" + currentClassLoader.getParent());
        logger.info("### Bundle ClassLoader=" + bundleClassloader + ", parent=" + bundleClassloader.getParent() + "\n");
        logger.info("### Available transtypes=" + Configuration.transtypes + "\n");
    }
}
