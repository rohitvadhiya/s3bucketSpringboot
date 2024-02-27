package com.s3bucket.springboot.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Service
@Slf4j
public class AwsService {
    @Autowired
    @Qualifier("amazonClient")
    public AmazonS3 amazonS3;

    String bucket = "Bucket_name";

    //get Object List of Specific Bucket
    public List<S3ObjectSummary> getObjectList() {
        ListObjectsV2Result result = amazonS3.listObjectsV2(bucket);
        List<S3ObjectSummary> object = result.getObjectSummaries();
        for (S3ObjectSummary os : object) {
            System.out.println("-- " + os.getKey());
        }
        return object;
    }

    // Upload FIle With Specific File path
    public String uploadFile(MultipartFile multipartFile) {
        File file = new File(multipartFile.getOriginalFilename());
        String keypathOfFile = "path/of/your/FileInS3db";
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, keypathOfFile, file);
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.length());
        amazonS3.putObject(putObjectRequest);
        file.delete();
        return keypathOfFile + "File Saved";
    }

    // Get url to download Your image with given file path
    public String url(String object) {
        String url = "{${YOUR_URL}}";       // which is from application.yaml Orijinal url of bucket
        List<S3ObjectSummary> s3ObjectSummaries = getObjectsInFolder(object);
        return url + "/" + s3ObjectSummaries;
    }

    //get all object of specific folder
    public List<S3ObjectSummary> getObjectsInFolder(String folerName) {
        ListObjectsV2Request request = new ListObjectsV2Request()
                .withBucketName(bucket)
                .withPrefix(folerName + "/");

        ListObjectsV2Result result;
        do {
            result = amazonS3.listObjectsV2(request);
            List<S3ObjectSummary> objects = result.getObjectSummaries();
            if (!objects.isEmpty()) {
                return objects;
            }
            request.setContinuationToken(result.getNextContinuationToken());
        } while (result.isTruncated());
        return null;
    }

    // delete all Object By folder name
    public String deleteObjectsInFolder(String folderName) {
        try {
            ListObjectsV2Request request = new ListObjectsV2Request()
                    .withBucketName(bucket)
                    .withPrefix(folderName + "/");

            ListObjectsV2Result result;
            do {
                result = amazonS3.listObjectsV2(request);
                result.getObjectSummaries().forEach(objectSummary ->
                        amazonS3.deleteObject(new DeleteObjectRequest(bucket, objectSummary.getKey())));
                request.setContinuationToken(result.getNextContinuationToken());
            } while (result.isTruncated());
        } catch (Exception e) {
            return "File Not Deleted...";
        }
        return "File Deleted...";
    }
}
