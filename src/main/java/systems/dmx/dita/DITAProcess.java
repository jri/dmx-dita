package systems.dmx.dita;

import static systems.dmx.dita.Constants.*;

import systems.dmx.core.service.CoreService;
import systems.dmx.core.Topic;
import systems.dmx.topicmaps.TopicmapsService;

import org.dita.dost.Processor;
import org.dita.dost.ProcessorFactory;
import org.dita.dost.util.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;



class DITAProcess {

    // ------------------------------------------------------------------------------------------------------- Constants

    // DITA-OT base directory
    private static final File DITA_DIR = new File("/usr/local/Cellar/dita-ot/3.4/libexec");
    private static final File TEMP_DIR = new File("/Users/jri/Documents/Test/dita-ot/tmp");

    // ---------------------------------------------------------------------------------------------- Instance Variables

    private long topicmapId;
    private TopicmapNavigation tmNav;

    private TopicmapsService tmService;
    private CoreService dmx;

    private Logger logger = Logger.getLogger(getClass().getName());

    // ---------------------------------------------------------------------------------------------------- Constructors

    DITAProcess(long processorId, long topicmapId, TopicmapsService tmService, CoreService dmx) {
        this.topicmapId = topicmapId;
        this.tmService = tmService;
        this.dmx = dmx;

        tmNav = new TopicmapNavigation(topicmapId, tmService, dmx);
        List<Topic> sequence = findTopicSequence(processorId);
        logger.info("Topics in sequence: " + sequence.size());
        new DITAExporter(TEMP_DIR).export(dmx.getTopic(topicmapId), sequence);
    }

    // ----------------------------------------------------------------------------------------- Package Private Methods

    void run() {
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            ClassLoader bundleClassloader = getClass().getClassLoader();
            Thread.currentThread().setContextClassLoader(bundleClassloader);
            logDebugInfo(currentClassLoader, bundleClassloader);
            //
            ProcessorFactory pf = ProcessorFactory.newInstance(DITA_DIR);
            pf.setBaseTempDir(TEMP_DIR);
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

    // ------------------------------------------------------------------------------------------------- Private Methods

    private List<Topic> findTopicSequence(long processorId) {
        List<Topic> sequence = new ArrayList();
        Topic topic = findStartTopic(processorId);
        while (topic != null) {
            sequence.add(topic);
            topic = findNextTopic(topic.getId());
        }
        return sequence;
    }

    private Topic findStartTopic(long processorId) {
        try {
            Topic topic = tmNav.getRelatedTopic(processorId, PROCESSOR_START, ROLE_TYPE_PROCESSOR, ROLE_TYPE_START,
                null);
            if (topic == null) {
                throw new RuntimeException("No start topic defined");
            }
            return topic;
        } catch (Exception e) {
            throw new RuntimeException("Finding start topic failed", e);
        }
    }

    private Topic findNextTopic(long topicId) {
        try {
            return tmNav.getRelatedTopic(topicId, SEQUENCE, ROLE_TYPE_PREDECESSOR, ROLE_TYPE_SUCCESSOR, null);
        } catch (Exception e) {
            throw new RuntimeException("Finding next topic in sequence failed", e);
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
