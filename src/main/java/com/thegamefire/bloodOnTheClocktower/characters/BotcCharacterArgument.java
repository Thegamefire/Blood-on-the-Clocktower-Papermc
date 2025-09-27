package com.thegamefire.bloodOnTheClocktower.characters;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class BotcCharacterArgument implements CustomArgumentType.Converted<BotcCharacter, String> {
    private static final DynamicCommandExceptionType ERROR_INVALID_CHARACTER = new DynamicCommandExceptionType(
            character -> MessageComponentSerializer.message().serialize(Component.text(character+ " is not a valid BotC character!"))
            );

    @Override
    public @NotNull BotcCharacter convert(String s) throws CommandSyntaxException {
        try {
            return BotcCharacter.valueOf(s.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            throw ERROR_INVALID_CHARACTER.create(s);
        }
    }

    @Override
    public @NotNull ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }

    @Override
    public <S> @NotNull CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        for (BotcCharacter character : BotcCharacter.values()) {
            String name = character.toString().toLowerCase();

            if (name.startsWith(builder.getRemainingLowerCase())) {
                builder.suggest(character.toString());
            }
        }

        return builder.buildFuture();
    }
}
