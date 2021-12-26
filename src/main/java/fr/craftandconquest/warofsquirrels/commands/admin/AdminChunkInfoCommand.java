package fr.craftandconquest.warofsquirrels.commands.admin;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.Pair;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class AdminChunkInfoCommand extends AdminCommandBuilder {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("ci").executes(this);
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
//        int chunkPerTerritory = (WarOfSquirrels.instance.getConfig().getTerritorySize() / 16);
//        Pair<Integer, Integer> chunkPos = Utils.WorldToChunkCoordinates(player.getPlayerEntity().getPosition().getX(), player.getPlayerEntity().getPosition().getZ());
//        int chunkX = player.getPlayerEntity().getPosition().getX() / 16;
//        int chunkZ = player.getPlayerEntity().getPosition().getZ() / 16;

//        BiomeContainer biomes = WarOfSquirrels.server.getWorld(DimensionType.OVERWORLD).getChunk(chunkPos.getKey(), chunkPos.getValue()).func_225549_i_();
//        Biome[] biomesAsArray = new Biome[Biome.BIOMES.size()];
//
//        biomesAsArray = Biome.BIOMES.toArray(biomesAsArray);
//
//        ForgeRegistry<Biome> registry = (ForgeRegistry<Biome>) ForgeRegistries.BIOMES;
//
//        List<Integer> values = new ArrayList<>();
//        int i = 0;
//        if (biomes != null) {
//            for (int biomeId : biomes.func_227055_a_()) {
//                if (values.contains(biomeId)) continue;
//                values.add(biomeId);
//            }
//
//
//            for (int biomeId : values) {
//                player.getPlayerEntity()
//                        .sendMessage(new StringTextComponent("[" + i + "] " + registry.getValue(biomeId).getTranslationKey()));
//                ++i;
//            }
//        }

//        for (int chunkX = chunkXOffset; chunkX < chunkXOffset + chunkPerTerritory; ++chunkX) {
//            for (int chunkZ = chunkZOffset; chunkZ < chunkZOffset + chunkPerTerritory; ++chunkZ) {
//                if (chunkX == 0 && chunkZ == 0) {
//                    BiomeContainer biomes = WarOfSquirrels.server.getWorld(DimensionType.OVERWORLD).getChunk(chunkX, chunkZ).func_225549_i_();
//                    if (biomes != null) {
//                        for (int biome : biomes.func_227055_a_()) { }
//                    }
//                }
//            }
//        }
//        Pair<Integer, Integer> territoryPos = Utils.ChunkToTerritoryCoordinates(chunkPos.getKey(), chunkPos.getValue());
//        int territorySize = WarOfSquirrels.instance.getConfig().getTerritorySize();
//
//        int posXMin = territoryPos.getKey() * territorySize;
//        int posXMax = posXMin + territorySize;
//        int posZMin = territoryPos.getValue() * territorySize;
//        int posZMax = posZMin + territorySize;
//
//        World world = WarOfSquirrels.server.getWorld(DimensionType.OVERWORLD);
//
//        Map<Integer, Integer> biomeMap = new HashMap<>();
//
//        BiomeManager bm = world.func_225523_d_();
//
//        for (int x = posXMin; x < posXMax; x += 16) {
//            for (int z = posZMin; z < posZMax; z += 16) {
//                Pair<Integer, Integer> chunkPosition = Utils.WorldToChunkCoordinates(x, z);
//                BiomeContainer biomeContainer = world
//                        .getChunk(chunkPosition.getKey(), chunkPosition.getValue())
//                        .func_225549_i_();
//                if (biomeContainer != null) {
//                    int[] biomes = biomeContainer.func_227055_a_();
//                    for (int biomeId : biomes) {
//                        if (!biomeMap.containsKey(biomeId))
//                            biomeMap.put(biomeId, 0);
//                        biomeMap.compute(biomeId, (k, v) -> (v == null) ? 1 : v+1);
//                    }
//                }
//            }
//        }
//
//        ForgeRegistry<Biome> registry = (ForgeRegistry<Biome>) ForgeRegistries.BIOMES;
//
//        player.getPlayerEntity()
//                .sendMessage(new StringTextComponent(" === [" + chunkPos.getKey() + ";" + chunkPos.getValue() + "] === "));
//
//        biomeMap.forEach((k, v) ->
//                player.sendMessage(new StringTextComponent(
//                registry.getValue(k).getTranslationKey() + " - " + v)));

        return 0;
    }
}
