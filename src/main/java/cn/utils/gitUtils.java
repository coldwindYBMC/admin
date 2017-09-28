package cn.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.filter.PathFilterGroup;

public class gitUtils {
	private static CredentialsProvider cp = new UsernamePasswordCredentialsProvider("hanxuquan", "0ipvsft5");
	public static void gitCheckout(File repoDir, String version) {
	    File RepoGitDir = new File(repoDir.getAbsolutePath() + "/.git");
	    if (!RepoGitDir.exists()) {
	        System.out.println("Error! Not Exists : " + RepoGitDir.getAbsolutePath());
	    } else {
	        Repository repo = null;
	        try {
	            repo = new FileRepository(RepoGitDir.getAbsolutePath());
	            Git git = new Git(repo);
	            CheckoutCommand checkout = git.checkout();
	            checkout.setName(version);
	            checkout.call();
	            System.out.println("Checkout to " + version);

	            PullCommand pullCmd = git.pull().setCredentialsProvider(cp);
	            pullCmd.call();

	            System.out.println("Pulled from remote repository to local repository at " + repo.getDirectory());
	        } catch (Exception e) {
	        		System.out.println(e.getMessage() + " : " + RepoGitDir.getAbsolutePath());
	        } finally {
	            if (repo != null) {
	                repo.close();
	            }
	        }
	    }
	}
	public static void gitPull(File repoDir) {
	    File RepoGitDir = new File(repoDir.getAbsolutePath() + "/.git");
	    if (!RepoGitDir.exists()) {
	    	System.out.println("Error! Not Exists : " + RepoGitDir.getAbsolutePath());
	    } else {
	        Repository repo = null;
	        try {
	            repo = new FileRepository(RepoGitDir.getAbsolutePath());
	            Git git = new Git(repo);
	            
	            PullCommand pullCmd = git.pull().setCredentialsProvider(cp);
	            pullCmd.call();

	            System.out.println("Pulled from remote repository to local repository at " + repo.getDirectory());
	        } catch (Exception e) {
	        	System.out.println(e.getMessage() + " : " + RepoGitDir.getAbsolutePath());
	        } finally {
	            if (repo != null) {
	                repo.close();
	            }
	        }
	    }
	}
	/** 
     * 将文件列表提交到git仓库中 
     * @param gitRoot git仓库目录 
     * @param files 需要提交的文件列表 
     * @param remark 备注 
     * @return 返回本次提交的版本号 
     * @throws IOException  
     */  
    public static String commitToGitRepository(String gitRoot, List<String> files, String remark)  
                                                                                                 throws Exception {  
        if (!utils.isEmpty(gitRoot) && files != null && files.size() > 0) {  
  
            File rootDir = new File(gitRoot);  
            //初始化git仓库  
            if (new File(gitRoot + File.separator + ".git").exists() == false) {  
                Git.init().setDirectory(rootDir).call();  
            }
            //打开git仓库  
            Git git = Git.open(rootDir);  
            //判断工作区与暂存区的文件内容是否有变更  
            List<DiffEntry> diffEntries = git.diff()  
                .setPathFilter(PathFilterGroup.createFromStrings(files))  
                .setShowNameAndStatusOnly(true).call();  
            if (diffEntries == null || diffEntries.size() == 0) {  
            		System.out.println("git没变化!");
                return "没变化!";  
            }  
            //被修改过的文件  
            List<String> updateFiles = new ArrayList<String>();  
            ChangeType changeType;  
            for (DiffEntry entry : diffEntries) {  
                changeType = entry.getChangeType();  
                switch (changeType) {  
                    case ADD:  
                    case COPY:  
                    case RENAME:  
                    case MODIFY:  
                        updateFiles.add(entry.getNewPath());  
                        break;  
                    case DELETE:  
                        updateFiles.add(entry.getOldPath());  
                        break;  
                }  
            }  
            //将文件提交到git仓库中，并返回本次提交的版本号  
            //1、将工作区的内容更新到暂存区  
            AddCommand addCmd = git.add();  
            for (String file : updateFiles) {  
                addCmd.addFilepattern(file);  
            }  
            addCmd.call();  
            //2、commit  
            CommitCommand commitCmd = git.commit();  
            for (String file : updateFiles) {  
                commitCmd.setOnly(file);  
            }  
            RevCommit revCommit = commitCmd.setCommitter("hanxuquan", "hanxuquan@hoolai.com")  
                .setMessage(remark).call();  
            System.out.println("commit");
            PushCommand push = git.push().setCredentialsProvider(cp);
            push.call();
            System.out.println("push");
            return revCommit.getName();  
        }  
        return null;  
    }
}
