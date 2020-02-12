package systems.dmx.dita;

import systems.dmx.core.Topic;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.stream.XMLOutputFactory;



class DITAExporter {

    // ------------------------------------------------------------------------------------------------------- Constants

    // ---------------------------------------------------------------------------------------------- Instance Variables

    private File outputDir;

    private Logger logger = Logger.getLogger(getClass().getName());

    // ---------------------------------------------------------------------------------------------------- Constructors

    DITAExporter(File outputDir) {
        this.outputDir = outputDir;
    }

    // ----------------------------------------------------------------------------------------- Package Private Methods

    void export(List<Topic> topics) {
        XMLOutputFactory.newFactory();
        XMLOutputFactory.newInstance();
    }
}
