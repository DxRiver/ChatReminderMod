package net.dxriver.chatremindermod;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = ChatReminderMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModSounds {
    private static final Map<String, SoundEvent> SOUNDS = new HashMap<>();

    public static final String QQ1 = "chatremindermod:qq1";
    public static final String WX1 = "chatremindermod:wx1";
    public static final String WX2 = "chatremindermod:wx2";


    @SubscribeEvent
    public static void onRegisterSounds(RegisterEvent event) {
        event.register(ForgeRegistries.SOUND_EVENTS.getRegistryKey(), helper -> {
            SOUNDS.forEach((name, sound) -> {
                ResourceLocation location = ResourceLocation.tryBuild(ChatReminderMod.MODID, name);
                helper.register(location, sound);
            });
        });
    }

    public static SoundEvent getSound(String name) {
        return SOUNDS.get(name);
    }
}