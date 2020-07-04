package com;

import org.json.JSONObject;
import util.AfJSON;
import util.JgitUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {
    //要配置的东西 example
    public static String localRepoPath = "";//本地的git仓库
    public static String remoteRepoURL = "";//远程的git仓库
    public static String remoteRepoName="";//远程的git仓库的名字
    public static String username = "";//用户名
    public static String password = "";//密码

    public static JgitUtil jgitUtil = null;

    //要拷贝的路径
    private static String oldURL = "";
    private static String newURL = "";

    //上传成功后回传的地址
    private static String realURL = "";


    //暂存区
    public static String temp;

    //ui相关
    private static final long serialVersionUID = 1L;
    private static JTextField field;
    private static JMenuBar menuBar;


    public static void main(String[] args) {
        loadData();
        new MainUI();
    }


    /****************UI相关****************/
    public static class MainUI extends JFrame {
        public MainUI() {

            this.setTitle("Imagic");
            this.setSize(500, 300);
            this.setLocationRelativeTo(null);
            this.setLayout(null);
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            field = new JTextField();
            field.setBounds(16, 20, 450, 200);


            field.setTransferHandler(new TransferHandler() {
                private static final long serialVersionUID = 1L;

                @Override
                public boolean importData(JComponent comp, Transferable t) {
                    try {
                        Object o = t.getTransferData(DataFlavor.javaFileListFlavor);

                        String filepath = o.toString();
                        if (filepath.startsWith("[")) {
                            filepath = filepath.substring(1);
                        }
                        if (filepath.endsWith("]")) {
                            filepath = filepath.substring(0, filepath.length() - 1);
                        }
                        filepath = filepath.replaceAll("\\\\", "/");
                        oldURL = filepath;
                        if (username.isEmpty() != false || password.isEmpty() != false || localRepoPath.isEmpty() != false || remoteRepoURL.isEmpty() != false) {
                            JOptionPane.showMessageDialog(null,"弟弟 你配置都没弄 想啥呢？","warning",JOptionPane.QUESTION_MESSAGE);
                            return false;
                        }
                        JOptionPane wait=new JOptionPane("uploading..");
                        JDialog dialog=wait.createDialog("uploading");
                        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                        dialog.setAlwaysOnTop(true);
                        dialog.setModal(false);
                        dialog.setVisible(true);

                        System.out.println("有一个地址进来咯，它是：" + oldURL);
                        System.out.println("准备上传到github仓库");
                        if (initData() == 1) {
                            CopyAuto copyAuto = new CopyAuto(oldURL, newURL);
                            copyAuto.doIt();

                            System.out.println("复制完成");
                            int returnNum=three();
                            if (returnNum == 1) {
                                dialog.setVisible(false);
                                dialog.dispose();
                                System.out.println("上传成功！");
                                realURL = "https://cdn.jsdelivr.net/gh/"+username+"/"+remoteRepoName+"/" + temp + type(oldURL);
                                System.out.println(realURL);
                                setSysClipboardText(realURL);
                                field.setText(realURL);
                                JOptionPane.showMessageDialog(null, "已复制到粘贴板");
                            }else if(returnNum==2){
                                dialog.setVisible(false);
                                dialog.dispose();
                                JOptionPane.showMessageDialog(null, "WDNMD 原来是git add . 这一步出错了");
                                return false;
                            }else if(returnNum==3){
                                dialog.setVisible(false);
                                dialog.dispose();
                                JOptionPane.showMessageDialog(null, "WDNMD 原来是git commit 这一步出错了");
                                return false;
                            }else if(returnNum==4){
                                dialog.setVisible(false);
                                dialog.dispose();
                                JOptionPane.showMessageDialog(null, "WDNMD 原来是git push   这一步出错了");
                                return false;
                            }else {
                                dialog.setVisible(false);
                                dialog.dispose();
                                JOptionPane.showMessageDialog(null, "WDNMD git上传过程中出错了 请重试");
                                return false;
                            }


                        }

//                        field.setText(filepath);
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                }

                @Override
                public boolean canImport(JComponent comp, DataFlavor[] flavors) {
                    for (int i = 0; i < flavors.length; i++) {
                        if (DataFlavor.javaFileListFlavor.equals(flavors[i])) {
                            return true;
                        }
                    }
                    return false;
                }
            });

            menuBar = new JMenuBar();
            JMenu setting = new JMenu("setting");
            JMenuItem item = new JMenuItem("配置");

            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("弹窗成功！");
                    username = JOptionPane.showInputDialog("用户名");
                    System.out.println(username);
                    password = JOptionPane.showInputDialog("密码");
                    System.out.println(password);
                    remoteRepoName=JOptionPane.showInputDialog("远程仓库的名字");
                    System.out.println(remoteRepoName);
                    localRepoPath = JOptionPane.showInputDialog("本地仓库路径");
                    localRepoPath = localRepoPath.replaceAll("\\\\", "/");
                    System.out.println(localRepoPath);
                    remoteRepoURL = "git@github.com:"+username+"/"+remoteRepoName+".git";
                    System.out.println(remoteRepoURL);


                    String message = "请重新配置";
                    if (username.isEmpty() == false && password.isEmpty() == false && localRepoPath.isEmpty() == false && remoteRepoURL.isEmpty() == false) {
                        message = "配置成功！";
                        JSONObject json = new JSONObject();
                        json.put("username", username);
                        json.put("password", password);
                        json.put("localRepoPath", localRepoPath);
                        json.put("remoteRepoURL", remoteRepoURL);
                        json.put("remoteRepoName",remoteRepoName);

                        File file = new File("message.json");
                        try {
                            AfJSON.toFile(json, file, "UTF-8");
                        } catch (Exception e1) {
                            JOptionPane.showMessageDialog(null, e1.getMessage());
                            e1.printStackTrace();
                        }
                    }
                    JOptionPane.showMessageDialog(null, message);
                }
            });
            JMenuItem show = new JMenuItem("显示配置");
            show.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String message = "弟弟 你配置不全";
                    if (username.isEmpty() == false && password.isEmpty() == false && localRepoPath.isEmpty() == false && remoteRepoURL.isEmpty() == false) {
                        message = "用户名是：" + username + "  " + "密码是：" + password + "  " + "本地仓库路径是：" + localRepoPath + "  " + "远程仓库地址是：" + remoteRepoURL;
                    }
                    JOptionPane.showMessageDialog(null, message);
                }
            });

            JMenuItem clearitem = new JMenuItem("清除信息");
            clearitem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("点击了清除按钮");

                    File file = new File("message.json");
                    if (file.exists())
                        file.delete();

                }
            });


            setting.add(item);
            setting.add(show);
            setting.add(clearitem);
            menuBar.add(setting);


            this.add(field);
            this.setJMenuBar(menuBar);

            Font font=new Font("微软雅黑",Font.BOLD,46);
            this.setFont(font);
            this.setVisible(true);
        }
    }
    /**
     * 将字符串复制到剪切板。
     */
    public static void setSysClipboardText(String writeMe) {
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable tText = new StringSelection(writeMe);
        clip.setContents(tText, null);
    }
    /*************缓存操作相关***************/
    /**
     * 载入数据
     */
    private static void loadData() {
        File file = new File("message.json");
        if (!file.exists()) return;
        JSONObject json = null;
        try {
            json = (JSONObject) AfJSON.fromFile(file, "UTF-8");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            e.printStackTrace();
            return;
        }
        username = json.getString("username");
        password = json.getString("password");
        localRepoPath = json.getString("localRepoPath");
        remoteRepoURL = json.getString("remoteRepoURL");
        remoteRepoName=json.getString("remoteRepoName");

        System.out.println("用户名是：" + username + "  " + "密码是：" + password + "  " + "本地仓库路径是：" + localRepoPath + "  " + "远程仓库地址是：" + remoteRepoURL);
    }


    /*************git操作相关***************/
    /**
     * 初始化
     */
    private static int initData() {
        oldURL = oldURL.replaceAll("\\\\", "/");

        newURL = localRepoPath + "/" + timeG() + type(oldURL);
        newURL = newURL.replaceAll("\\\\", "/");

        System.out.println("就的路径的是" + oldURL);
        System.out.println("要复制到的路径是：" + newURL);
        jgitUtil=new JgitUtil(localRepoPath, remoteRepoURL, username, password);

        return 1;
    }

    /**
     * git add .
     * git commit -m "update by Imagic"
     * git push
     * 三连
     */
    public static int three() {
        try {
            if (jgitUtil.addAll() == 1) {
                if (jgitUtil.commit() == 1) {
                    if (jgitUtil.push() == 1) {
                        return 1;
                    }else {
                        return 4;
                    }
                }else {
                    return 3;
                }
            }else {
                return 2;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取后缀名，暂且支持jpg和png格式
     */
    public static String type(String url) {
        if (url.endsWith(".png") || url.endsWith(".jpg")) {
            if (url.endsWith(".png"))
                return ".png";
            else if (url.endsWith(".jpg"))
                return ".jpg";
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
     * 拷贝文件
     */
    public static class CopyAuto implements AutoCloseable {
        private String sourceURL;
        private String destURL;
        private InputStream input = null;
        private OutputStream output = null;

        public CopyAuto(String sourceURL, String destURL) {
            this.sourceURL = sourceURL;
            this.destURL = destURL;
        }

        public void doIt() {
            System.out.println("接收到的数据是：");
            System.out.println("souceURL=" + sourceURL);
            System.out.println("destURL=" + destURL);
            if (sourceURL.isEmpty() == false && destURL.isEmpty() == false) {
                File source = new File(sourceURL);
                File dest = new File(destURL);

                try {
                    input = new FileInputStream(source);
                    output = new FileOutputStream(dest);
                    System.out.println("没有错啊");
                    byte[] buf = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = input.read(buf)) > 0) {
                        output.write(buf, 0, bytesRead);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void close() throws Exception {
            input.close();
            output.close();
        }

    }
}
