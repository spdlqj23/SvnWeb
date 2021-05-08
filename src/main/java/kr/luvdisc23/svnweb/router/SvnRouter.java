package kr.luvdisc23.svnweb.router;

import io.javalin.core.util.Header;
import io.javalin.http.Context;
import kr.luvdisc23.svnweb.service.PropertyService;
import kr.luvdisc23.svnweb.service.SvnService;
import kr.luvdisc23.svnweb.vo.SvnVo;
import kr.luvdisc23.svnweb.vo.SvnVoPage;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.jetbrains.annotations.NotNull;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class SvnRouter {

    private static final String svnFilePathReplace = "svn.file.path.replace";
    private static final ArrayList<SVNLogEntry> logList = new ArrayList<>();

    public static void handle(@NotNull Context context) throws Exception {
        String author = context.req.getParameter("author");
        String file   = context.req.getParameter("file");
        String startDate  = context.req.getParameter("startDate");
        String endDate   = context.req.getParameter("endDate");
        String page   = context.pathParamMap().get("page");

        if(!context.header(Header.ACCEPT).toUpperCase().contains("JSON")) {
            context.redirect("/");
            return;
        }

        if(StringUtils.isBlank(page)) {
            synchronized (logList) {
                page = "1";
                List<SVNLogEntry> logEntries = SvnService.getLogEntry(startDate, endDate);
                List<SVNLogEntry> filteredEntry = logEntries.stream()
                        .filter(s -> {
                            if(StringUtils.isNotBlank(author)) {
                                if(StringUtils.containsIgnoreCase(s.getAuthor(),author)) {
                                    return true;
                                } else {
                                    return false;
                                }
                            } else {
                                return true;
                            }
                        })
                        .filter(s -> {
                            if(StringUtils.isNotBlank(file)){
                                boolean isContain = false;
                                for(SVNLogEntryPath svnLogPath: s.getChangedPaths().values()) {
                                    String path = svnLogPath.getPath().replace(PropertyService.get(svnFilePathReplace), "");
                                    if(StringUtils.containsIgnoreCase(path,file)) {
                                        return true;
                                    }
                                }
                                return false;
                            }else {
                                return true;
                            }
                        })
                        .collect(Collectors.toList());

                logList.clear();
                logList.addAll(filteredEntry);
            }
        }

        int pageInt = NumberUtils.toInt(page);
        int rowsPerPage = 10;
        AtomicLong lineNumber = new AtomicLong((pageInt - 1) * rowsPerPage);
        SvnVoPage rtnPage = new SvnVoPage();
        var rtn = logList.stream()
                .skip((long) (pageInt - 1) * rowsPerPage)
                .limit(rowsPerPage)
                .map( s -> {
                    SvnVo vo = new SvnVo();
                    vo.setNumber(lineNumber.addAndGet(1));
                    vo.setRevision(s.getRevision());
                    vo.setAuthor(s.getAuthor());
                    vo.setMessage(s.getMessage());
                    vo.setDate(DateFormatUtils.format(s.getDate(), "yyyy-MM-dd HH:mm"));
                    s.getChangedPaths().forEach( (i, svnLogPath) -> {
                        vo.addFile(svnLogPath.getPath().replace(PropertyService.get(svnFilePathReplace), ""));
                    });
                    return vo;
                })
                .collect(Collectors.toList())
                ;
        rtnPage.setTotCnt(logList.size());
        rtnPage.setSvnVo(rtn);
        rtnPage.setCurrentPage(pageInt);
        rtnPage.setRowsPerPage(rowsPerPage);
        rtnPage.makePaging();
        context.json(rtnPage);
    }
}
