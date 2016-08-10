JC     = javac
EXE    = GLCrane
JFLAGS = -sourcepath src -cp bin:lib/* -d bin -target 7 -source 7

default: all

all: $(CLASSES)
	javac $(JFLAGS) `find src -type f -name "*.java"`
	jar cvmf MANIFEST.MF $(EXE).jar \
	   `find bin -type f -name "*.class"` \
	   `find lib -type f -name "*.jar"`
	chmod +x ./$(EXE).jar

clean:
	find . -name "*.class" -exec rm {} \;
	rm -f $(EXE).jar
