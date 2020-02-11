package systems.dmx.dita;

import systems.dmx.core.osgi.PluginActivator;

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
public class DITAPlugin extends PluginActivator {

    // ------------------------------------------------------------------------------------------------------- Constants

    // DITA-OT base directory
    private static final File DITA_DIR = new File("/usr/local/Cellar/dita-ot/3.4/libexec");

    // ---------------------------------------------------------------------------------------------- Instance Variables

    private Logger logger = Logger.getLogger(getClass().getName());

    // -------------------------------------------------------------------------------------------------- Public Methods

    @PUT
    @Path("/process/{id}/topicmap/{topicmapId}")
    public void process(@PathParam("id") long processorId, @PathParam("topicmapId") long topicmapId) {
        ClassLoader originalContextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            ClassLoader bundleClassloader = getClass().getClassLoader();
            Thread.currentThread().setContextClassLoader(bundleClassloader);

            logger.info("### org.osgi.framework.bootdelegation=" +
                System.getProperty("org.osgi.framework.bootdelegation"));
            logger.info("### org.osgi.framework.system.packages.extra=" +
                System.getProperty("org.osgi.framework.system.packages.extra") + "\n");

            logger.info("### Current ClassLoader=" + originalContextClassLoader + ", parent=" +
                originalContextClassLoader.getParent());
            logger.info("### Bundle ClassLoader=" + bundleClassloader + ", parent=" +
                bundleClassloader.getParent() + "\n");

            logger.info("### Available transtypes=" + Configuration.transtypes + "\n");

            ProcessorFactory pf = ProcessorFactory.newInstance(DITA_DIR);
            pf.setBaseTempDir(new File("/Users/jri/Documents/Test/dita-ot/tmp"));
            // Create a processor using the factory and configure the processor
            Processor p = pf.newProcessor("html5")
                .setInput(new File("/usr/local/Cellar/dita-ot/3.4/libexec/docsrc/samples/sequence.ditamap"))
                .setOutputDir(new File("/Users/jri/Documents/Test/dita-ot"));
                //.setProperty("nav-toc", "partial");

            // Run conversion
            p.run();
        } catch (Exception e) {
            throw new RuntimeException("DITA processing failed, processorId=" + processorId + ", topicmapId=" +
                topicmapId, e);
        } finally {
            Thread.currentThread().setContextClassLoader(originalContextClassLoader);
        }
    }

    // ------------------------------------------------------------------------------------------------- Private Methods
}
