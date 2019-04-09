@echo off
cd /d %~dp0

if exist %~dp0jre (
	set jre_home=%~dp0jre
)

set path="%jre_home%\bin"

set mainjar=doctools-1.0-SNAPSHOT.jar
set cp=
setlocal enabledelayedexpansion
for /F %%i in ('dir /s /b lib\*.jar')  do set cp=%%i;!cp!
setlocal disabledelayedexpansion

set cp=%cp%;%mainjar%


for /F %%i in ('dir /b *.jar')  do (
	if not "%%i"=="%mainjar%" del /q /f  %%i
)
if exist %~dp0start.bat del /q /f %~dp0start.bat

start javaw -cp "%cp%" org.jim.doctools.ui.App --base WebContent --port 9999
