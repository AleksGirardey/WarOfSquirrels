package fr.craftandconquest.warofsquirrels.commands.extractor;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.cuboide.AdminCubo;
import net.minecraft.commands.CommandSourceStack;

public interface IAdminCuboExtractor extends IExtractor<AdminCubo> {
    @Override
    default String getArgumentName() {
        return "AdminCuboName";
    }

    @Override
    default AdminCubo getArgument(CommandContext<CommandSourceStack> context) {
        return WarOfSquirrels.instance.getAdminHandler().get(getRawArgument(context));
    }

    @Override
    default SuggestionProvider<CommandSourceStack> getSuggestions() {
        return ((context, builder) -> suggestions(builder, WarOfSquirrels.instance.getAdminHandler().getAll()));
    }
}