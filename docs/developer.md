Developer Notes
---------------
This is a scratch pad from the developer to document convenient things.





## How the app is structured

### Scene management / switching
TODO - document how the scenes are managed

### Spring integration
TODO - document how spring is bootstrapped

## Quirks

### Very little testing!
The automated testing is currently insufficient

### Enum ordering links to database
The two enums VisitStatus & EmployeeRole are saved to the respective objects that are found in.
The value that gets written to the database is the logical order (zero-based) of the declared
values in the enum.  IE - the first listed enum value is id=0 in the database.
Therefore, changing the order of the entries in the enum will necessarily change the interpretation
of values in the database.  Do not change the order after any data has been written.  
Adding new values to the end of the list is acceptable, and it will work going forward.

Another idea - could write the label of the enum to the table (db column=string?)
- need to test out this idea/fix
 

## Database scripts


### Add a new user to the db
Use this template to prepare a new user in the db.

Variables:
* {username}
* {password}
* {dbName}

```
create user {username} with encrypted password '{password}';
grant all privileges on database {dbName} to {username};
grant all privileges on all sequences in schema public to {username};
grant select, insert, update, delete on all tables in schema public to {username};
```

### Add a user to a role

Variables:
* {next employee_roles_id pk}
* {id of employee}
* {id of role}

```
INSERT INTO employee_roles(employee_roles_id, employee_id, employee_role_id) VALUES ({next employee_roles_id pk}, id of employee}, {id of role});
```



## Miscellaneous notes


### How to create a tag
Info here: https://git-scm.com/book/en/v2/Git-Basics-Tagging

```
$ git tag -a v0.1 -m "first tagged version"
$ git push origin v0.1
```


### How to build and deploy a new version
Run the `shadowJar` task to build the deployable

```
$ gradlew shadowJar
```

The updated uber-jar will be found at `build/libs/kpt-billing-manager-all.jar`

Upload the file to [here](https://github.com/codingdiscer/kpt-billing-manager/releases):




### Gradle commands that come with Griffon:

Output from a `$ gradlew -tasks` command:

```

------------------------------------------------------------
All tasks runnable from root project
------------------------------------------------------------

Application tasks
-----------------
createDistributionFiles
run - Runs this project as a JVM application
runShadow - Runs this project as a JVM application using the shadow jar
startShadowScripts - Creates OS specific scripts to run the project as a JVM application using the shadow jar

Build tasks
-----------
assemble - Assembles the outputs of this project.
build - Assembles and tests this project.
buildDependents - Assembles and tests this project and all projects that depend on it.
buildNeeded - Assembles and tests this project and all projects it depends on.
classes - Assembles main classes.
clean - Deletes the build directory.
functionalTestClasses - Assembles functional test classes.
integrationTestClasses - Assembles integration test classes.
jar - Assembles a jar archive containing the main classes.
sourceJar - An archive of the source code
stats - Counts source lines
testClasses - Assembles test classes.

Build Setup tasks
-----------------
init - Initializes a new Gradle build.
wrapper - Generates Gradle wrapper files.

Distribution tasks
------------------
assembleDist - Assembles the main distributions
assembleShadowDist - Assembles the shadow distributions
distTar - Bundles the project as a distribution.
distZip - Bundles the project as a distribution.
installDist - Installs the project as a distribution as-is.
installShadowDist - Installs the project as a distribution as-is.
shadowDistTar - Bundles the project as a distribution.
shadowDistZip - Bundles the project as a distribution.

Documentation tasks
-------------------
groovydoc - Generates Groovydoc API documentation for the main source code.
javadoc - Generates Javadoc API documentation for the main source code.

Help tasks
----------
buildEnvironment - Displays all buildscript dependencies declared in root project 'kpt-billing-manager'.
components - Displays the components produced by root project 'kpt-billing-manager'. [incubating]
dependencies - Displays all dependencies declared in root project 'kpt-billing-manager'.
dependencyInsight - Displays the insight into a specific dependency in root project 'kpt-billing-manager'.
dependencyUpdates - Displays the dependency updates for the project.
dependentComponents - Displays the dependent components of components in root project 'kpt-billing-manager'. [incubating]
help - Displays a help message.
model - Displays the configuration model of root project 'kpt-billing-manager'. [incubating]
projects - Displays the sub-projects of root project 'kpt-billing-manager'.
properties - Displays the properties of root project 'kpt-billing-manager'.
tasks - Displays the tasks runnable from root project 'kpt-billing-manager'.

IDE tasks
---------
cleanIdea - Cleans IDEA project files (IML, IPR)
idea - Generates IDEA project files (IML, IPR, IWS)

Installation tasks
------------------
izPackCreateInstaller - Creates an IzPack-based installer

JavaFX tasks
------------
jfxGenerateKeyStore - Create a Java keystore
jfxJar - Create executable JavaFX-jar
jfxListBundlers - List all possible bundlers available on this system, use '--info' parameter for detailed information
jfxNative - Create native JavaFX-bundle
jfxRun - Start generated JavaFX-jar

Reporting tasks
---------------
jacocoFunctionalTestReport - Generate Jacoco coverage reports after running functional tests.
jacocoIntegrationTestReport - Generate Jacoco coverage reports after running integration tests.
jacocoRootReport - Generate Jacoco coverage reports after running all tests.
jacocoTestReport - Generate Jacoco coverage reports after running tests.

Shadow tasks
------------
knows - Do you know who knows?
shadowJar - Create a combined JAR of project and runtime dependencies

Verification tasks
------------------
check - Runs all checks.
jacocoTestCoverageVerification - Verifies code coverage metrics based on specified rules for the test task.
test - Runs the unit tests.

Versioning tasks
----------------
versionDisplay - Writes version information on the standard output.
versionFile - Writes version information into a file.

```


### How to add a security group to the db

Get the IP address here - https://whatismyipaddress.com/

https://us-east-2.console.aws.amazon.com/ec2/v2/home?region=us-east-2#SecurityGroups:search=home;sort=group-id


https://us-east-2.console.aws.amazon.com/rds/home?region=us-east-2#dbinstance:id=kinespherept


