package systems.dmx.dita;

import systems.dmx.core.ChildTopics;
import systems.dmx.core.Topic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;



class DITAExporter {

    // ------------------------------------------------------------------------------------------------------- Constants

    public static final String DOCTYPE_TOPIC = "<!DOCTYPE topic PUBLIC \"-//OASIS//DTD DITA Topic//EN\" \"topic.dtd\">";
    public static final String DOCTYPE_MAP   = "<!DOCTYPE map PUBLIC \"-//OASIS//DTD DITA Map//EN\" \"map.dtd\">";

    private static final XMLOutputFactory xmlFactory = XMLOutputFactory.newFactory();

    // ---------------------------------------------------------------------------------------------- Instance Variables

    private File outputDir;

    private Logger logger = Logger.getLogger(getClass().getName());

    // ---------------------------------------------------------------------------------------------------- Constructors

    DITAExporter(File outputDir) {
        this.outputDir = outputDir;
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
            writer.writeDTD(DOCTYPE_TOPIC);
            writer.writeStartElement("topic");
            writer.writeAttribute("id", "topic-" + topic.getId());
            writer.writeStartElement("title");
            writer.writeCharacters(ct.getString("dmx.dita.title"));
            writer.writeEndElement();
            writer.writeStartElement("body");
            parseHTML(ct.getString("dmx.dita.body"), writer);
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
            writer.writeDTD(DOCTYPE_MAP);
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
            writer.writeAttribute("type", "topic");
            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException("Writing <topicref> element for topic " + topic.getId() + " failed", e);
        }
    }

    private BufferedWriter newFileWriter(String filename) throws IOException {
        return new BufferedWriter(new FileWriter(new File(outputDir, filename)));
    }

    // --- HTML -> XHTML ---

    private static final List TAG_FILTER = Arrays.asList("html", "head", "body", "span", "br");

    private void parseHTML(String html, XMLStreamWriter writer) {
        try {
            new ParserDelegator().parse(
                new StringReader(html),
                new XMLWriterCallback(writer),
                false       // ignoreCharSet
            );
        } catch (Exception e) {
            throw new RuntimeException("HTML parsing failed", e);
        }
    }

    private class XMLWriterCallback extends HTMLEditorKit.ParserCallback {

        private XMLStreamWriter writer;

        private XMLWriterCallback(XMLStreamWriter writer) {
            this.writer = writer;
        }

        @Override
        public void handleStartTag(Tag t, MutableAttributeSet a, int pos) {
            try {
                if (filter(t)) {
                    // logger.info("# start " + t + ", " + a + ", pos=" + pos);
                    writer.writeStartElement(t.toString());
                }
            } catch (Exception e) {
                throw new RuntimeException("Writing to XML file failed", e);
            }
        }

        @Override
        public void handleSimpleTag(Tag t, MutableAttributeSet a, int pos) {
            try {
                if (filter(t)) {
                    // logger.info("# simple " + t + ", " + a + ", pos=" + pos);
                    writer.writeEmptyElement(t.toString());
                }
            } catch (Exception e) {
                throw new RuntimeException("Writing to XML file failed", e);
            }
        }

        @Override
        public void handleEndTag(Tag t, int pos) {
            try {
                if (filter(t)) {
                    // logger.info("# end " + t + ", pos=" + pos);
                    writer.writeEndElement();
                }
            } catch (Exception e) {
                throw new RuntimeException("Writing to XML file failed", e);
            }
        }

        @Override
        public void handleText(char[] data, int pos) {
            try {
                // logger.info("# text " + new String(data) + ", pos=" + pos);
                writer.writeCharacters(new String(data));
            } catch (Exception e) {
                throw new RuntimeException("Writing to XML file failed", e);
            }
        }

        private boolean filter(Tag t) {
            return !TAG_FILTER.contains(t.toString());
        }
    }
}
