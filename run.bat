@echo off
cd /d "%~dp0"

echo [1/3] Checking MySQL Server...
tasklist /fi "imagename eq mysqld.exe" | findstr /i "mysqld.exe" >nul
if %ERRORLEVEL% neq 0 (
    echo Starting MySQL Server...
    start "" /b "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysqld.exe" --datadir="C:\ProgramData\MySQL\MySQL Server 8.4\Data"
    timeout /t 3 /nobreak >nul
) else (
    echo MySQL Server is already running.
)

echo [2/3] Compiling Java source files...
if not exist "bin" mkdir bin
"C:\Program Files\Microsoft\jdk-17.0.21.21-hotspot\bin\javac.exe" -encoding UTF-8 -cp "lib/*" -d bin src\com\university\*.java src\com\university\ui\*.java

echo [3/3] Launching University Academic System Desktop Interface...
start "" "C:\Program Files\Microsoft\jdk-17.0.21.21-hotspot\bin\javaw.exe" -cp "bin;lib/*" com.university.Main
exit
