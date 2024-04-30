setlocal

CD /D "D:\NuoCanvas\SourceCode\nuo-frontend-eva\src\"

xcopy /s .\css .\dist\css\
xcopy /s .\lib .\dist\lib\
xcopy /s .\public .\dist\public\
copy .\index.html .\dist\

mkdir .\dist\js

type js\app.js js\eva.js > js\combined.js
uglifyjs ./js/combined.js -c -m -o ./dist/js/app.js

pause
exit /b