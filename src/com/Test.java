package com;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Test {
    //要配置的东西
    public static String localRepoPath = "F:/example";//本地的git仓库
    public static String remoteRepoURL = "git@github.com:example/example.git";//远程的git仓库
    public static String username = "example";//用户名
    public static String passwrod = "example";//密码

    public static JgitUtil jgitUtil = new JgitUtil(localRepoPath, remoteRepoURL, username, passwrod);

    //要拷贝的路径
    private static String oldURL = "";
    private static String newURL = "";


    //暂存区
    public static String temp;

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("请输入要转换的图片路径：");
        oldURL = input.next();

        if (initData() == 1) {
            try {
                if (copyFile(oldURL, newURL) == 1) {
                    System.out.println("复制完成");
                    if (three() == 1) {
                        System.out.println("上传成功！");
                        System.out.println("https://cdn.jsdelivr.net/gh/fushaolei/imguploadtest/" + temp + type(oldURL));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


//        if (three()==1){
//            System.out.println("上传成功！");
//        }

    }

    private static int initData() {
        oldURL = oldURL.replaceAll("\\\\", "/");

        newURL = localRepoPath + "/" + timeG() + type(oldURL);
        newURL = newURL.replaceAll("\\\\", "/");

        System.out.println("就的路径的是" + oldURL);
        System.out.println("要复制到的路径是：" + newURL);

        return 1;
    }

    public static int three() {
        try {
            if (jgitUtil.addAll() == 1) {
                if (jgitUtil.commit() == 1) {
                    if (jgitUtil.push() == 1) {
                        return 1;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取后缀名
     */
    public static String type(String url) {
        if (url.endsWith(".png") || url.endsWith(".jpg") || url.endsWith(".gif")) {
            if (url.endsWith(".png"))
                return ".png";
            else if (url.endsWith(".jpg"))
                return ".jpg";
            else if (url.endsWith(".gif")) {
                return ".gif";
            }
        }
        return "";
    }

    /**
     * 获取当前时间
     */
    public static String timeG() {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyyMMddHHmmss");
        Date date = new Date();
        System.out.println("代号：" + sdf.format(date));
        temp = sdf.format(date);
        return sdf.format(date);
    }

    /**
     * 复制要用到的方法
     *
     * @param sourceURL
     * @param destURL
     * @return
     * @throws Exception
     */
    private static int copyFile(String sourceURL, String destURL) throws Exception {
        File source = new File(sourceURL);
        File dest = new File(destURL);

        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } finally {
            inputChannel.close();
            outputChannel.close();
        }

        return 1;
    }
}
