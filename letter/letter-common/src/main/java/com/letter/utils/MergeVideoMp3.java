package com.letter.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MergeVideoMp3 {

    private String ffmpegEXE;

    public MergeVideoMp3(String ffmpegEXE) {
        super();
        this.ffmpegEXE = ffmpegEXE;
    }


    public void convertor(String videoInputPath, String mp3InputPath, double seconds, String videoOutputPath) throws IOException {
//        ffmpeg -i input.mp4 -i bgm.mp3 -t 7 -y output.mp4

        //调用cmd命令
        List<String> command = new ArrayList<String>();
        command.add(ffmpegEXE);

        command.add("-i");
        command.add(videoInputPath);
        command.add("-i");
        command.add(mp3InputPath);

        command.add("-t");
        command.add(String.valueOf(seconds));

        command.add("-y");
        command.add(videoOutputPath);


        //运行cmd
        ProcessBuilder builder = new ProcessBuilder(command);
        Process process = builder.start();

        //处理流碎片,避免资源浪费
        InputStream errorStream = process.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
        BufferedReader br = new BufferedReader(inputStreamReader);

        while (br.readLine() != null){

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
        MergeVideoMp3 ffmpeg = new MergeVideoMp3("E:\\Tools\\ffmpeg\\bin\\ffmpeg.exe");
        ffmpeg.convertor("E:\\Tools\\ffmpeg\\bin\\test.mp4","E:\\ffmpeg\\bin\\bgm.mp3",7,"E:\\ffmpeg\\bin\\spring.avi");

    }
}
