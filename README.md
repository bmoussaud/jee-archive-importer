Description
===========

A Deployit Importer plugpoint that allows plain EAR files to be imported as Deployment Packages.

Installation
============

Place the 'jee-archive-importer-<version>.jar' file into your SERVER_HOME/lib directory. 

If you are running a version of the Deployit server that does not yet address [DEPLOYITPB-2635](http://tech.xebialabs.com/jira/browse/DEPLOYITPB-2635), you will also need to copy the hotfix-DEPLOYITPB-2635-1.0.jar to your SERVER_HOME/hotfix directory. Create the JAR by running 'gradle build' from this project's 'hotfix-DEPLOYITPB-2635' directory.

The configuration file 'jee-archive-importer.properties' should be created/placed in SERVER_HOME/conf.

Configuration
=============

The main task of the importer is to derive, on the basis of the EAR or WAR file, the application name and version for the deployment package that will be created.

The default option (and fallback) is to attempt to extract the application name and version from the archive file name itself. This is controlled by the

# '<ext>' is 'ear' or 'war'
jee-archive-importer.<ext>.nameVersionRegex

property - if the name matches, the first matching group becomes the name and the second, if found, the version. If only the name can be matched the

jee-archive-importer.<ext>.defaultVersion

property determines the version given to the deployment package. If the property

jee-archive-importer.<ext>.scanManifest=true

is set, the importer will - before falling back to the file name - attempt to extract the application name and version from the archive's manifest. The properties

jee-archive-importer.<ext>.nameManifestAttribute
jee-archive-importer.<ext>.versionManifestAttribute

determine the manifest attributes that should be read to determine the name (resp. version) of the deployment package.

Examples
--------

See https://github.com/demobox/jee-archive-importer/blob/master/src/test/resources/jee-archive-importer.properties for examples.