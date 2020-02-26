DMX-DITA
========

[DITA](https://en.wikipedia.org/wiki/Darwin_Information_Typing_Architecture) publishing for the [DMX](https://git.dmx.systems/dmx-platform/dmx-platform) platform.

Use DMX topicmaps to author DITA content.  
Generate publications in various output formats (HTML5, PDF, ...) by the means of [DITA Open Toolkit](https://www.dita-ot.org).

### Install

1. Install the [DMX-DITA plugin](https://download.dmx.systems/ci/dmx-dita/).

2. Install [DITA Open Toolkit](https://www.dita-ot.org) (DITA-OT).

### Configure

Add 3 settings to DMX's `conf/config.properties`:
```properties
# absolute directory where DITA-OT is installed
dmx.dita.install_dir = /path/to/dita-ot

# absolute output directory; if not exsits this directory is created
dmx.dita.output_dir = /path/to/output/dir

# absolute directory for temporary files and debug logs; this directory must exist
dmx.dita.temp_dir = /path/to/temp/dir
```

### Use

1. Create "DITA Topic"s.
2. Create a sequence of DITA Topics by drawing associations *from* predecessor *to* successor.  
   The associations will be typed automatically.
3. Create a "DITA Processor".
4. Associate the DITA Processor with the sequence's 1st DITA Topic.  
   (Leave the association's type and role types as is; direction doesn't matter.)
5. Choose the DITA Processor's "Run" command (from context menu).  
   A publication (in HTML5 format) will be generated in the configured output directory.


## Version History

**0.2** -- Feb 26, 2020

* Output format selectable by user

**0.1** -- Feb 18, 2020

* Content types: "DITA Topic"
* Structues: sequence
* Output formats: HTML5
