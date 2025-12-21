package com.lx862.takeaseat.config;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lx862.takeaseat.TakeASeat;
import com.lx862.takeaseat.Util;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Config {
    private static final Path CONFIG_PATH = Paths.get(FabricLoader.getInstance().getConfigDir().toString(), "takeaseat.json");
    private final List<Identifier> allowedBlockId = new ArrayList<>();
    private final List<TagKey<Block>> allowedBlockTag = new ArrayList<>(Arrays.asList(TagKey.create(Registries.BLOCK, Identifier.parse("stairs")), TagKey.create(Registries.BLOCK, Identifier.parse("slabs"))));
    private boolean ensurePlayerWontSuffocate = true;
    private boolean mustBeEmptyHandToSit = true;
    private boolean blockMustBeLowerThanPlayer = true;
    private boolean mustNotBeObstructed = false;
    private boolean stairs025Offset = false;
    private int requiredOpLevel = 0;
    private double maxDistance = 0;

    public void load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                final JsonObject jsonConfig = JsonParser.parseString(String.join("", Files.readAllLines(CONFIG_PATH))).getAsJsonObject();

                final List<Identifier> allowedBlockIdToAdd = new ArrayList<>();
                final List<TagKey<Block>> allowedBlockTagToAdd = new ArrayList<>();

                if (jsonConfig.has("allowedBlockId")) {
                    jsonConfig.getAsJsonArray("allowedBlockId").forEach(e -> {
                        allowedBlockIdToAdd.add(Identifier.parse(e.getAsString()));
                    });
                }

                if (jsonConfig.has("allowedBlockTag")) {
                    jsonConfig.getAsJsonArray("allowedBlockTag").forEach(e -> {
                        allowedBlockTagToAdd.add(TagKey.create(Registries.BLOCK, Identifier.parse(e.getAsString())));
                    });
                }

                ensurePlayerWontSuffocate = GsonHelper.getAsBoolean(jsonConfig, "ensurePlayerWontSuffocate", ensurePlayerWontSuffocate);
                stairs025Offset = GsonHelper.getAsBoolean(jsonConfig, "stairsOffset", stairs025Offset);
                mustBeEmptyHandToSit = GsonHelper.getAsBoolean(jsonConfig, "mustBeEmptyHandToSit", mustBeEmptyHandToSit);
                blockMustBeLowerThanPlayer = GsonHelper.getAsBoolean(jsonConfig, "blockMustBeLowerThanPlayer", blockMustBeLowerThanPlayer);
                mustNotBeObstructed = GsonHelper.getAsBoolean(jsonConfig, "mustNotBeObstructed", mustNotBeObstructed);
                maxDistance = GsonHelper.getAsDouble(jsonConfig, "maxDistance", maxDistance);
                requiredOpLevel = GsonHelper.getAsInt(jsonConfig, "requiredOpLevel", requiredOpLevel);

                allowedBlockId.addAll(allowedBlockIdToAdd);
                allowedBlockTag.addAll(allowedBlockTagToAdd);
            } catch (Exception e) {
                TakeASeat.LOGGER.warn("[TakeASeat] Unable to read config file!", e);
                write();
            }
        } else {
            write();
        }
    }

    public void write() {
        try {
            TakeASeat.LOGGER.info("[TakeASeat] Writing Config...");
            final JsonObject jsonConfig = new JsonObject();
            jsonConfig.add("allowedBlockId", Util.toJsonArray(allowedBlockId, Identifier::toString));
            jsonConfig.add("allowedBlockTag", Util.toJsonArray(allowedBlockTag, (tagKey) -> tagKey.location().toString()));
            jsonConfig.addProperty("stairsOffset", stairs025Offset);
            jsonConfig.addProperty("ensurePlayerWontSuffocate", ensurePlayerWontSuffocate);
            jsonConfig.addProperty("mustBeEmptyHandToSit", mustBeEmptyHandToSit);
            jsonConfig.addProperty("blockMustBeLowerThanPlayer", blockMustBeLowerThanPlayer);
            jsonConfig.addProperty("mustNotBeObstructed", mustNotBeObstructed);
            jsonConfig.addProperty("maxDistance", maxDistance);
            jsonConfig.addProperty("requiredOpLevel", requiredOpLevel);

            Files.write(CONFIG_PATH, Collections.singleton(new GsonBuilder().setPrettyPrinting().create().toJson(jsonConfig)));
        } catch (Exception e) {
            TakeASeat.LOGGER.error("Failed to generate config file!", e);
        }
    }

    public boolean blockIdIsAllowed(Identifier identifier) {
        return allowedBlockId.contains(identifier);
    }

    public boolean ensurePlayerWontSuffocate() {
        return ensurePlayerWontSuffocate;
    }

    public boolean mustBeEmptyHandToSit() {
        return mustBeEmptyHandToSit;
    }

    public boolean blockMustBeLowerThanPlayer() {
        return blockMustBeLowerThanPlayer;
    }

    public boolean mustNotBeObstructed() {
        return mustNotBeObstructed;
    }

    public double maxDistance() {
        return maxDistance;
    }

    public int requiredOpLevel() {
        return requiredOpLevel;
    }

    public boolean stairs025Offset() {
        return stairs025Offset;
    }

    public List<TagKey<Block>> getAllowedBlockTag() {
        return allowedBlockTag;
    }
}
