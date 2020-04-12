# Day08 课程图片管理 & 分布式文件系统

## 1. FastDFS 研究

- 参考资料：编程资料笔记\【07】分布式架构 & 微服务架构\05-FastDFS 分布式文件系统.md
- 测试代码：xc-edu-project-service 工程的 test-fastdfs 模块

## 2. 上传图片功能开发
### 2.1. 需求分析

在很多系统都有上传图片/上传文件的需求，比如：上传课程图片、上传课程资料、上传用户头像等，为了提供系统的可重用性专门设立文件系统服务承担图片/文件的管理，文件系统服务实现对文件的上传、删除、查询等功能进行管理。

各个子系统不再开发上传文件的请求，各各子系统通过文件系统服务进行文件的上传、删除等操作。文件系统服务最终会将文件存储到fastDSF文件系统中。

下图是各个子系统与文件系统服务之间的关系

![子系统与文件系统服务关系图](images/20190805185255973_16113.png)

下图是课程管理中上传图片处理流程

![课程管理中上传图片处理流程图](images/20190805185729735_14231.png)

- 执行流程如下：
    1. 管理员进入教学管理前端，点击上传图片
    2. 图片上传至文件系统服务，文件系统请求fastDFS上传文件
    3. 文件系统将文件入库，存储到文件系统服务数据库中。
    4. 文件系统服务向前端返回文件上传结果，如果成功则包括文件的Url路径。
    5. 课程管理前端请求课程管理进行保存课程图片信息到课程数据库。
    6. 课程管理服务将课程图片保存在课程数据库。

### 2.2. 创建文件系统服务工程

导入本项目提供的资料，xc-service-base-filesystem.zip工程

1. 工程目录结构

![工程目录结构](images/20190805191148029_28414.png)

pom.xml配置相关依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>xc-framework-parent</artifactId>
        <groupId>com.xuecheng</groupId>
        <version>1.0-SNAPSHOT</version>
        <relativePath>../xc-framework-parent/pom.xml</relativePath>
    </parent>
    <modelVersion>4.0.0</modelVersion>
    <artifactId>xc-service-base-filesystem</artifactId>

    <dependencies>
        <dependency>
            <groupId>com.xuecheng</groupId>
            <artifactId>xc-service-api</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>com.xuecheng</groupId>
            <artifactId>xc-framework-model</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>com.xuecheng</groupId>
            <artifactId>xc-framework-common</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>net.oschina.zcx7878</groupId>
            <artifactId>fastdfs-client-java</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-io</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-mongodb</artifactId>
        </dependency>
    </dependencies>

</project>
```

2. 配置文件，原测试程序中fastdfs-client.properties的配置信息统一放在application.yml

```yml
server:
  port: 22100
spring:
  application:
    name: xc-service-base-filesystem
  # mongo配置
  data:
    mongodb:
      database: xc_fs
      uri: mongodb://root:123@127.0.0.1:27017
  # SpringMVC上传文件配置
  servlet:
    multipart:
      # 默认支持文件上传.
      enabled: true
      # 支持文件写入磁盘.
      file-size-threshold: 0
      # 上传文件的临时目录
      location:
      # 最大支持文件大小
      max-file-size: 1MB
      # 最大支持请求大小
      max-request-size: 30MB
xuecheng:
  fastdfs:
    connect_timeout_in_seconds: 5
    network_timeout_in_seconds: 30
    charset: UTF-8
    tracker_servers: 192.168.12.132:22122 # 多个 trackerServer中间以逗号分隔
