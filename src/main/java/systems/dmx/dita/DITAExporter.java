package systems.dmx.dita;

import systems.dmx.core.ChildTopics;
import systems.dmx.core.Topic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;



class DITAExporter {

    // ------------------------------------------------------------------------------------------------------- Constants

    // ---------------------------------------------------------------------------------------------- Instance Variables

    private File outputDir;
    private XMLOutputFactory xmlFactory;

    private Logger logger = Logger.getLogger(getClass().getName());

    // ---------------------------------------------------------------------------------------------------- Constructors

    DITAExporter(File outputDir) {
        this.outputDir = outputDir;
        this.xmlFactory = XMLOutputFactory.newFactory();
    }

    // ----------------------------------------------------------------------------------------- Package Private Methods

    void export(Topic topicmap, List<Topic> topics) {
        topics.stream().forEach(this::exportTopic);
        exportTopicmap(topicmap, topics);
    }

    // ------------------------------------------------------------------------------------------------- Private Methods

    private void exportTopic(Topic topic) {
        try (BufferedWriter bf = newFileWriter(topic.getId() + ".xml")) {
            XMLStreamWriter writer = xmlFactory.createXMLStreamWriter(bf);
            ChildTopics ct = topic.getChildTopics();
            writer.writeStartDocument();
            writer.writeStartElement("topic");
            writer.writeStartElement("title");
            writer.writeCharacters(ct.getString("dmx.dita.title"));
            writer.writeEndElement();
            writer.writeStartElement("body");
            writer.writeCharacters(ct.getString("dmx.dita.body"));
            writer.writeEndElement();
            writer.writeEndElement();
            writer.writeEndDocument();
        } catch (Exception e) {
            throw new RuntimeException("Writing XML file for topic " + topic.getId() + " failed", e);
        }
    }

    private void exportTopicmap(Topic topicmap, List<Topic> topics) {
        try (BufferedWriter bf = newFileWriter(topicmap.getId() + ".xml")) {
            XMLStreamWriter writer = xmlFactory.createXMLStreamWriter(bf);
            writer.writeStartDocument();
            writer.writeStartElement("map");
            writer.writeStartElement("title");
            writer.writeCharacters(topicmap.getSimpleValue().toString());
            writer.writeEndElement();
            topics.stream().forEach(topic -> writeTopicref(topic, writer));
            writer.writeEndElement();
            writer.writeEndDocument();
        } catch (Exception e) {
            throw new RuntimeException("Writing XML file for topicmap " + topicmap.getId() + " failed", e);
        }
    }

    private void writeTopicref(Topic topic, XMLStreamWriter writer) {
        try {
            writer.writeStartElement("topicref");
            writer.writeAttribute("href", topic.getId() + ".xml");
            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException("Writing <topicref> element for topic " + topic.getId() + " failed", e);
        }
    }

    private BufferedWriter newFileWriter(String filename) throws IOException {
        return new BufferedWriter(new FileWriter(new File(outputDir, filename)));
    }
}
