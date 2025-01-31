package org.isomorphism.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.isomorphism.grace.result.GraceJSONResult;
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

    @PostMapping("uploadFace")
    public GraceJSONResult uploadFace(@RequestParam("file") MultipartFile file,
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

}