```

### 2.3. 后端API接口
#### 2.3.1. 定义数据模型类

1. 系统的文件信息（图片、文档等小文件的信息）在mongodb中存储，在xc-framework-model工程创建文件信息的模型类

```java
@Data
@ToString
@Document(collection = "filesystem")
public class FileSystem {
    @Id
    private String fileId;
    // 文件请求路径
    private String filePath;
    // 文件大小
    private long fileSize;
    // 文件名称
    private String fileName;
    // 文件类型
    private String fileType;
    // 图片宽度
    private int fileWidth;
    // 图片高度
    private int fileHeight;
    // 用户id，用于授权
    private String userId;
    // 业务key
    private String businesskey;
    // 业务标签
    private String filetag;
    // 文件元信息
    private Map metadata;
}
```

- 模型类属性说明：
    - fileId：fastDFS返回的文件ID。
    - filePath：请求fastDFS浏览文件URL。
    - filetag：文件标签，由于文件系统服务是公共服务，文件系统服务会为使用文件系统服务的子系统分配文件标签，用于标识此文件来自哪个系统。
    - businesskey：文件系统服务为其它子系统提供的一个业务标识字段，各子系统根据自己的需求去使用，比如：课程管理会在此字段中存储课程id用于标识该图片属于哪个课程。
    - metadata：文件相关的元信息。
2. 在mongodb创建数据库xc_fs（文件系统数据库），并创建（collection）集合filesystem。

#### 2.3.2. Api接口

在xc-service-api工程下创建com.xuecheng.api.filesystem.FileSystemControllerApi接口

```java
package com.xuecheng.api.filesystem;

import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件系统服务接口
 */
@Api(value = "文件管理接口", description = "文件管理接口，提供文件的增、删、改、查")
public interface FileSystemControllerApi {
    /**
     * 上传文件
     *
     * @param multipartFile 文件
     * @param filetag       文件标签
     * @param businesskey   业务key
     * @param metadata      元信息,json格式
     * @return 响应标识
     */
    @ApiOperation("上传文件接口")
    public UploadFileResult upload(MultipartFile multipartFile, String filetag,
                                   String businesskey, String metadata);
}
```

#### 2.3.3. dao 层

在xc-service-base-filesystem工程，定义FileSystemRepository接，将文件信息存入数据库，主要存储文件系统中的文件路径

```java
package com.xuecheng.filesystem.dao;

import com.xuecheng.framework.domain.filesystem.FileSystem;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * 文件系统接口
 */
public interface FileSystemRepository extends MongoRepository<FileSystem, String> {
}
```

#### 2.3.4. service 层

创建com.xuecheng.filesystem.service.FileSystemService类，增加上传文件的方法

```java
package com.xuecheng.filesystem.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.filesystem.dao.FileSystemRepository;
import com.xuecheng.framework.domain.filesystem.FileSystem;
import com.xuecheng.framework.domain.filesystem.response.FileSystemCode;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import org.apache.commons.lang3.StringUtils;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 文件系统业务逻辑层
 */
@Service
public class FileSystemService {
    /* 注入配置文件数据 */
    @Value("${xuecheng.fastdfs.tracker_servers}")
    private String tracker_servers;
    @Value("${xuecheng.fastdfs.connect_timeout_in_seconds}")
    private int connect_timeout_in_seconds;
    @Value("${xuecheng.fastdfs.network_timeout_in_seconds}")
    private int network_timeout_in_seconds;
    @Value("${xuecheng.fastdfs.charset}")
    private String charset;

    /* 注入文件服务数据层接口 */
    @Autowired
    private FileSystemRepository fileSystemRepository;

    /**
     * 初始化fastDFS环境
     */
    private void initFastDfsConfig() {
        try {
            // 初始化tracker服务地址（多个tracker中间以半角逗号分隔）
            ClientGlobal.initByTrackers(this.tracker_servers);
            ClientGlobal.setG_charset(this.charset);
            ClientGlobal.setG_network_timeout(this.network_timeout_in_seconds);
            ClientGlobal.setG_connect_timeout(this.connect_timeout_in_seconds);
        } catch (Exception e) {
            e.printStackTrace();
            // 抛出异常
            ExceptionCast.cast(FileSystemCode.FS_INITFDFSERROR);
        }
    }

    /**
     * 上传文件
     *
     * @param multipartFile 文件对象
     * @param filetag       文件标签
     * @param businesskey   业务key
     * @param metadata      元信息,json格式
     * @return 响应标识
     */
    public UploadFileResult upload(MultipartFile multipartFile, String filetag,
                                   String businesskey, String metadata) {
        if (multipartFile == null) {
            // 抛出上传文件为空的异常
            ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_FILEISNULL);
        }

