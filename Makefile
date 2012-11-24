
CLASSES = CopyJob.class CopyJobList.class \
        copytest1.class

all: copyqueue.jar 


copyqueue.jar: classes
	jar -cf copyqueue.jar $(CLASSES)

classes: $(CLASSES)

%.class: %.java
	javac  -Xlint:deprecation -Xlint:unchecked -target 1.5 $<

clean:
	rm *.class

