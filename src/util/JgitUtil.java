package util;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.IOException;

public class JgitUtil {
    private String localPath, localGitPath, remotePath;
    private Repository localRepository;
    private String username;
    private String password;
    private Git git;

    public JgitUtil(String localPath, String remotePath, String username , String password) {
        this.username = username;
        this.password = password;
        this.localPath = localPath;
        this.remotePath = remotePath;
        this.localGitPath = this.localPath + "/.git";
        try {
            localRepository = new FileRepository(localGitPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        git = new Git(localRepository);

    }

    public int addAll(){
        try{
            git.add().addFilepattern(".").call();
            System.out.println("添加修改到暂存区");
            return 1;
        }catch (Exception e){
            e.printStackTrace();

        }
        return 0;
    }
    public int commit() {
        try{
            git.commit().setMessage("Upload by Imagic v 0.2").call();
            System.out.println("往分支上提交修改");
            return 1;
        }catch (Exception e){
            e.printStackTrace();

        }
        return 0;

    }
    public int push(){
        try {
            git.push().setCredentialsProvider(new UsernamePasswordCredentialsProvider(this.username,this.password)).call();
            System.out.println("提交到远程仓库");
            return 1;
        }catch (Exception e){
            e.printStackTrace();
        }

        return 0;
    }

}
