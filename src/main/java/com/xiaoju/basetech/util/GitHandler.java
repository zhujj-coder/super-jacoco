package com.xiaoju.basetech.util;

/**
 * @description:
 * @author: gaoweiwei_v
 * @time: 2019/6/20 4:28 PM
 */

import com.google.common.collect.Lists;
import com.xiaoju.basetech.util.po.Revision;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.util.FS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

@Component
public class GitHandler {
    static final Logger logger = LoggerFactory.getLogger(GitHandler.class);

    @Value(value = "${gitlab.username}")
    private  String username;

    @Value(value = "${gitlab.password}")
    private  String password;

    public Git cloneRepository(String gitUrl, String codePath, String commitId) throws GitAPIException {
        Git git = Git.cloneRepository()
                .setURI(gitUrl)
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password))
                .setDirectory(new File(codePath))
                .setBranch(commitId)
                .call();
        // 切换到指定commitId
        checkoutBranch(git, commitId);
        return git;
    }
    public List<Revision> log(String codePath, LogParams logParams) throws GitAPIException, IOException {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository repository = builder.setGitDir(new File(codePath))
                .readEnvironment() // scan environment GIT_* variables
                .findGitDir() // scan up the file system tree
                .build();
        Git git = Git.wrap(repository);
        LogCommand logCommand = git.log();
        logCommand.setMaxCount(100);
        setRevisionRange(logCommand,logParams,repository);
        return getHistoryFromLogCommand(logCommand);
    }
    private List<Revision> getHistoryFromLogCommand(final LogCommand command) throws RuntimeException {
        final List<Revision> versions = Lists.newArrayList();
        final Iterable<RevCommit> commits;
        try {
            commits = command.call();
        } catch (GitAPIException e) {
            throw new RuntimeException("Could not get history", e);
        }
        for( RevCommit commit : commits) {
            versions.add(new Revision(
                    commit.getId().getName(),
                    commit.getName(),
                    commit.getAuthorIdent().toExternalString(),
                    new Date(Long.valueOf(commit.getCommitTime()) * 1000 /* convert seconds to milliseconds */),
                    commit.getFullMessage()
            ));
        }
        return versions;
    }
    private void setRevisionRange(LogCommand logCommand, LogParams params, Repository repository) throws IOException {
        if (params != null && logCommand != null) {
            String revisionRangeSince = params.getRevisionRangeSince();
            String revisionRangeUntil = params.getRevisionRangeUntil();
            if (revisionRangeSince != null && revisionRangeUntil != null) {
                ObjectId since = repository.resolve(revisionRangeSince);
                ObjectId until = repository.resolve(revisionRangeUntil);
                logCommand.addRange(since, until);
            }
        }
    }
    private static Ref checkoutBranch(Git git, String branch) {
        try {
            return git.checkout()
                    .setName(branch)
                    .call();
        } catch (GitAPIException e) {
            throw new IllegalStateException(e);
        }
    }

    public static boolean isValidGitRepository(String codePath) {
        Path folder = Paths.get(codePath);
        if (Files.exists(folder) && Files.isDirectory(folder)) {
            // If it has been at least initialized
            if (RepositoryCache.FileKey.isGitRepository(folder.toFile(), FS.DETECTED)) {
                // we are assuming that the clone worked at that time, caller should call hasAtLeastOneReference
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


}