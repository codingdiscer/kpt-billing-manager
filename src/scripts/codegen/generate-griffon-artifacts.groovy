/**
 * this script generates the MVC-group and appropriate code for an artifact declared in griffonartifact.properties
 */

// load the properties
Properties properties = new Properties()
properties.load(new FileInputStream(new File('./griffonartifact.properties')))

// prepare a common variable
String packageName = properties['packageName']
String packagePath = '/' + packageName.replaceAll('\\.', '/')


// controller
buildArtifact(
        packageName,                // packageName,
        packagePath,                // packagePath,
        properties['artifactName'] as String,     // artifactName
        properties['controllerPath'] as String,     // artifactPath
        properties['controllerTemplate'] as String, // artifactTemplate
        (properties['artifactName']  as String) + 'Controller.groovy'    // shortFileName
)

// model
buildArtifact(
        packageName,                // packageName,
        packagePath,                // packagePath,
        properties['artifactName'] as String,     // artifactName
        properties['modelPath'] as String,     // artifactPath
        properties['modelTemplate'] as String, // artifactTemplate
        (properties['artifactName']  as String) + 'Model.groovy'    // shortFileName
)

// view
buildArtifact(
        packageName,                // packageName,
        packagePath,                // packagePath,
        properties['artifactName'] as String,     // artifactName
        properties['viewPath'] as String,     // artifactPath
        properties['viewTemplate'] as String, // artifactTemplate
        (properties['artifactName']  as String) + 'View.groovy'    // shortFileName
)

// fxml
buildArtifact(
        packageName,                // packageName,
        packagePath,                // packagePath,
        properties['artifactName'] as String,     // artifactName
        properties['fxmlPath'] as String,     // artifactPath
        properties['fxmlTemplate'] as String, // artifactTemplate
        (properties['artifactName'] as String).substring(0, 1).toLowerCase() + (properties['artifactName'] as String).substring(1) + '.fxml'    // shortFileName
)

// performs the work of preparing the file, checking for existence (log a warn and do nothing when it exists,
// otherwise create the new file and populate it with the applied template data
void buildArtifact(String packageName, String packagePath, String artifactName, String artifactPath, String artifactTemplate, String shortFileName) {
    File fullFile = new File(artifactPath + packagePath + '/' + shortFileName)
    if(!fullFile.exists()) {
        fullFile.createNewFile()
        fullFile.text = new File(artifactTemplate).text
                .replaceAll('\\{package\\}', packageName)
                .replaceAll('\\{artifact\\}', artifactName)
                .replaceAll('\\{ARTIFACT\\}', artifactName.toUpperCase())
        println "INFO - created file :: ${fullFile} "
    } else {
        println "WARN - fxml file already exists, so no action will take place :: ${fullFile}"
    }
}

// prepare the text to add to the `griffon-app/conf/Config.groovy`
println "***************  Add to Config.groovy  ***************\n"
println "    '${(properties['artifactName'] as String).substring(0, 1).toLowerCase() + (properties['artifactName'] as String).substring(1)}' {"
println "        model      = '${packageName}.${properties['artifactName']}Model'"
println "        view       = '${packageName}.${properties['artifactName']}View'"
println "        controller = '${packageName}.${properties['artifactName']}Controller'"
println "    }"

