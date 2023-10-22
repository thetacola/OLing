echo What is the version of OLing to bundle?
read version
echo Bundling $version...
cd target
jar -cvf oling-$version-bundle.jar oling-$version.pom oling-$version.pom.asc oling-$version.jar oling-$version.jar.asc oling-$version-javadoc.jar oling-$version-javadoc.jar.asc oling-$version-sources.jar oling-$version-sources.jar.asc
