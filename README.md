# TranskribusSwtGui / Transkribus expert client / TranskribusX
- SWT based GUI for the Transkribus platform

## Installation and startup of the latest release

- register and download the latest release from either [transkribus.eu](https://transkribus.eu) or [readcoop.eu/transkribus](https://readcoop.eu/transkribus)
- Unpack the release .zip and start the app using either Transkribus.exe (Windows), Transkribus.command (Mac) or Transkribus.sh (Linux) inside the unpacked folder 
- Make sure you do have write access to the folder!
- If the program won't start, check if the right Java is installed - a 64 bit version >= version 8 is needed
  - download and install the latest JDK from this Homepage: https://adoptopenjdk.net/

## Build status
[![Build Status](http://dbis-halvar.uibk.ac.at/jenkins/buildStatus/icon?job=TranskribusSwtGui)](http://dbis-halvar.uibk.ac.at/jenkins/job/TranskribusSwtGui)

## Building
Here is a short guide with steps that need to be performed
to build your project.

### Requirements
- Java >= version 8
- Maven
- All further dependencies are gathered via Maven

### Build Steps
TODO
```
git clone https://github.com/Transkribus/TranskribusSwtGui
cd TranskribusSwtGui
mvn install
```

### Links
- https://transkribus.eu/TranskribusSwtGui/apidocs/index.html
