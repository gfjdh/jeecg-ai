echo 打包Docker容器...

docker build -t crpi-o08rhi2pxibxdenj.cn-beijing.personal.cr.aliyuncs.com/jeecg-gfjdh/jeecg-system:latest -f jeecg-boot/jeecg-module-system/jeecg-system-start/Dockerfile jeecg-boot/jeecg-module-system/jeecg-system-start/
docker build -t crpi-o08rhi2pxibxdenj.cn-beijing.personal.cr.aliyuncs.com/jeecg-gfjdh/jeecg-vue:latest -f jeecgboot-vue3/Dockerfile jeecgboot-vue3/
docker build -t crpi-o08rhi2pxibxdenj.cn-beijing.personal.cr.aliyuncs.com/jeecg-gfjdh/jeecg-mysql:latest -f docker/mysql/Dockerfile docker/mysql/

echo Docker容器打包完成！