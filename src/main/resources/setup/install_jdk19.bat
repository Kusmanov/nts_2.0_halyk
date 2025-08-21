@echo off
SETLOCAL ENABLEDELAYEDEXPANSION

:: Указываем путь к установочному файлу
SET JDK_INSTALLER=jdk-19_windows-x64_bin.exe

:: Указываем каталог установки (можно изменить)
SET INSTALL_DIR="C:\Program Files\Java\jdk-19"

:: Запуск установщика в тихом режиме
%JDK_INSTALLER% /s INSTALLDIR=%INSTALL_DIR%

:: Добавление пути JDK в переменную окружения PATH
setx PATH "%INSTALL_DIR%\bin;%PATH%" /M

:: Проверка установки
java -version

echo JDK 19 sucessfully installed!
pause