@echo off
rem Demarre l'application PECCNSS (prise en charge medicale CNSS) en mode production.
rem Prerequis : Java 21 et une base PostgreSQL accessible.
chcp 65001 > nul
cd /d "%~dp0"

rem --- Base de donnees : ajustez ces valeurs a votre serveur ---
if not defined SPRING_DATASOURCE_URL set SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/Prisecharge
if not defined SPRING_DATASOURCE_USERNAME set SPRING_DATASOURCE_USERNAME=postgres
if not defined SPRING_DATASOURCE_PASSWORD set /p SPRING_DATASOURCE_PASSWORD=Mot de passe PostgreSQL :

rem --- Cle de signature des jetons de connexion (JWT) ---
rem Generee au premier lancement puis conservee dans jwt-secret.txt : la changer deconnecte tout le monde.
if not exist jwt-secret.txt powershell -NoProfile -Command "$b = New-Object byte[] 64; [Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($b); [Convert]::ToBase64String($b) | Set-Content -NoNewline -Encoding ascii jwt-secret.txt"
if not defined JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET set /p JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET=<jwt-secret.txt

rem --- Port HTTP de l'application ---
if not defined SERVER_PORT set SERVER_PORT=8080

echo.
echo Demarrage de PECCNSS sur http://localhost:%SERVER_PORT% ...
echo (fermez cette fenetre pour arreter l'application)
echo.
java -jar peccnss.jar
pause
