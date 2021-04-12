package fr.craftandconquest.warofsquirrels.commands.admin;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.Player;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.dimension.DimensionType;

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
        int chunkX = player.getPlayerEntity().getPosition().getX() / 16;
        int chunkZ = player.getPlayerEntity().getPosition().getZ() / 16;

        BiomeContainer biomes = WarOfSquirrels.server.getWorld(DimensionType.OVERWORLD).getChunk(chunkX, chunkZ).func_225549_i_();
        Biome[] biomesAsArray = new Biome[Biome.BIOMES.size()];

        biomesAsArray = Biome.BIOMES.toArray(biomesAsArray);

        int i = 0;
        if (biomes != null) {
            for (int biomeId : biomes.func_227055_a_()) {
                if (biomeId < biomesAsArray.length)
                    player.getPlayerEntity().sendMessage(new StringTextComponent("[" + i + "] " + biomesAsArray[biomeId].getDisplayName()));
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
