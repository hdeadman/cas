if "%CAS_WORKSPACE%" == "" set CAS_WORKSPACE=%USERPROFILE%\git\cas
@cls
@echo Using %CAS_WORKSPACE%
set DEBUG_SUSPEND=true
@pushd %CAS_WORKSPACE%\support\cas-server-support-shell && @call %CAS_WORKSPACE%\gradlew.bat build install bootRun --configure-on-demand --build-cache --parallel -x test -x javadoc -x check -Djava.net.preferIPv4Stack=true -DenableRemoteDebugging=true -DremoteDebuggingSuspend=%DEBUG_SUSPEND% --stacktrace -DskipNestedConfigMetadataGen=true -DskipGradleLint=true -DskipSass=true -DskipNodeModulesCleanUp=true -DskipNpmCache=true -DskipNpmLint=true
@popd