package com.giornosmp.shop;

import com.giornosmp.shop.api.v1.ShopAPI;
import io.javalin.Javalin;
import io.javalin.plugin.openapi.OpenApiOptions;
import io.javalin.plugin.openapi.OpenApiPlugin;
import io.javalin.plugin.openapi.ui.SwaggerOptions;
import io.swagger.v3.oas.models.info.Info;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static io.javalin.apibuilder.ApiBuilder.*;

public class GiornoShop extends JavaPlugin {

    private static final Logger log = Bukkit.getLogger();
    private static Javalin app = null;


    @Override
    public void onEnable() {

        saveDefaultConfig();
        FileConfiguration bukkitConfig = getConfig();

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ThisPlugin.constructor((Plugin)this);

        Thread.currentThread().setContextClassLoader(GiornoShop.class.getClassLoader());

        if (app == null) {
            app = Javalin.create(config -> {
                config.defaultContentType = "application/json";
                config.showJavalinBanner = false;

                // unpack the list of strings into varargs
                List<String> corsOrigins = bukkitConfig.getStringList("corsOrigins");
                String[] originArray = new String[corsOrigins.size()];
                for (int i = 0; i < originArray.length; i++) {
                    log.info(String.format("Enabling CORS for %s", corsOrigins.get(i)));
                    originArray[i] = corsOrigins.get(i);
                }
                config.enableCorsForOrigin(originArray);

                // Create an accessManager to verify the path is a swagger call, or has the correct authentication
                config.accessManager((handler, ctx, permittedRoles) -> {
                    String path = ctx.req.getPathInfo();
                    String[] noAuthPaths = new String[]{"/swagger", "/swagger-docs"};
                    List<String> noAuthPathsList = Arrays.asList(noAuthPaths);
                    if (noAuthPathsList.contains(path) || !bukkitConfig.getBoolean("useKeyAuth") || bukkitConfig.getString("key").equals(ctx.header("key"))) {
                        handler.handle(ctx);
                    } else {
                        ctx.status(401).result("Unauthorized key, reference the key existing in config.yml");
                    }
                });

                config.registerPlugin(new OpenApiPlugin(getOpenApiOptions()));
            });
        }
        // Don't create a new instance if the plugin is reloaded
        app.start(bukkitConfig.getInt("port"));

        if (bukkitConfig.getBoolean("debug")) {
            app.before(ctx -> log.info(ctx.req.getPathInfo()));
        }

        app.routes(() -> {
            path(Constants.API_V1, () -> {

                get("players", ShopAPI::playersGet);
                post("command", ShopAPI::command);

            });
        });

        Thread.currentThread().setContextClassLoader(classLoader);

    }

    @Override
    public void onDisable() {
        log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
        if (app != null) {
            app.stop();
        }
    }

    private OpenApiOptions getOpenApiOptions() {
        Info applicationInfo = new Info()
                .title(this.getDescription().getName())
                .version(this.getDescription().getVersion())
                .description(this.getDescription().getDescription());
        return new OpenApiOptions(applicationInfo)
                .path("/swagger-docs")
                .activateAnnotationScanningFor("io.servertap.api.v1")
                .swagger(new SwaggerOptions("/swagger"));
    }

}
