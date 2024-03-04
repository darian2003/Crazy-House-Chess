JAVAC = javac
METAINF = META-INF
MANIFEST = $(METAINF)/MANIFEST.MF
JVM = java
JVMFLAGS = -jar

JAVACFLAGS =
JAR = jar
JARFLAGS = cmfv

SRCS := $(wildcard *.java)
PRGM = Main.jar

.PHONY: all clean
.SILENT: run

all: $(PRGM)

$(MANIFEST):
	mkdir META-INF 2>/dev/null
	echo "Main-Class: Main">$(MANIFEST)

$(PRGM): $(MANIFEST)
	$(JAVAC) $(JAVACFLAGS) $(SRCS)
	$(JAR) $(JARFLAGS) $(MANIFEST) $(PRGM) *.class

run: $(PRGM)
	$(JVM) $(JVMFLAGS) $(PRGM)

clean:
	rm -rf $(PRGM)
	rm -rf *.class
	rm -rf $(METAINF)
