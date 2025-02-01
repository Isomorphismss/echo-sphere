package org.isomorphism.test;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import io.minio.errors.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@SpringBootTest
public class MinIOTest {

    @Value("${minio.url}")
    private String endpoint;

    @Value("${minio.username}")
    private String userName;

    @Value("${minio.password}")
    private String password;

    @Value("${minio.local-image-source}")
    private String testImageSource;

    @Test
    public void testUpload() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        // 创建客户端
        MinioClient minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(userName, password)
                .build();
        // 如果没有bucket，则需要创建
        String bucketName = "localjava";
        boolean isExist = minioClient.bucketExists(
                BucketExistsArgs
                        .builder()
                        .bucket(bucketName)
                        .build()
        );

        // 判断当前的bucket是否存在，不存在则创建，存在则什么都不做
        if (!isExist) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(bucketName)
                            .build()
            );
        } else {
            System.out.println("当前[" + bucketName + "]已经存在！");
        }

        // 上传本地的文件到minio的服务器中
        minioClient.uploadObject(UploadObjectArgs
                .builder()
                .bucket(bucketName)
                .object("myImage.jpg")
                .filename(testImageSource)
                .build()
        );

    }

}
