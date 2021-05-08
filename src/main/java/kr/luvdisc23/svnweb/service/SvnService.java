package kr.luvdisc23.svnweb.service;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class SvnService {

    private static final String svnUrl      = "svn.url";
    private static final String svnUser     = "svn.user";
    private static final String svnPassword = "svn.password";

    public static List<SVNLogEntry> getLogEntry(String startDate, String endDate) {
        List<SVNLogEntry> logEntries = new ArrayList<>();

        try {
            var url = SVNURL.parseURIEncoded(PropertyService.get(svnUrl));
            var repository = SVNRepositoryFactory.create(url);
            try {
                repository.setAuthenticationManager(BasicAuthenticationManager.newInstance(PropertyService.get(svnUser), PropertyService.get(svnPassword).toCharArray()));
                repository.testConnection();
                long lastRevision = repository.getLatestRevision();

                var fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
                var start = fmt.parseDateTime(startDate);
                var end = fmt.parseDateTime(endDate).plusDays(1);
                long startDateRevision = repository.getDatedRevision(start.toDate());
                long endDateRevision = repository.getDatedRevision(end.toDate());

                repository.log(new String[]{}, endDateRevision, startDateRevision, true, false, 0, svnLogEntry -> {
                    logEntries.add(svnLogEntry);
                });
            } finally {
                repository.closeSession();
            }
        } catch (SVNException e) {
            e.printStackTrace();
        }

        return logEntries;
    }
}
