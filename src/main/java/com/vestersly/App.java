package com.vestersly;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class App {
  public static void main(String[] args) throws IOException {
    int port = 8080;
    HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
    server.createContext("/", exchange -> {
      String resp = "Hello from DevSecOps pipeline 👋";
      exchange.sendResponseHeaders(200, resp.getBytes().length);
      try (OutputStream os = exchange.getResponseBody()) { os.write(resp.getBytes()); }
    });
    server.setExecutor(null);
    server.start();
    System.out.println("Server listening on " + port);
  }
}
