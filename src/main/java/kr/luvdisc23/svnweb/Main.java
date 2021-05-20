package kr.luvdisc23.svnweb;

import io.javalin.Javalin;
import kr.luvdisc23.svnweb.router.SvnRouter;

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
