package fr.maxducoudre.botting;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Main {
    private final static int NUMBER_OF_VIEWS = 10000;
    private final static String USER = "seo-referencement-optimisation-trafic-hanoot";

    public static void main(String[] args){
        addViews(USER, 10_000);
    }


    private static void addViews(String user, int views) {

        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder(
                        URI.create("https://www.codeur.com/-" + user))
                .header("accept", "application/json")
                .build();

        for(int i = 0; i < views/100; i++) {
            Thread.ofPlatform().start(() -> {
                for(int j = 0; j < 10_000; j++) {
                    try {
                        client.send(request, HttpResponse.BodyHandlers.ofString());
                    } catch (Exception e) {
                        continue;
                    }
                }
            });
        }
    }
}