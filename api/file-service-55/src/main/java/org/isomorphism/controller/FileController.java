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
import org.isomorphism.utils.QrCodeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("file")
public class FileController {

    @Value("${image.storage.path}")
    private String rootPath;

    @Value("${qr-code.filePath}")
    private String qrCodePath;

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

    @PostMapping("generatorQrCode")
    public String generatorQrCode(String wechatNumber,
                                  String userId) throws Exception {
        // 构建map对象
        Map<String, String> map = new HashMap<>();
        map.put("wechatNumber", wechatNumber);
        map.put("userId", userId);

        // 把对象转换为json字符串，用于存储到二维码中
        String data = JsonUtils.objectToJson(map);

        // 生成二维码
        BufferedImage qrCodeImage = QrCodeUtils.generateQRCodeImage(data, 300, 300);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(qrCodeImage, "png", baos);
        byte[] qrCodeBytes = baos.toByteArray();

        String uuid = UUID.randomUUID().toString();
        String fileName = uuid + ".png";
        MultipartFile multipartFile = new MockMultipartFile("file", fileName, "image/png", qrCodeBytes);

        String objectName = "wechatNumber/" + userId + "/" + fileName;
        String imageQrCodeUrl = MinIOUtils.uploadFile(minIOConfig.getBucketName(), objectName, multipartFile.getInputStream(), true);

        return imageQrCodeUrl;
    }

    @PostMapping("uploadFriendCircleBg")
    public GraceJSONResult uploadFriendCircleBg(@RequestParam("file") MultipartFile file,
                                      String userId) throws Exception {

        if (StringUtils.isBlank(userId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        String filename = file.getOriginalFilename();   // 获得文件原始名称元始天尊
        if (StringUtils.isBlank(filename)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        filename = "friendCircleBg"
                + "/" + userId
                + "/" + dealWithoutFilename(filename);
        String imageUrl = MinIOUtils.uploadFile(minIOConfig.getBucketName(),
                filename,
                file.getInputStream(),
                true
        );

        GraceJSONResult jsonResult = userInfoMicroServiceFeign
                                            .updateFriendCircleBg(userId, imageUrl);
        Object data = jsonResult.getData();

        String json = JsonUtils.objectToJson(data);
        UsersVO usersVO = JsonUtils.jsonToPojo(json, UsersVO.class);

        return GraceJSONResult.ok(usersVO);
    }

    @PostMapping("uploadChatBg")
    public GraceJSONResult uploadChatBg(@RequestParam("file") MultipartFile file,
                                         String userId) throws Exception {

        if (StringUtils.isBlank(userId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        String filename = file.getOriginalFilename();   // 获得文件原始名称元始天尊
        if (StringUtils.isBlank(filename)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        filename = "chatBg"
                + "/" + userId
                + "/" + dealWithoutFilename(filename);
        String imageUrl = MinIOUtils.uploadFile(minIOConfig.getBucketName(),
                filename,
                file.getInputStream(),
                true
        );

        GraceJSONResult jsonResult = userInfoMicroServiceFeign
                .updateChatBg(userId, imageUrl);
        Object data = jsonResult.getData();

        String json = JsonUtils.objectToJson(data);
        UsersVO usersVO = JsonUtils.jsonToPojo(json, UsersVO.class);

        return GraceJSONResult.ok(usersVO);
    }

    @PostMapping("uploadFriendCircleImage")
    public GraceJSONResult uploadFriendCircleImage(@RequestParam("file") MultipartFile file,
                                        String userId) throws Exception {

        if (StringUtils.isBlank(userId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        String filename = file.getOriginalFilename();   // 获得文件原始名称元始天尊
        if (StringUtils.isBlank(filename)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        filename = "FriendCircleImage"
                + "/" + userId
                + "/" + dealWithoutFilename(filename);
        String imageUrl = MinIOUtils.uploadFile(minIOConfig.getBucketName(),
                filename,
                file.getInputStream(),
                true
        );

        return GraceJSONResult.ok(imageUrl);
    }

    @PostMapping("uploadChatPhoto")
    public GraceJSONResult uploadChatPhoto(@RequestParam("file") MultipartFile file,
                                           String userId) throws Exception {

        if (StringUtils.isBlank(userId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        String filename = file.getOriginalFilename();   // 获得文件原始名称元始天尊
        if (StringUtils.isBlank(filename)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        filename = "chat"
                + "/" + userId
                + "/" + "photo"
                + "/" + dealWithoutFilename(filename);
        String imageUrl = MinIOUtils.uploadFile(minIOConfig.getBucketName(),
                filename,
                file.getInputStream(),
                true
        );

        return GraceJSONResult.ok(imageUrl);
    }

    private String dealWithFilename(String filename) {
        String suffixName = filename.substring(filename.lastIndexOf("."));  // 从最后一个.开始截取
        String fName = filename.substring(0, filename.lastIndexOf("."));
        String uuid = UUID.randomUUID().toString();
        return fName + "-" + uuid + suffixName;
    }

    private String dealWithoutFilename(String filename) {
        String suffixName = filename.substring(filename.lastIndexOf("."));  // 从最后一个.开始截取
        String uuid = UUID.randomUUID().toString();
        return uuid + suffixName;
    }

}
