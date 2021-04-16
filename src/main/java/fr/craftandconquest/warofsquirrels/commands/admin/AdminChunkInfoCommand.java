package fr.craftandconquest.warofsquirrels.commands.admin;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import fr.craftandconquest.warofsquirrels.utils.Pair;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;

import java.util.ArrayList;
import java.util.List;

public class AdminChunkInfoCommand extends AdminCommandBuilder {
    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("ci").executes(this);
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) { return true; }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
//        int chunkPerTerritory = (WarOfSquirrels.instance.getConfig().getTerritorySize() / 16);
        Pair<Integer, Integer> chunkPos = Utils.WorldToChunkCoordinates(player.getPlayerEntity().getPosition().getX(), player.getPlayerEntity().getPosition().getZ());
//        int chunkX = player.getPlayerEntity().getPosition().getX() / 16;
//        int chunkZ = player.getPlayerEntity().getPosition().getZ() / 16;

        BiomeContainer biomes = WarOfSquirrels.server.getWorld(DimensionType.OVERWORLD).getChunk(chunkPos.getKey(), chunkPos.getValue()).func_225549_i_();
        Biome[] biomesAsArray = new Biome[Biome.BIOMES.size()];

        biomesAsArray = Biome.BIOMES.toArray(biomesAsArray);

        ForgeRegistry<Biome> registry = (ForgeRegistry<Biome>) ForgeRegistries.BIOMES;

        List<Integer> values = new ArrayList<>();
        int i = 0;
        if (biomes != null) {
            for (int biomeId : biomes.func_227055_a_()) {
                if (values.contains(biomeId)) continue;
                values.add(biomeId);
            }

            player.getPlayerEntity()
                    .sendMessage(new StringTextComponent(" === [" + chunkPos.getKey() + ";" + chunkPos.getValue() + "] === "));

            for (int biomeId : values) {
                player.getPlayerEntity()
                        .sendMessage(new StringTextComponent("[" + i + "] " + registry.getValue(biomeId).getTranslationKey()));
                ++i;
            }
        }

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
        return 0;
    }
}
