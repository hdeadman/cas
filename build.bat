
@rem By default this just builds and doesn't run tests, javadoc, checkstyle, etc
@rem To install in local maven repo for use by overlay, run "build install"
@rem To run tests and checkstyle, run "build test checkstyle"
@rem Basically you pass in the thing you want the build to do in addition to building
@rem Options are: test check checkstyle findbugs offline clean javadoc install

set TEST=-x test
set CHECK=-x check
set FINDBUGS=-DskipFindbugs=true
set CHECKSTYLE=-DskipCheckstyle=true
set OFFLINE=
set CLEAN=
set JAVADOC=-x javadoc
set BUILD=build
set BOOTIFUL=-DskipBootifulArtifact=true
set CACHE=--build-cache
set PARALLEL=--parallel


:loop
@if "%1" == "test" set TEST=
@if "%1" == "check" set CHECK=
@if "%1" == "checkstyle" set CHECKSTYLE=
@if "%1" == "findbugs" set FINDBUGS=
@if "%1" == "offline" set OFFLINE=--offline
@if "%1" == "clean" set CLEAN=clean
@if "%1" == "javadoc" set JAVADOC=
@if "%1" == "install" set BUILD=install
@if "%1" == "bootiful" set BOOTIFUL=
@if "%1" == "noparallel" set PARALLEL=
@if "%1" == "nocache" set CACHE=
@if "%1" == "" goto argsdone
@shift
@goto loop
:argsdone

@rem skip npm lint due to line feed issues
set WINDOWS_ISSUES=-DskipNpmLint=true -DskipNodeModulesCleanUp=true -DskipNpmCache=true -DskipNpmLint=true

@echo call gradlew.bat %CLEAN% %BUILD% %CACHE% %PARALLEL% %BOOTIFUL% %TEST% %JAVADOC% %CHECK% %CHECKSTYLE% %FINDBUGS% %OFFLINE% %WINDOWS_ISSUES%
call gradlew.bat %CLEAN% %BUILD% %CACHE% %PARALLEL% %BOOTIFUL% %TEST% %JAVADOC% %CHECK% %CHECKSTYLE% %FINDBUGS% %OFFLINE% %WINDOWS_ISSUES%