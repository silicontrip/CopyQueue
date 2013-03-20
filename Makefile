
CLASSES = CopyJob.class CopyJobList.class  Display.class CopyListener.class \
        copytest1.class copyqueue.class copyclient.class

CP=.:/Users/mark/Downloads/lanterna-2.1.3.jar

all: copyqueue.jar 


copyqueue.jar: classes
	jar -cf copyqueue.jar $(CLASSES) 

classes: $(CLASSES)

%.class: %.java
	javac  -classpath $(CP) -encoding utf8 -Xlint:deprecation -Xlint:unchecked -target 1.5 $<

clean:
	rm *.class

