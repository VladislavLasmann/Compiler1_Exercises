@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  mavlc startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Add default JVM options here. You can also use JAVA_OPTS and MAVLC_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*

:execute
@rem Setup the command line

set CLASSPATH=\home\seed\Workspace\TuDarmstadt\EiCb\Compiler1_Exercises\Praktikum2\Vladi\eicb-p2-2017.1\bin;\home\seed\Workspace\TuDarmstadt\EiCb\Compiler1_Exercises\Praktikum2\Vladi\eicb-p2-2017.1\build\classes\java\main;\home\seed\.gradle\caches\modules-2\files-2.1\commons-cli\commons-cli\1.3.1\1303efbc4b181e5a58bf2e967dc156a3132b97c0\commons-cli-1.3.1.jar;\home\seed\.gradle\caches\modules-2\files-2.1\com.thoughtworks.xstream\xstream\1.4.9\c43f6e6bfa79b56e04a8898a923c3cf7144dd460\xstream-1.4.9.jar;\home\seed\.gradle\caches\modules-2\files-2.1\org.xmlunit\xmlunit-matchers\2.2.1\26c2afc03381d88ffa32ef2351c69e8ae90c3a8c\xmlunit-matchers-2.2.1.jar;\home\seed\.gradle\caches\modules-2\files-2.1\org.xmlunit\xmlunit-core\2.2.1\1f3c89bdf2800ab830a0b556c883ecf99083460c\xmlunit-core-2.2.1.jar;\home\seed\.gradle\caches\modules-2\files-2.1\xmlpull\xmlpull\1.1.3.1\2b8e230d2ab644e4ecaa94db7cdedbc40c805dfa\xmlpull-1.1.3.1.jar;\home\seed\.gradle\caches\modules-2\files-2.1\xpp3\xpp3_min\1.1.4c\19d4e90b43059058f6e056f794f0ea4030d60b86\xpp3_min-1.1.4c.jar;\home\seed\.gradle\caches\modules-2\files-2.1\org.hamcrest\hamcrest-core\1.3\42a25dc3219429f0e5d060061f71acb49bf010a0\hamcrest-core-1.3.jar

@rem Execute mavlc
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %MAVLC_OPTS%  -classpath "%CLASSPATH%" mavlc.Main %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable MAVLC_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%MAVLC_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
