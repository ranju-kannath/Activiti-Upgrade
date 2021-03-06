#!/bin/bash
export MAVEN_OPTS="-Xms521M -Xmx1024M"
if [[ "$3" == "" ]] ; then

   	echo "Missing argument: correct usage <./run-upgrade-test.sh oldVersion newVersion database>"
   	
else

	OLD_VERSION_MODULE="activiti-upgrade-$1"
    echo "Old version module: $OLD_VERSION_MODULE"
	NEW_VERSION_MODULE="activiti-upgrade-$2"
    echo "Old version module: $NEW_VERSION_MODULE"
    DATABASE=$3
    echo "Database type: $DATABASE"
        
	echo
    echo "Running old version module: generating Activiti data for Activiti $1"
    echo
    cd $OLD_VERSION_MODULE
    mvn -Ddatabase=$DATABASE -DoldVersion=$1 -Dmaven.test.skip=true clean test
    
    STATUS=$?
	if [ $STATUS -eq 0 ] 
	then
		echo
		echo "Running new version module: running $2 unit tests against $1 data"
		echo
    	cd ..
    	cd $NEW_VERSION_MODULE
    	mvn -Ddatabase=$DATABASE -DoldVersion=$1 clean test
	else
		echo
		echo
    	echo "Build failure on old version module. Halting."
    	echo
	fi   
	 
fi
