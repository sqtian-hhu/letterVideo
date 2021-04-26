package com.letter.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FFMpegTest {

    private String ffmpegEXE;

    public FFMpegTest(String ffmpegEXE) {
        super();
        this.ffmpegEXE = ffmpegEXE;
    }


    public void convertor(String videoInputPath, String videoOutputPath) throws IOException {
//        ffmpeg -i input.mp4 output.avi

        //调用cmd命令
        List<String> command = new ArrayList<String>();
        command.add(ffmpegEXE);

        command.add("-i");
        command.add(videoInputPath);
        command.add(videoOutputPath);

        System.out.println(command);

        //运行cmd
        ProcessBuilder builder = new ProcessBuilder(command);
        Process process = builder.start();

        //处理流碎片,避免资源浪费
        InputStream errorStream = process.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
        BufferedReader br = new BufferedReader(inputStreamReader);

        String line="";
        while ((line=br.readLine()) != null){

        }

        if (br != null){
            br.close();
        }

        if (inputStreamReader != null){
            inputStreamReader.close();
        }

        if (errorStream != null){
            errorStream.close();
        }

    }
    public static void main(String[] args) throws IOException {
        FFMpegTest ffmpeg = new FFMpegTest("E:\\Tools\\ffmpeg\\bin\\ffmpeg.exe");
        ffmpeg.convertor("E:\\Tools\\ffmpeg\\bin\\test.mp4","E:\\Tools\\ffmpeg\\bin\\spring.avi");

    }
}
