# TranskribusSwtGui / Transkribus expert client / TranskribusX

SWT based GUI for the Transkribus platform

## Build

### Requirements

* OpenJDK 11+
* Maven 3.6+

### Building

Please open Terminal and run

```bash
git clone https://github.com/ulb-sachsen-anhalt/TranskribusSwtGui <local-path>
cd <local-path>
mvn clean package
```

This will create dedicated binaries for Linux, Mac and Windows each in the Maven `target` dir, *if* all required libraries are available. In case you run into trouble, it might help to build the contained Transkribus-Libraries first:

* Transkribus-Core: <https://gitlab.com/readcoop/transkribus/TranskribusCore>
* Transkribus-Client: <https://gitlab.com/readcoop/transkribus/TranskribusClient>
