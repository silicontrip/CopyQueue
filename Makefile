
CLASSES = CopyJob.class CopyJobList.class  Display.class CopyListener.class \
        copytest1.class copyqueue.class copyclient.class

CP=.

all: copyqueue.jar 


copyqueue.jar: classes
	jar -cf copyqueue.jar $(CLASSES) com

classes: $(CLASSES)

%.class: %.java
	javac  -classpath $(CP) -encoding utf8 -Xlint:deprecation -Xlint:unchecked -target 1.5 $<

clean:
	rm *.class

