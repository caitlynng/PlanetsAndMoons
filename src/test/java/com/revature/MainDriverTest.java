package com.revature;

import com.revature.utilities.RequestMapper;
import io.javalin.Javalin;

public class MainDriverTest {

    public static void main(String[] args) {
        // leave this code as is: if you change it you will most likely break the application
        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(it -> {
                    it.anyHost();
                });
            });
        });
        RequestMapper.setUpEndPoints(app);
        app.start(7001);
    }
}
