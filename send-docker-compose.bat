@echo off
setlocal

:: 配置信息
set REGISTRY=crpi-o08rhi2pxibxdenj.cn-beijing.personal.cr.aliyuncs.com
set NAMESPACE=jeecg-gfjdh
set USERNAME=清水尚辉
set LOCAL_PREFIX=gfjdh

echo ========================================================
echo  Start pushing Docker images to Aliyun Registry
echo ========================================================

echo.
echo [Step 1] Logging in to Aliyun Registry...
set /p REGISTRY_PWD=Please enter password for %REGISTRY%: 
echo %REGISTRY_PWD% | docker login --username=%USERNAME% --password-stdin %REGISTRY%
if %errorlevel% neq 0 (
    echo Login failed!
    pause
    exit /b %errorlevel%
)

echo.
echo [Step 2] Pushing images...
echo 1/3 Pushing jeecg-mysql...
docker push %REGISTRY%/%NAMESPACE%/jeecg-mysql:latest

echo 2/3 Pushing jeecg-system...
docker push %REGISTRY%/%NAMESPACE%/jeecg-system:latest

echo 3/3 Pushing jeecg-vue...
docker push %REGISTRY%/%NAMESPACE%/jeecg-vue:latest

echo.
echo ========================================================
echo  Success! All images pushed.
echo ========================================================
pause
