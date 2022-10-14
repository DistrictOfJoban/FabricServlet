package com.lx.fabricservlet.Config;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lx.fabricservlet.Data.FabricServletServer;
import com.lx.fabricservlet.FabricServlet;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

public class config {
    public static final String WEB_BASE_PATH = System.getProperty("user.dir") + "/config/fbweb/";
    private static final String CONFIG_PATH = System.getProperty("user.dir") + "/config/fbweb/" + "config.json";

    public static void loadConfig() {
        if(!Files.exists(Paths.get(CONFIG_PATH))) {
            FabricServlet.LOGGER.warn("[FabricServlet] Config file not found, generating one...");
            writeConfig();
            return;
        }

        FabricServlet.LOGGER.info("[FabricServlet] Reading Config...");
        try {
            int i = 0;
            final JsonObject jsonConfig = new JsonParser().parse(String.join("", Files.readAllLines(Paths.get(CONFIG_PATH)))).getAsJsonObject();

            for(Map.Entry<String, JsonElement> elements : jsonConfig.entrySet()) {
                String urlPath = "file://" + (WEB_BASE_PATH + elements.getKey()).replace(" ", "%20");
                int port = 9967 + i;
                boolean allowDirListing = false;
                if(elements.getValue().getAsJsonObject().has("port")) {
                    port = elements.getValue().getAsJsonObject().get("port").getAsInt();
                }

                if(elements.getValue().getAsJsonObject().has("dirListingAllowed")) {
                    allowDirListing = elements.getValue().getAsJsonObject().get("dirListingAllowed").getAsBoolean();
                }

                FabricServlet.serverList.add(new FabricServletServer(urlPath, port, allowDirListing, i));
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            writeConfig();
        }
    }

    public static void writeConfig() {
        FabricServlet.LOGGER.info("[FabricServlet] Writing Config");
        final JsonObject jsonConfig = new JsonObject();
        jsonConfig.addProperty("debug", false);
        jsonConfig.addProperty("port", 9967);

        try {
            Files.write(Paths.get(CONFIG_PATH), Collections.singleton(new GsonBuilder().setPrettyPrinting().create().toJson(jsonConfig)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
