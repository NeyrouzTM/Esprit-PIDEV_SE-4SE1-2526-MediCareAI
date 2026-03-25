#!/usr/bin/env powershell
$ErrorActionPreference = "Continue"
Set-Location "C:\Users\NAYROUZ\Desktop\projetpi\Medicare_Ai"
& ".\mvnw.cmd" spring-boot:run 2>&1 | Tee-Object -FilePath ".\app-output.log"

