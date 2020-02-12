package systems.dmx.dita;



public interface DITAConstants {

    // assoc between processor and first content topic
    static final String PROCESSOR_START     = "dmx.core.association";
    static final String ROLE_TYPE_PROCESSOR = "dmx.core.default";
    static final String ROLE_TYPE_START     = "dmx.core.default";

    // sequence assoc (TODO: move to Core)
    static final String SEQUENCE              = "dmx.core.sequence";
    static final String ROLE_TYPE_PREDECESSOR = "dmx.core.predecessor";
    static final String ROLE_TYPE_SUCCESSOR   = "dmx.core.successor";
}
