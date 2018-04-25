package com.pinyougou.shop.controller;

import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import utils.FastDFSClient;

/**
 * 图片上传
 */
@RestController
public class UploadController {

    //注入地址属性
    @Value("${FILE_SERVER_URL}")
    private  String file_server_url;

    @RequestMapping("/upload")
    public Result upload(MultipartFile file){   //springmvc接收文件的指定类
        //获取文件名
        String originalFilename = file.getOriginalFilename();
        //获取拓展名
        String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);

        try {
            //创建上传工具类,传入配置文件
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.conf");
            //调用上传方法,获取fileid
            String fileId = fastDFSClient.uploadFile(file.getBytes(), extName);
            //生成图片存储路径
            String url = file_server_url + fileId ;
            System.out.println(url);
            //返回路径
            return new Result(true,url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败");
        }
    }
}
