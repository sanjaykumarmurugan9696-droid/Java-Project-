@echo off
echo ========================================================
echo Starting FeedbackFlow - Course Feedback Collection System
echo ========================================================
echo.
cd /d "%~dp0"
call mvn spring-boot:run
pause