        // 第一步：将文件上传到fastDFS中，得到一个文件id
        String fileId = this.fdfs_upload(multipartFile);
        if (StringUtils.isBlank(fileId)) {
            // 返回的文件id为空，抛出上传异常
            ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_SERVERFAIL);
        }

        // 第二步：将文件id及其它文件信息存储到mongodb中
        // 创建文件信息对象
        FileSystem fileSystem = new FileSystem();
        // 设置文件id
        fileSystem.setFileId(fileId);
        // 设置文件在文件系统中的路径
        fileSystem.setFilePath(fileId);
        // 设置业务标识
        fileSystem.setBusinesskey(businesskey);
        // 设置标签
        fileSystem.setFiletag(filetag);
        // 设置元数据
        if (StringUtils.isNoneBlank(metadata)) {
            try {
                Map metadataMap = JSON.parseObject(metadata, Map.class);
                fileSystem.setMetadata(metadataMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 设置名称
        fileSystem.setFileName(multipartFile.getOriginalFilename());
        // 设置大小
        fileSystem.setFileSize(multipartFile.getSize());
        // 设置文件类型
        fileSystem.setFileType(multipartFile.getContentType());

        // 调用数据访问层的接口，保存数据到mongoDB
        fileSystemRepository.save(fileSystem);
        return new UploadFileResult(CommonCode.SUCCESS, fileSystem);
    }

    /**
     * 上传文件到fastDFS，返回文件id
     *
     * @param multipartFile 文件对象
     * @return 文件id
     */
    private String fdfs_upload(MultipartFile multipartFile) {
        try {
            // 1. 初始化fastDFS的环境
            initFastDfsConfig();
            // 2. 创建TrackerClient，用于请求TrackerServer
            TrackerClient trackerClient = new TrackerClient();
            // 3. 获取TrackerServer服务连接
            TrackerServer trackerServer = trackerClient.getConnection();
            // 4. 获取Stroage服务器
            StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
            // 5. 创建stroageClient，一个storage存储客户端用于上传文件
            StorageClient1 storageClient1 = new StorageClient1(trackerServer, storageServer);

            // 6. 上传文件
            // 6.1 获取上传文件的字节
            byte[] fileBytes = multipartFile.getBytes();
            // 6.2 获取文件的原始名称
            String originalFilename = multipartFile.getOriginalFilename();
            // 6.3 获取文件扩展名
            String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            // 6.4 上传文件，返回文件id
            return storageClient1.upload_file1(fileBytes, suffix, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
```

#### 2.3.5. Controller 层

创建com.xuecheng.filesystem.controller.FileSystemController类，实现文件系统服务接口FileSystemControllerApi

```java
package com.xuecheng.filesystem.controller;

import com.xuecheng.api.filesystem.FileSystemControllerApi;
import com.xuecheng.filesystem.service.FileSystemService;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件系统服务控制层
 */
@RestController
@RequestMapping("/filesystem")
public class FileSystemController implements FileSystemControllerApi {
    /* 注入文件系统业务逻辑层 */
    @Autowired
    private FileSystemService fileSystemService;

    /**
     * 上传文件
     *
     * @param multipartFile 文件
     * @param filetag       文件标签
     * @param businesskey   业务key
     * @param metadata      元信息,json格式
     * @return 响应标识
     */
    @Override
    @PostMapping("/upload")
    public UploadFileResult upload(@RequestParam("file") MultipartFile multipartFile,
                                   @RequestParam("filetag") String filetag, // required属性默认是true，可以不指定值
                                   @RequestParam(value = "businesskey", required = false) String businesskey,
                                   @RequestParam(value = "metedata", required = false) String metadata) {
        return fileSystemService.upload(multipartFile, filetag, businesskey, metadata);
    }
}
```

#### 2.3.6. 测试

- swagger-ui：http://127.0.0.1:22100/swagger-ui.html
- postman：http://127.0.0.1:22100/filesystem/upload

### 2.4. 上传课程图片前端
#### 2.4.1. 需求

上传图片界面如下图：点击“加号”上传图片，图片上传成功自动显示；点击“删除”将删除图片

![前端页面](images/20190806125609982_19479.png)

#### 2.4.2. 页面

1. 创建src\module\course\page\course_manage\course_picture.vue，使用Element-UI的Upload上传组件实现需求的效果

```html
<el-upload
  action="/filesystem/upload"
  list-type="picture-card"
  :before-upload="setbusinesskey"
  :on-success="handleSuccess"
  :file-list="fileList"
  :limit="picmax"
  :on-exceed="rejectupload"
  :data="uploadval">
  <i class="el-icon-plus"></i>
</el-upload>
```

- el-upload参数说明：
    - action：必选参数，上传的地址
    - list-type：文件列表的类型（text/picture/picture-card）
    - before-upload：上传前执行钩子方法 ，function(file)
    - on-success：上传成功 执行的钩子方法 ，function(response, file, fileList)
    - on-error：上传失败的钩子方法，function(err, file, fileList)
    - on-remove：文件删除的钩子方法，function(file, fileList)
    - file-list：文件列表，此列表为上传成功 的文件
    - limit：最大允许上传个数
    - on-exceed：文件超出个数限制时的钩子，方法为：function(files, fileList)
    - data：提交上传的额外参数，需要封装为json对象，最终提交给服务端为key/value串

2. 定义数据模型封装数据

```js
import * as sysConfig from '@/../config/sysConfig';
import * as courseApi from '../../api/course';
import utilApi from '../../../../common/utils';
import * as systemApi from '../../../../base/api/system';
export default {
  data() {
    return {
      picmax: 1, // 最大上传文件的数量
      courseid: '',
      dialogImageUrl: '',
      dialogVisible: false,
      fileList: [],
      uploadval: { filetag: "course" }, // 上传提交的额外的数据 ，将uploadval转成key/value提交给服务器
      imgUrl: sysConfig.imgUrl
    }
  },
  methods: {
    // 超出文件上传个数提示信息
    rejectupload() {
      this.$message.error("最多上传" + this.picmax + "个图片");
    },
    // 在上传前设置上传请求的数据
    setuploaddata() {

    },
    // 删除图片
    handleRemove(file, fileList) {
      console.log(file)
    },
    // 上传成功的钩子方法
    handleSuccess(response, file, fileList) {
      console.log(response)
    },
    // 上传失败执行的钩子方法
    handleError(err, file, fileList) {
      this.$message.error('上传失败');
      // 清空文件队列
      this.fileList = []
    },
    /**
     * promise 有三种状态:
     *  进行中pending
     *  执行成功 resolve
     *  执行失败 reject
     */
    testPromise(i) {
      return new Promise((resolve, reject) => {
        if (i < 2) {
          // 成功了
          resolve('成功了');
        } else {
          // 失败了
          reject('失败了');
        }
      })
    }
  },
  mounted() {
    // 课程id
    this.courseid = this.$route.params.courseid;
    // 测试调用promise方法，then中写的成功后的回调方法
    /* this.testPromise(3).then(res => {
      alert(res)
    }).catch(res => {//catch就是执行失败的回调方法
      alert("失败了。。。。。")
      alert(res)
    }) */
  }
}
```

#### 2.4.3. 测试，点击“加号”测试上传图片

## 3. 保存课程图片
### 3.1. 需求分析

图片上传到文件系统后，其它子系统如果想使用图片可以引用图片的地址，课程管理模块使用图片的方式是将图片地址保存到课程数据库中

业务流程如下：

1. 上传图片到文件系统服务
2. 保存图片地址到课程管理服务，在课程管理服务创建保存课程与图片对应关系的表course_pic
3. 保存图片成功后，参过course_pic表数据可查询某个课程图片信息

### 3.2. 课程管理服务端开发
#### 3.2.1. API接口

课程管理需要使用图片，在xc-service-api工程的课程管理服务中CourseControllerApi接口要提供保存课程图片的api方法

```java
@ApiOperation("添加课程图片")
public ResponseResult addCoursePic(String courseId, String pic);
```

#### 3.2.2. dao 层

- 定义模型封装数据

```java
@Data
@ToString
@Entity
@Table(name = "course_pic")
@GenericGenerator(name = "jpa-assigned", strategy = "assigned")
public class CoursePic implements Serializable {
    private static final long serialVersionUID = -916357110051689486L;
    @Id
    @GeneratedValue(generator = "jpa-assigned")
    private String courseid;
    private String pic;
}
```

- 在xc-service-manage-course工程，创建课程图片dao接口

```java
package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.CoursePic;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 课程图片数据接口
 */
public interface CoursePicRepository extends JpaRepository<CoursePic, String> {
}
```

#### 3.2.3. Service 层

修改CourseService类，创建添加课程图片方法

```java
/* 注入课程图片JPA接口 */
@Autowired
private CoursePicRepository coursePicRepository;

/**
 * 向课程管理数据表添加课程与图片的关联信息
 *
 * @param courseId 课程id
 * @param pic      图片地址
 * @return 响应标识
 */
@Transactional
public ResponseResult addCoursePic(String courseId, String pic) {
    // 根据id查询课程图片
    CoursePic coursePic = null;
    Optional<CoursePic> picOptional = coursePicRepository.findById(courseId);

    if (picOptional.isPresent()) {
        // 查询到课程图片数据
        coursePic = picOptional.get();
    } else {
        // 没有课程图片则新建对象
        coursePic = new CoursePic();
    }

    // 设置新的课程id与图片地址
    coursePic.setPic(pic);
    coursePic.setCourseid(courseId);

    // 调用数据访问接口，新增/修改课程图片数据
    coursePicRepository.save(coursePic);
    return ResponseResult.SUCCESS();
}
```

#### 3.2.4. Controller 层

修改CourseController类，实现新增图片addCoursePic方法

```java
/**
 * 添加课程图片
 *
 * @param courseId 课程id
 * @param pic      图片地址
 * @return 响应结果
 */
@Override
@PostMapping("/coursepic/add")
public ResponseResult addCoursePic(@RequestParam("courseId") String courseId,
                                   @RequestParam("pic") String pic) {
    // 调用业务逻辑层保存图片信息
    return courseService.addCoursePic(courseId, pic);
}
```

### 3.3. 保存图片前端开发

前端需要在上传图片成功后保存课程图片信息

#### 3.3.1. 前端Api方法

修改course.js，增加保存课程图片地址数据方法

```js
/* 保存课程图片地址到课程数据库 */
export const addCoursePic = (courseId, pic) => {
  return http.requestPost(apiUrl + '/course/coursepic/add?courseId=' + courseId + "&pic=" + pic)
}
```

#### 3.3.2. 页面代码

1. 添加上传成功的钩子方法`:on-success="handleSuccess"`
2. 在钩子方法中保存课程图片信息，如果保存图片失败则上传失败，清除文件列表

```js
// 上传成功的钩子方法
handleSuccess(response, file, fileList) {
  console.log(response)
  // 调用课程管理的保存图片接口，将图片信息保存到课程管理数据库course_pic中
  // 从response得到新的图片文件的地址
  if (response.success) {
    let fileId = response.fileSystem.fileId;
    courseApi.addCoursePic(this.courseid, fileId).then(res => {
      if (res.success) {
        this.$message.success("上传图片")
      } else {
        this.$message.error(res.message)
      }
    })
  }
},
// 上传失败执行的钩子方法
handleError(err, file, fileList) {
  this.$message.error('上传失败');
  // 清空文件队列
  this.fileList = []
},
```

## 4. 课程图片查询
### 4.1. 需求分析

课程图片上传成功，再次进入课程上传页面应该显示出来已上传的图片

### 4.2. API 接口

在课程管理服务的CourseControllerApi定义查询接口方法

```java
@ApiOperation("查询课程图片")
public CoursePic findCoursePic(String courseId);
```

### 4.3. 课程管理服务商开发
#### 4.3.1. dao 层

直接使用CoursePicRepository接口

#### 4.3.2. Service 层

修改CourseService类，定义根据课程id查询课程图片方法

```java
/**
 * 查询课程图片
 *
 * @param courseId 课程id
 * @return 课程图片对象
 */
public CoursePic findCoursePic(String courseId) {
    // 调用课程图片JPA接口查询
    Optional<CoursePic> picOptional = coursePicRepository.findById(courseId);
    if (picOptional.isPresent()) {
        return picOptional.get();
    }
    return null;
}
```

#### 4.3.3. Controller 层

修改CourseController类，实现查询课程图片findCoursePic方法

```java
/**
 * 查询课程图片
 *
 * @param courseId 课程id
 * @return 课程图片对象
 */
@Override
@GetMapping("/coursepic/list/{courseId}")
public CoursePic findCoursePic(@PathVariable("courseId") String courseId) {
    return courseService.findCoursePic(courseId);
}
```

### 4.4. 查询图片前端开发
#### 4.4.1. 定义API方法

修改course.js，增加技术要求课程图片方法

```js
/* 查询课程图片 */
export const findCoursePicList = courseId => {
  return http.requestQuickGet(apiUrl + '/course/coursepic/list/' + courseId)
}
```

#### 4.4.2. 页面代码

在课程图片页面的mounted钩子方法中查询课程图片信息，并将图片地址赋值给数据对象

1. 定义图片查询方法

```js
// 查询图片
list() {
  courseApi.findCoursePicList(this.courseid).then(res => {
    if (res && res.pic) {
      const url = this.imgUrl + res.pic
      const fileId = res.courseid
      // 先清空文件列表，再将图片放入文件列表
      this.fileList = []
      // 将图片地址设置到
      this.fileList.push({ name: 'pic', url, fileId })
    }
  })
},
```

2. 在mounted钩子方法中调用服务端查询文件列表并绑定到数据对象

```js
ounted() {
  // 课程id
  this.courseid = this.$route.params.courseid
  // 查询课程
  this.list()
}
```

#### 4.4.3. 测试

- 配置host映射

```
# 学成在线项目配置域名
127.0.0.1  www.xuecheng.com
192.168.12.132  img.xuecheng.com
```

- 测试流程：
    1. 上传图片成功
    2. 进入上传图片页面，观察图片是否显示

## 5. 课程图片删除
### 5.1. 需求分析

课程图片上传成功后，可以重新上传，方法是先删除现有图片再上传新图片

*注意：此删除只删除课程数据库的课程图片信息，不去删除文件数据库的文件信息及文件系统服务器上的文件，由于课程图片来源于该用户的文件库，所以此图片可能存在多个地方共用的情况，所以要删除文件系统中的文件需要到图片库由用户确认后再删除*。

### 5.2. 服务API接口

在课程管理服务CourseControllerApi接口添加删除课程图片api方法

```java
@ApiOperation("删除课程图片")
public ResponseResult deleteCoursePic(String courseId);
```

### 5.3. 课程管理服务端开发
#### 5.3.1. dao 层

CoursePicRepository父类提供的delete方法没有返回值，无法知道是否删除成功，所以在CoursePicRepository接口中自定义删除方法

```java
public interface CoursePicRepository extends JpaRepository<CoursePic, String> {
    /**
     * 删除成功返回1，失败则返回0
     *
     * @param courseId 课程id
     * @return 当返回值大于0，表示删除成功的记录数
     */
    long deleteByCourseid(String courseId);
}
```

#### 5.3.2. Service 层

修改CourseService类，增加删除课程图片的方法

```java
/**
 * 删除课程图片
 *
 * @param courseId 课程id
 * @return 响应标识
 */
@Transactional
public ResponseResult deleteCoursePic(String courseId) {
    // 执行删除，返回1表示删除成功，返回0表示删除失败
    long result = coursePicRepository.deleteByCourseid(courseId);
    if (result > 0) {
        return ResponseResult.SUCCESS();
    }
    return ResponseResult.FAIL();
}
```

#### 5.3.3. Controller 层

修改CourseController类，实现删除课程图片deleteCoursePic方法

```java
/**
 * 删除课程图片
 *
 * @param courseId 课程id
 * @return 响应结果
 */
@Override
@DeleteMapping("/coursepic/delete")
public ResponseResult deleteCoursePic(@RequestParam("courseId") String courseId) {
    return courseService.deleteCoursePic(courseId);
}
```

### 5.4. 删除图片前端开发
#### 5.4.1. API 调用

修改course.js，增加删除课程图片方法

```js
/* 删除课程图片 */
export const deleteCoursePic = courseId => {
  return http.requestDelete(apiUrl + '/course/coursepic/delete?courseId=' + courseId)
}
```

#### 5.4.2. 页面测试

- 定义上传组件中的before-remove钩子方法，在upload组件的before-remove钩子方法中实现删除动作

```html
<el-upload
  action="/api/filesystem/upload"
  list-type="picture-card"
  :before-upload="setuploaddata"
  :on-success="handleSuccess"
  :file-list="fileList"
  :limit="picmax"
  :on-exceed="rejectupload"
  :before-remove="handleRemove"
  :data="uploadval"
>
  <i class="el-icon-plus"></i>
</el-upload>
```

- before-remove说明：删除文件之前的钩子，参数为上传的文件和文件列表，若返回 false 或者返回 Promise 且被 reject，则停止删除。
- 定义handleRemove方法进行测试：handleRemove 返回 true 则删除页面的图片，返回 false 则停止删除页面的图片。

```js
// 删除图片
handleRemove(file, fileList) {
  console.log(file)
  // 如果返回false，前端停止删除
  return false
},
```

#### 5.4.3. promise异步调用
##### 5.4.3.1. 删除测试

- 在handleRemove方法调用删除图片的api方法，根据后端返回标识，删除成功时return true，删除失败时return false

```js
// 删除图片
handleRemove(file, fileList) {
  // 异步调用服务端去删除课程图片信息，如果返回false，前端停止删除
  courseApi.deleteCoursePic('1').then(res => {
    if (res.success) {
      this.$message.success('删除成功')
      return true
    } else {
      this.$message.error(res.message)
      return false
    }
  })
},
```

- 在上边代码中将提交的课程id故意写错，按照我们预期应该是删除失败，而测试结果却是图片在页面上删除成功
- 问题原因：通过查询deleteCoursePic方法的底层代码，deleteCoursePic最终返回一个promise对象

##### 5.4.3.2. Promise 对象说明

- Promise 是ES6提供的用于异步处理的对象，因为axios提交是异步提交，这里使用promise作为返回值
- Promise对象在处理过程中有三种状态：
    - pending：进行中
    - resolved：操作成功
    - rejected: 操作失败
- Promise的使用方法如下：

```js
const promise = new Promise((resolve, reject) => {
  // ...TODO...
  if (操作成功) {
    resolve(value)
  } else {
    reject(value)
  }
})
```

- 上边的构造方法 `function(resolve, reject)` 执行流程如下
    - 方法执行一些业务逻辑
    - 如果操作成功将Promise的状态由pending变为resolved，并将操作结果传出去
    - 如果操作失败会将promise的状态由pending变为rejected，并将失败结果传出去
- 上边说的操作成功与失败返回的数据，通过promise的then、catch来指定接收

```js
promise.then(result => {
  console.log(`操作成功：${result}`)
})
promise.catch(reason => {
  console.log(`操作失败：${reason}`)
})
```

##### 5.4.3.3. Promise 使用案例与修改删除图片的钩子方法

1. 定义一个方法，返回promise对象

```js
/**
 * promise 有三种状态:
 *  进行中pending
 *  执行成功 resolve
 *  执行失败 reject
 */
testPromise(i) {
  return new Promise((resolve, reject) => {
    if (i % 2 == 0) {
      // 成功了
      resolve('成功了');
    } else {
      // 失败了
      reject('失败了');
    }
  })
},
```

2. 调用此方法，向方法传入偶数、奇数进行测试

```js
// 测试调用promise方法，then中写的成功后的回调方法
this.testPromise(1).then(res => {
  alert(res)
}).catch(res => { // catch就是执行失败的回调方法
  alert("失败了。。。。。")
  alert(res)
})
```

3. 使用Promise方法，修改上传组件的handleRemove方法。handleRemove方法返回promise对象，当删除成功则resolve，删除失败则reject。

```js
handleRemove(file, fileList) {
  // 异步调用服务端去删除课程图片信息，如果返回false，前端停止删除
  return new Promise((resolve, reject) => {
    courseApi.deleteCoursePic(this.courseid).then(res => {
      if (res.success) {
        this.$message.success("删除成功")
        // 成功
        resolve()
      } else {
        this.$message.error("删除失败");
        // 失败
        reject()
      }
    })
  })
},
```
