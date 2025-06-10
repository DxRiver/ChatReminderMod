package net.dxriver.chatremindermod;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ClientCommandHandler {
    private static final List<String> PRESET_COLORS = Arrays.asList(
            "FFFFFF", "000000", "FF0000", "00FF00", "0000FF", "FFFF00",
            "FF00FF", "00FFFF", "808080", "C0C0C0", "800000", "008000",
            "000080", "808000", "800080", "008080"
    );
    private static final SuggestionProvider<CommandSourceStack> COLOR_SUGGESTIONS =
            (context, builder) -> {
                PRESET_COLORS.forEach(builder::suggest);
                return builder.buildFuture();
            };

    private static void registerAddCommand(RegisterClientCommandsEvent event, boolean isIdTrigger) {
        event.getDispatcher().register(Commands.literal("ChatReminder")
                .then(Commands.literal(isIdTrigger ? "addid" : "addword")
                        .then(Commands.argument("trigger", StringArgumentType.string())
                                .then(Commands.argument("color", StringArgumentType.string()).suggests(COLOR_SUGGESTIONS)
                                        .then(Commands.argument("sound", ResourceLocationArgument.id())
                                                .suggests(SuggestionProviders.AVAILABLE_SOUNDS)
                                                .executes(ctx -> createConfig(
                                                        ctx,
                                                        isIdTrigger,
                                                        1,
                                                        0,
                                                        1.0f,
                                                        1.0f,
                                                        1.0f
                                                ))

                                                .then(Commands.argument("notes", IntegerArgumentType.integer(1, 20))
                                                        .executes(ctx -> createConfig(
                                                                ctx,
                                                                isIdTrigger,
                                                                IntegerArgumentType.getInteger(ctx, "notes"),
                                                                0,
                                                                1.0f,
                                                                1.0f,
                                                                1.0f
                                                        ))
                                                        .then(Commands.argument("delay", IntegerArgumentType.integer(0))
                                                                .executes(ctx -> createConfig(
                                                                        ctx,
                                                                        isIdTrigger,
                                                                        IntegerArgumentType.getInteger(ctx, "notes"),
                                                                        IntegerArgumentType.getInteger(ctx, "delay"),
                                                                        1.0f,
                                                                        1.0f,
                                                                        1.0f
                                                                ))
                                                                .then(Commands.argument("volume", FloatArgumentType.floatArg(0, 1))
                                                                        .executes(ctx -> createConfig(
                                                                                ctx,
                                                                                isIdTrigger,
                                                                                IntegerArgumentType.getInteger(ctx, "notes"),
                                                                                IntegerArgumentType.getInteger(ctx, "delay"),
                                                                                FloatArgumentType.getFloat(ctx, "volume"),
                                                                                1.0f,
                                                                                1.0f
                                                                        ))
                                                                        .then(Commands.argument("pitchMin", FloatArgumentType.floatArg(0.1f, 3.0f))
                                                                                .executes(ctx -> createConfig(
                                                                                        ctx,
                                                                                        isIdTrigger,
                                                                                        IntegerArgumentType.getInteger(ctx, "notes"),
                                                                                        IntegerArgumentType.getInteger(ctx, "delay"),
                                                                                        FloatArgumentType.getFloat(ctx, "volume"),
                                                                                        FloatArgumentType.getFloat(ctx, "pitchMin"),
                                                                                        1.0f
                                                                                ))
                                                                                .then(Commands.argument("pitchMax", FloatArgumentType.floatArg(0.1f, 3.0f))
                                                                                        .executes(ctx -> createConfig(
                                                                                                ctx,
                                                                                                isIdTrigger,
                                                                                                IntegerArgumentType.getInteger(ctx, "notes"),
                                                                                                IntegerArgumentType.getInteger(ctx, "delay"),
                                                                                                FloatArgumentType.getFloat(ctx, "volume"),
                                                                                                FloatArgumentType.getFloat(ctx, "pitchMin"),
                                                                                                FloatArgumentType.getFloat(ctx, "pitchMax")
                                                                                        ))
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
    }

    private static int createConfig(
            CommandContext<CommandSourceStack> ctx,
            boolean isIdTrigger,
            int notes,
            int delay,
            float volume,
            float pitchMin,
            float pitchMax
    ) throws CommandSyntaxException {
        String trigger = StringArgumentType.getString(ctx, "trigger");
        String color = StringArgumentType.getString(ctx, "color");
        ResourceLocation soundId = ResourceLocationArgument.getId(ctx, "sound");

        
        TriggerConfig config = TriggerConfig.builder()
                .color(color)
                .sound(getSoundWithFallback(soundId))
                .notes(notes)
                .delay(delay)
                .volume(volume)
                .pitchRange(pitchMin, pitchMax)
                .build();

        if (isIdTrigger) {
            Reminder.addIdTrigger(trigger, config);
        } else {
            Reminder.addKeywordTrigger(trigger, config);
        }
        return 1;
    }

    private static SoundEvent getSoundWithFallback(ResourceLocation soundId) {
        SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(soundId);
        return sound != null ? sound : new SoundEvent(soundId);
    }

    @SubscribeEvent
    public static void registerCommands(RegisterClientCommandsEvent event) {
        registerAddCommand(event, true);  
        registerAddCommand(event, false); 

        event.getDispatcher().register(Commands.literal("ChatReminder")
                .then(Commands.literal("setid")
                        .then(Commands.argument("target", StringArgumentType.string())
                                .executes(ctx -> {
                                    String target = StringArgumentType.getString(ctx, "target");
                                    Reminder.setTargetId(target);
                                    return 1;
                                })
                        )
                )
                .then(Commands.literal("sound")
                        .then(Commands.argument("sound_id", ResourceLocationArgument.id())
                                .suggests(SuggestionProviders.AVAILABLE_SOUNDS)
                                .executes(ctx -> {
                                    ResourceLocation soundId = ResourceLocationArgument.getId(ctx, "sound_id");
                                    Reminder.setSound(soundId);
                                    return 1;
                                })
                        )
                )
                .then(Commands.literal("notes")
                        .then(Commands.argument("count", IntegerArgumentType.integer(1, 20))
                                .executes(ctx -> {
                                    int count = IntegerArgumentType.getInteger(ctx, "count");
                                    Reminder.setNoteCount(count);
                                    return 1;
                                })
                        )
                )
                .then(Commands.literal("pitchrange")
                        .then(Commands.argument("min", FloatArgumentType.floatArg(0.1f, 3.0f))
                                .then(Commands.argument("max", FloatArgumentType.floatArg(0.1f, 3.0f))
                                        .executes(ctx -> {
                                            float min = FloatArgumentType.getFloat(ctx, "min");
                                            float max = FloatArgumentType.getFloat(ctx, "max");
                                            Reminder.setPitchRange(min, max);
                                            return 1;
                                        })
                                )
                        )
                )
                .then(Commands.literal("setcolor")
                        .then(Commands.argument("hex", StringArgumentType.string()).suggests(COLOR_SUGGESTIONS)
                                .executes(ctx -> {
                                    String hex = StringArgumentType.getString(ctx, "hex");
                                    Reminder.setHexColor(hex);
                                    return 1;
                                })
                        )
                )
                .then(Commands.literal("removeid")
                        .then(Commands.argument("id", StringArgumentType.string())
                                .executes(ctx -> {
                                    String id = StringArgumentType.getString(ctx, "id");
                                    Reminder.removeIdTrigger(id);
                                    return 1;
                                })
                        )
                )
                .then(Commands.literal("removeword")
                        .then(Commands.argument("keyword", StringArgumentType.string())
                                .executes(ctx -> {
                                    String keyword = StringArgumentType.getString(ctx, "keyword");
                                    Reminder.removeKeywordTrigger(keyword);
                                    return 1;
                                })
                        )
                )
        );
    }
}