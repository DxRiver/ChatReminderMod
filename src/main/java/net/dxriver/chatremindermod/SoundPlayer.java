package net.dxriver.chatremindermod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Random;
@OnlyIn(Dist.CLIENT)
public class SoundPlayer {
    static SoundManager soundManager = Minecraft.getInstance().getSoundManager();
    static final Random RANDOM = new Random();



    public static void playSound(TriggerConfig triggerConfig) {

        float pitch = triggerConfig.getPitchMin()
                + RANDOM.nextFloat()
                * (triggerConfig.getPitchMax() - triggerConfig.getPitchMin());

        int delay=triggerConfig.getDelayTicks()+1;
        for (int i = triggerConfig.getNoteCount();i>0;i--)
        {
            if (i== triggerConfig.getDelayTicks())
            {
                playSoundDelay(0,triggerConfig.getSound(),pitch,triggerConfig.getVolume());
            }

            playSoundDelay(delay,triggerConfig.getSound(),pitch,triggerConfig.getVolume());
            delay+=delay;
        }


    }

    public static void playSound(SoundEvent soundEvent) {
        playSound(soundEvent, 1, 1);
    }

    public static void playSound(SoundEvent soundEvent, float pitch, float volume) {
        SoundInstance soundInstance = getSoundInstance(soundEvent, pitch, volume);
        soundManager.play(soundInstance);
    }

    public static void playSound(ResourceLocation soundId, float pitch, float volume) {
        playSound(getSoundEventByName(soundId), pitch, volume);
    }

    public static void playSoundDelay(int delay, SoundEvent soundEvent, float pitch, float volume) {
        soundManager.playDelayed(getSoundInstance(soundEvent, pitch, volume), delay);
    }

    public static void playSoundDelay(int delay, ResourceLocation soundName, float pitch, float volume) {
        playSoundDelay(delay, getSoundEventByName(soundName), pitch, volume);
    }

    public static SoundEvent getSoundEventByName(ResourceLocation soundId) {

        return SoundEvent.createVariableRangeEvent(soundId);
    }

    public static SoundInstance getSoundInstance(SoundEvent soundEvent, float pitch, float volume) {
        return SimpleSoundInstance.forUI(soundEvent, pitch, volume);
    }
}
