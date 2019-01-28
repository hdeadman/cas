if "%CAS_WORKSPACE%" == "" set CAS_WORKSPACE=%USERPROFILE%\git\cas
if "%CAS_SERVER_TYPE%" == "" set CAS_SERVER_TYPE=tomcat
@cls
@echo Using %CAS_WORKSPACE%
if "%1" == "" set DEBUG_SUSPEND=false
if "%1" == "true" set DEBUG_SUSPEND=true
@pushd %CAS_WORKSPACE%\webapp\cas-server-webapp-%CAS_SERVER_TYPE% && @call %CAS_WORKSPACE%\gradlew.bat build install bootRun --configure-on-demand --build-cache --parallel -x test -x javadoc -x check -Djava.net.preferIPv4Stack=true -DenableRemoteDebugging=true -DremoteDebuggingSuspend=%DEBUG_SUSPEND% --stacktrace -DskipNestedConfigMetadataGen=true -DskipGradleLint=true -DskipSass=true -DskipNodeModulesCleanUp=true -DskipNpmCache=true -DskipNpmLint=true -Dlog.dir=c:/etc/cas/logs/
@popd