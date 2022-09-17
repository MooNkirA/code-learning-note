# Postman 接口测试工具

> 官网地址：https://www.postman.com/

## 1. 简介

Postman 是一款功能强大的 HTTP 接口测试工具，可以完成各种 HTTP 请求的功能测试。

- post请求参数设置
    - form-data：将表单的数据转为键值对，并且可以包括文件
    - x-www-form-urlencoded: content-type为application/x-www-from-urlencoded，将表单的数据转为键值对
    - raw：请求text、json、xml、html，比如如果请求json数据则使用此格式
    - binary：content-type为application/octet-stream，可用于上传文件。

