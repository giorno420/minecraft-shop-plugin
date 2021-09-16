package com.giornosmp.shop.api.v1;

import com.giornosmp.shop.api.v1.models.Player;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.*;
import com.giornosmp.shop.ThisPlugin;
import org.bukkit.Bukkit;

import java.util.ArrayList;

import java.util.concurrent.Callable;

public class ShopAPI {

    @OpenApi(
            path = "/v1/players",
            summary = "Gets all currently online players",
            tags = {"Player"},
            headers = {
                    @OpenApiParam(name = "key")
            },
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(from = Player.class, isArray = true))
            }
    )
    public static void playersGet(Context ctx) {
        ArrayList<Player> players = new ArrayList<>();

        Bukkit.getOnlinePlayers().forEach((player -> {
            Player p = new Player();
            p.setDisplayName(player.getDisplayName());

            p.setAddress(player.getAddress().getHostName());

            players.add(p);
        }));

        ctx.json(players);
    }

    @OpenApi(
            path = "/v1/command",
            summary = "Runs a command",
            tags = {"Server"},
            headers = {
                    @OpenApiParam(name = "key")
            },
            formParams = {
                    @OpenApiFormParam(name = "command")
            },
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(from = Player.class, isArray = true))
            }
    )
    public static void command(Context ctx) {

        if (ctx.formParam("command").isEmpty() || ctx.formParam("command") == null){
            throw new BadRequestResponse("no cmd given kys");
        }
        String cmd = ctx.formParam("command");
        Bukkit.getScheduler().callSyncMethod( ThisPlugin.p, new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
            }
        });
        ctx.json("success");
    }

}
