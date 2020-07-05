# Imagic
一款github图片上传并返回jsdelivr加速链接的小工具

## Content
- [预览](#Screenshots)
- [配置](#Setting)
- [更新历史](#Change Log)
- [联系我](#Contact)
- [MIT License](#MIT License)


## Screenshots

 ![](https://cdn.jsdelivr.net/gh/fushaolei/imguploadtest/20200704181945.png)|![](https://cdn.jsdelivr.net/gh/fushaolei/imguploadtest/20200704182125.png)
-----------------------------------------------|---------------------------------------------------------------------
| ![](https://cdn.jsdelivr.net/gh/fushaolei/imguploadtest/20200704182432.png) |



![](https://cdn.jsdelivr.net/gh/fushaolei/ImagitcTest/test.gif)
## Setting
![](https://cdn.jsdelivr.net/gh/fushaolei/imguploadtest/20200704182547.png)

你需要配置以下内容

- 用户名
- 密码
- github的仓库名
- 本地的仓库 （绝对路径，需要有.git，注意：不能在C盘里）

> 示例
> 用户名：example
> 密码：example123
> 本地仓库：F:/source/images
> 远程仓库：git@github.com:example/example.git



这么做后Imagic会再本地生成一个json文件用于存储你配置的内容

## Change Log

### 2020/07/03
- ✅完成依据位置信息上传功能（依据JGit）

### 2020/07/04
- ✅完成配置功能
- ✅完成拖拽上传功能
- ✅发布0.1版本

### 2020/07/05
- ✅添加了清除本地的用户数据的功能
- 🐛填写github远程连接改成了填写github仓库名
- 🐛修改了出现错误没有提示

## Contact
- 1563250958@qq.com

## MIT License

Copyright (c) 2020 任我行
