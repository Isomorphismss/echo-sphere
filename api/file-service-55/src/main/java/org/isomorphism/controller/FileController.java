package org.isomorphism.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.isomorphism.MinIOConfig;
import org.isomorphism.MinIOUtils;
import org.isomorphism.api.feign.UserInfoMicroServiceFeign;
import org.isomorphism.grace.result.GraceJSONResult;
import org.isomorphism.grace.result.ResponseStatusEnum;
import org.isomorphism.pojo.vo.UsersVO;
import org.isomorphism.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("file")
public class FileController {

    @Value("${image.storage.path}")
    private String rootPath;

    // 127.0.0.1:55/file/uploadFace?userId

    @PostMapping("uploadFace1")
    public GraceJSONResult uploadFace1(@RequestParam("file") MultipartFile file,
                                      String userId,
                                      HttpServletRequest request) throws IOException {

        String filename = file.getOriginalFilename();   // 获得文件原始名称元始天尊
        String suffixName = filename.substring(filename.lastIndexOf("."));  // 从最后一个.开始截取
        String newFilename = userId + suffixName;   // 文件的新名称

        String filePath = rootPath + File.separator + newFilename;
        File newFile = new File(filePath);
        // 判断目标文件所在目录是否存在
        if (!newFile.getParentFile().exists()) {
            // 如果目标文件所在目录不存在，则创建父级目录
            newFile.getParentFile().mkdirs();
        }

        // 将内存中的数据写入磁盘
        file.transferTo(newFile);

        return GraceJSONResult.ok();
    }

    @Resource
    private MinIOConfig minIOConfig;

    @Resource
    private UserInfoMicroServiceFeign userInfoMicroServiceFeign;

    @PostMapping("uploadFace")
    public GraceJSONResult uploadFace(@RequestParam("file") MultipartFile file,
                                       String userId,
                                       HttpServletRequest request) throws Exception {

        if (StringUtils.isBlank(userId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        String filename = file.getOriginalFilename();   // 获得文件原始名称元始天尊
        if (StringUtils.isBlank(filename)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        filename = "face" + "/" + userId + "/" + filename;
        MinIOUtils.uploadFile(minIOConfig.getBucketName(),
                filename,
                file.getInputStream()
        );

        String faceUrl = minIOConfig.getFileHost()
                + "/"
                + minIOConfig.getBucketName()
                + "/"
                + filename;

        /**
         * 微服务远程调用更新用户头像到数据库 OpenFeign
         * 如果前端没有保存按钮可以这么做，如果有保存提交按钮，则在前端可以触发
         * 此处则不需要进行微服务调用，让前端触发保存提交到后台进行保存
         */
        GraceJSONResult jsonResult = userInfoMicroServiceFeign.updateFace(userId, faceUrl);
        Object data = jsonResult.getData();

        String json = JsonUtils.objectToJson(data);
        UsersVO usersVO = JsonUtils.jsonToPojo(json, UsersVO.class);

        return GraceJSONResult.ok(usersVO);
    }

}
