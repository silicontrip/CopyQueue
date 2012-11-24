
CLASSES = CopyJob.class CopyJobList.class  DisplayProgress.class \
        copytest1.class

CP=lanterna-2.1.1.jar:.

all: copyqueue.jar 


copyqueue.jar: classes
	jar -cf copyqueue.jar $(CLASSES)

classes: $(CLASSES)

%.class: %.java
	javac  -classpath $(CP) -Xlint:deprecation -Xlint:unchecked -target 1.5 $<

clean:
	rm *.class

