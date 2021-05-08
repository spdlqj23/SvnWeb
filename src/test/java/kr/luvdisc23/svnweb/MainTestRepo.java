package kr.luvdisc23.svnweb;

import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

import java.io.File;

/*
    테스트용 로컬 레포지토리 생성
*/
public class MainTestRepo {

    public static void main(String[] args) throws Exception{
        String path = "./tmp/testrepo";
        SVNRepositoryFactoryImpl.setup();

        SVNURL tgtURL = SVNRepositoryFactory.createLocalRepository(new File(path), true, true);
        System.out.println(tgtURL);
        for(int i = 0; i < 300; i++) {
            final CommitBuilder commitBuilder = new CommitBuilder(tgtURL);
            commitBuilder.setCommitMessage("Message " + i);
            commitBuilder.addFile("file" + i);
            commitBuilder.commit();
        }
    }
}
