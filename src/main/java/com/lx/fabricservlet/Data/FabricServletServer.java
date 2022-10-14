package com.lx.fabricservlet.Data;

import org.eclipse.jetty.server.Server;

public class FabricServletServer {
    public int id;
    public int port;
    public boolean dirListing;
    public String path;
    public Server activeServer;

    public FabricServletServer(String path, int port, boolean dirListing, int id) {
        this.port = port;
        this.dirListing = dirListing;
        this.path = path;
        this.activeServer = null;
        this.id = id;
    }
}
