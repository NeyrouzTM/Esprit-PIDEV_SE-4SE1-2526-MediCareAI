#!/usr/bin/env powershell
$ErrorActionPreference = "Continue"
Set-Location "C:\Users\sahli\projets\Medicare_Ai\Medicare_Ai"
& ".\mvnw.cmd" spring-boot:run 2>&1 | Tee-Object -FilePath ".\app-output.log"
