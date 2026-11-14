@echo off
cd /d "%~dp0"

echo [1/2] Checking MySQL Server...
tasklist /fi "imagename eq mysqld.exe" | findstr /i "mysqld.exe" >nul
if %ERRORLEVEL% neq 0 (
    echo Starting MySQL Server...
    start "" /b "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysqld.exe" --datadir="C:\ProgramData\MySQL\MySQL Server 8.4\Data"
    timeout /t 3 /nobreak >nul
) else (
    echo MySQL Server is already running.
)

echo [2/2] Launching University Academic System desktop interface...
start "" "C:\Program Files\Microsoft\jdk-17.0.21.21-hotspot\bin\javaw.exe" -cp "target\classes;C:\Users\SLIM 3\.m2\repository\org\mindrot\jbcrypt\0.4\jbcrypt-0.4.jar;C:\Users\SLIM 3\.m2\repository\com\formdev\flatlaf\3.4\flatlaf-3.4.jar;C:\Users\SLIM 3\.m2\repository\com\mysql\mysql-connector-j\8.3.0\mysql-connector-j-8.3.0.jar" com.university.Main
exit
