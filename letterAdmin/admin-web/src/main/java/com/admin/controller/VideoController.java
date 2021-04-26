package com.admin.controller;

import com.admin.enums.VideoStatusEnum;
import com.admin.pojo.Bgm;
import com.admin.pojo.Users;
import com.admin.service.VideoService;
import com.admin.utils.LetterJSONResult;
import com.admin.utils.PagedResult;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

@Controller
@RequestMapping("/video")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @GetMapping("/showAddBgm")
    public String showAddBgm(){
        return "video/addBgm";
    }

    @GetMapping("/showBgmList")
    public String showBgmList(){
        return "video/bgmList";
    }

    @GetMapping("/showReportList")
    public String showReportList() {
        return "video/reportList";
    }

    /**
     * 管理员上传BGM
     * @param files
     * @return
     * @throws Exception
     */
    @PostMapping("/bgmUpload")
    //ResponseBody注解表示返回Json对象
    @ResponseBody
    //添加RequestParam注解,不使用该注解要求controller方法中的参数名称要跟form中name名称一致，使用该注解方便随意取参数名称，不过value属性还是要与name一致
    //MultipartFile 其实例对象代表了一个在multipart请求中接收到的待上传文件
    public LetterJSONResult bgmUpload(@RequestParam("file") MultipartFile[] files) throws Exception {


        //统一文件保存的命名空间
        //File.separator会根据系统的不同选择/
        String fileSpace = "E:"+File.separator+"letter_videos_dev" + File.separator + "mvc-bgm";
        //保存到数据库中的相对路径
        String uploadPathDB = File.separator + "bgm";

        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;

        try{
            if (files != null && files.length > 0){


                String originalFilename = files[0].getOriginalFilename();
                if(StringUtils.isNotBlank(originalFilename)){
                    //文件上传的最终保存路径
                    String finalPath = fileSpace + uploadPathDB + File.separator + originalFilename;
                    //设置数据库保存的路径
                    uploadPathDB += (File.separator + originalFilename);

                    File outFile = new File(finalPath);
                    if(outFile.getParentFile() != null || !outFile.getParentFile().isDirectory() ){
                        //如果保存头像的父文件夹不为空或者不是文件夹, 创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }

                    //输出到最终文件目录的流
                    fileOutputStream = new FileOutputStream(outFile);
                    //上传文件的输入流
                    inputStream = files[0].getInputStream();
                    //上传文件拷贝到服务器最终文件夹里
                    IOUtils.copy(inputStream,fileOutputStream);
                }

            }else{
                return LetterJSONResult.errorMsg("上传出错");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return LetterJSONResult.errorMsg("上传出错");
        }finally{
            if(fileOutputStream != null){
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }


        //返回存储路径
        return LetterJSONResult.ok(uploadPathDB);
    }



    @PostMapping("/addBgm")
    @ResponseBody
    public LetterJSONResult addBgm(Bgm bgm){

        videoService.addBgm(bgm);
        return LetterJSONResult.ok();
    }

    @PostMapping("/delBgm")
    @ResponseBody
    public LetterJSONResult delBgm(String bgmId){

        videoService.delBgm(bgmId);
        return LetterJSONResult.ok();
    }


    @RequestMapping("/queryBgmList")
    @ResponseBody
    /**
     * jqGrid无法识别LetterJSONResult,要直接返回PagedResult
     */
    public PagedResult queryBgmList(Integer page){
//        System.out.println(videoService.queryBgmList(page,10).getRows().get(1).toString());

        return videoService.queryBgmList(page,10);
    }

    @PostMapping("/reportList")
    @ResponseBody
    public PagedResult reportList(Integer page) {

        return videoService.queryReportList(page, 10);
    }

    /**
     * 将视频状态设为禁播
     * @param videoId
     * @return
     */

    @PostMapping("/forbidVideo")
    @ResponseBody
    public LetterJSONResult forbidVideo(String videoId) {

        videoService.updateVideoStatus(videoId, VideoStatusEnum.FORBID.value);
        return LetterJSONResult.ok();
    }
}
