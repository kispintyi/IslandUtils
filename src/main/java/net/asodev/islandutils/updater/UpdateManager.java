package net.asodev.islandutils.updater;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.asodev.islandutils.IslandUtils;
import net.asodev.islandutils.updater.schema.AvailableUpdate;
import net.asodev.islandutils.updater.schema.GithubRelease;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class UpdateManager {

    HttpClient client;
    Gson gson;

    public UpdateManager() {
        client = HttpClient.newBuilder().build();
        gson = new Gson();
    }

    public CompletableFuture<GithubRelease> checkForUpdates() throws Exception {
        CompletableFuture<GithubRelease> f = new CompletableFuture<>();

        URI updatorURI = new URI("https://api.github.com/repos/AsoDesu/IslandUtils/releases/latest");
        HttpRequest req = HttpRequest.newBuilder(updatorURI).GET().build();

        client.sendAsync(req, HttpResponse.BodyHandlers.ofString()).thenAccept(res -> {
            f.complete(gson.fromJson(res.body(), GithubRelease.class));
        });

        return f;
    }

    public void runUpdateCheck() {
        try {
            checkForUpdates().thenAccept(res -> {
                if (res == null) return;
                if (!IslandUtils.version.equals(res.getTagName())) {
                    IslandUtils.availableUpdate =
                            new AvailableUpdate(res.getName(), res.getTagName(), "https://modrinth.com/mod/island-utils/version/" + res.getTagName());
                }
            });
        } catch (Exception e) {
            System.err.println("Failed to get IslandUtils Update!");
        }
    }

}
