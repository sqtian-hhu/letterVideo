package com.letter.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FetchVideoCover {

    private String ffmpegEXE;

    public FetchVideoCover(String ffmpegEXE) {
        super();
        this.ffmpegEXE = ffmpegEXE;
    }


    public void fetchCover(String videoInputPath, String coverOutputPath) throws IOException {
//        ffmpeg -ss 00:00:01 -y -i test.mp4 -vframes 1 new.jpg

        //调用cmd命令
        List<String> command = new ArrayList<String>();
        command.add(ffmpegEXE);

        command.add("-ss");
        command.add("00:00:01");
        command.add("-y");
        command.add("-i");
        command.add(videoInputPath);


        command.add("-vframes");
        command.add("1");

        command.add(coverOutputPath);

//        System.out.println(command);

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
        FetchVideoCover ffmpeg = new FetchVideoCover("E:\\Tools\\ffmpeg\\bin\\ffmpeg.exe");
        ffmpeg.fetchCover("E:\\Tools\\ffmpeg\\bin\\test1.mp4","E:\\Tools\\ffmpeg\\bin\\test1.jpg");


    }
}
