@echo off
setlocal

call :FindReplace "async" " " eva\eva.js
call :FindReplace "await" " " eva\eva.js

type eva\eva.js eva\nuoCharts.js > eva\combined.js
java -jar yuicompressor-2.4.8.jar ./eva/combined.js -o ./eva/eva-min.js

call :FindReplace "function handleResponseFromEva" "async function handleResponseFromEva" eva\eva-min.js
call :FindReplace "sleep(pollingIntervalMilli)" "await sleep(pollingIntervalMilli)" eva\eva-min.js

pause
exit /b 

:FindReplace <findstr> <replstr> <file>
set tmp="%temp%\tmp.txt"
If not exist %temp%\_.vbs call :MakeReplace
for /f "tokens=*" %%a in ('dir "%3" /s /b /a-d /on') do (
  for /f "usebackq" %%b in (`Findstr /mic:"%~1" "%%a"`) do (
    echo(&Echo Replacing "%~1" with "%~2" in file %%~nxa
    <%%a cscript //nologo %temp%\_.vbs "%~1" "%~2">%tmp%
    if exist %tmp% move /Y %tmp% "%%~dpnxa">nul
		)
  )
)
del %temp%\_.vbs
exit /b

:MakeReplace
>%temp%\_.vbs echo with Wscript
>>%temp%\_.vbs echo set args=.arguments
>>%temp%\_.vbs echo .StdOut.Write _
>>%temp%\_.vbs echo Replace(.StdIn.ReadAll,args(0),args(1),1,-1,1)
>>%temp%\_.vbs echo end with
