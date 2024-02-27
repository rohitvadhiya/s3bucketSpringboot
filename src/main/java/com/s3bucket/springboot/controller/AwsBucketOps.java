package com.s3bucket.springboot.controller;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.s3bucket.springboot.service.AwsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v2/s3ops")
public class AwsBucketOps {
    @Autowired
    AwsService awsService;

    //get your all bucket list of s3
    @GetMapping("/get")
    public List<S3ObjectSummary> getbucketlist() {
        List<S3ObjectSummary> s3ObjectSummaries = awsService.getObjectList();
        return s3ObjectSummaries;
    }

    // Upload your image into awss3db
    @PostMapping("/uploadfile")
    public ResponseEntity<String> uploadfile(@RequestParam("image") MultipartFile multipartFile) {
        String str = awsService.uploadFile(multipartFile);
        return ResponseEntity.ok(str);
    }

    //object means perticuller folder name
    //get Url Of Your image or file
    @GetMapping("getimageurl")
    public String geturl(@RequestParam("path") String objectofyourFile) {
        String url = awsService.url(objectofyourFile);
        return url;
    }

    //delete all object of Your Particuller Folder
    @DeleteMapping("deleteObject")
    public String deleteObjects(@RequestParam("path") String objectofyourFile) {
        String str = awsService.deleteObjectsInFolder(objectofyourFile);
        return str;
    }

    //get all object of Your Particuller Folder
    @GetMapping("getallObjects")
    public List<S3ObjectSummary> getallObject(@RequestParam("path") String folerName) {
        List<S3ObjectSummary> s3ObjectSummaries = awsService.getObjectsInFolder(folerName);
        return s3ObjectSummaries;
    }
}
