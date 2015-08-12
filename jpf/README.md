# What is it

java.util.Map implementation should be thread-save. 
Generally it's quite difficult or nearly impossible to prove that some code is thread-safe.
The project try to crate all possible JVM states and check that any state doesn't break data consistency.
Here is used [JPF - Java Path Finder](http://babelfish.arc.nasa.gov/trac/jpf/wiki).      

# Test execution

##prerequisite

1. installed maven
2. installed optionally eclipse
3. installed jpf module 'jpf-core'
4. installed jpf module 'jpf-concurrent'
	* install [mercurial SCM](https://mercurial.selenic.com/)
	* install [ant](http://ant.apache.org/)

## Executions steps

1. Install JBree library into local maven repository. To do that perform in directory 'jbtree'
```text
mvn clean install
``` 
2. Copy JBtree source file of JBtree map from maven repository to jpf direcory. In 'jpf' directpry execute: 
```text
mvn process-resources
``` 
3. It's optional step. When you want to edit test code or edit in eclipse than in direcory 'jpf' execute: 
```text
mvn eclipse:clean eclipse:eclipse
```
After that add new project in eclipse 'jbtree-jpf'.
Add to build path 'user library' 'jpf'. 
After that source code can be compiled in eclipse.

4. Compile all sources with following command:
```text
mvn clean test-compile
```

5. Execute test with command:
```text
./start
```

## Test result
Example test result should look like:

```text
JavaPathfinder v6.0 (rev 1038) - (C) RIACS/NASA Ames Research Center


====================================================== system under test
application: com/coroptis/TreeTest.java

====================================================== search started: 8/9/15 11:21 PM
Test is done
Test is done
Test is done
Test is done
Test is done

====================================================== results
no errors detected

====================================================== statistics
elapsed time:       00:00:01
states:             new=67, visited=70, backtracked=137, end=5
search:             maxDepth=10, constraints hit=0
choice generators:  thread=67 (signal=0, lock=21, shared ref=12), data=0
heap:               new=1694, released=817, max live=824, gc-cycles=137
instructions:       111972
max memory:         123MB
loaded code:        classes=128, methods=1792

====================================================== search finished: 8/9/15 11:21 PM
```
Test result say that code is correct.
Please note that test was executed with low number of TreeTest.NUMBER_OF_CONCURRENT_INSERTS.
Results is just example.   
