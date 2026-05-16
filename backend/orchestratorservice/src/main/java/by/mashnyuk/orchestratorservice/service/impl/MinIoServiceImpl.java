package by.mashnyuk.orchestratorservice.service.impl;

import by.mashnyuk.orchestratorservice.exceptions.AudioDeleteException;
import by.mashnyuk.orchestratorservice.exceptions.AudioUploadException;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MinIoServiceImpl {

  private final MinioClient minioClient;

  public void uploadFile(String bucketName, String fileId, MultipartFile audio){
    try {
      InputStream inputStream = audio.getInputStream();

      minioClient.putObject(
              PutObjectArgs.builder()
                      .bucket(bucketName)
                      .object(fileId)
                      .stream(inputStream, inputStream.available(),-1)
                      .build()
      );
    } catch (Exception e) {
      throw new AudioUploadException("Something went wrong while uploading the audio file",e.getCause());
    }
  }

  public String getPresignedUrl(String bucketName, String fileId) {
    try {
      return minioClient.getPresignedObjectUrl(
              GetPresignedObjectUrlArgs.builder()
                      .method(Method.GET)
                      .bucket(bucketName)
                      .object(fileId)
                      .expiry(2, TimeUnit.HOURS)
                      .build()
      );
    } catch (Exception e) {
      throw new AudioUploadException("Exception while generating a reference", e);
    }
  }

  public byte[] getFileById(String bucketName, String fileId){
    try {
      var stream = minioClient.getObject(
              GetObjectArgs.builder()
                      .bucket(bucketName)
                      .object(fileId)
                      .build()
      );

      return IOUtils.toByteArray(stream);
    } catch (Exception e) {
      throw new AudioUploadException("Something went wrong while getting the audio file",e.getCause());
    }
  }

  public void deleteFile(String bucketName, String fileId) {
    try {
      minioClient.removeObject(
              RemoveObjectArgs.builder()
                      .bucket(bucketName)
                      .object(fileId)
                      .build()
      );
    } catch (Exception e) {
      throw new AudioDeleteException("Failed to delete file from MinIO", e);
    }
  }
}
