package kr.luvdisc23.svnweb;

import io.javalin.Javalin;
import io.javalin.http.Context;
import kr.luvdisc23.svnweb.router.SvnRouter;
import kr.luvdisc23.svnweb.vo.SvnVo;
import kr.luvdisc23.svnweb.vo.SvnVoPage;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        Javalin app =  Javalin.create(config -> {
            config.addSinglePageRoot("/", "/public/index.html");
            config.addStaticFiles("/public");
            config.autogenerateEtags = true;

        });
        app.start(7000);

        app.get("/svn", SvnRouter::handle);
        app.get("/svn/:page", SvnRouter::handle);
    }
}
