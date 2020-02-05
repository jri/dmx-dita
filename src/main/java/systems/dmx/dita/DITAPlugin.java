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

    @GET
    @Path("/process")
    public void process() {
        try {
            logger.info("### plugindirs=" + Configuration.configuration.get("plugindirs"));
            logger.info("### validate=" + Configuration.configuration.get("validate"));
            logger.info("### transtypes=" + Configuration.transtypes);
            logger.info("### printTranstype=" + Configuration.printTranstype);
            ProcessorFactory pf = ProcessorFactory.newInstance(DITA_DIR);
            pf.setBaseTempDir(new File("/Users/jri/Documents/Test/dita-ot/tmp"));
            // Create a processor using the factory and configure the processor
            Processor p = pf.newProcessor("html5")
                .setInput(new File("/usr/local/Cellar/dita-ot/3.4/libexec/docsrc/samples/sequence.ditamap"))
                .setOutputDir(new File("/Users/jri/Documents/Test/dita-ot"));
                //.setProperty("nav-toc", "partial");
            //
            // Run conversion
            p.run();
        } catch (Exception e) {
            throw new RuntimeException("DITA processing failed", e);
        }
    }

    // ------------------------------------------------------------------------------------------------- Private Methods
}
