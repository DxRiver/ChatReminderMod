package net.dxriver.chatremindermod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = ChatReminderMod.MODID, value = Dist.CLIENT)
public class ClientSoundScheduler {
    private static final Queue<SoundEntry> SOUND_QUEUE = new ConcurrentLinkedQueue<>();


    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START && !SOUND_QUEUE.isEmpty()) {

            SoundEntry entry = SOUND_QUEUE.poll();
            if (entry != null) {

                SoundEntry updatedEntry = entry.decrementDelay();
                if (updatedEntry.delayTicks() <= 0) {

                    Minecraft.getInstance().getSoundManager().play(
                            SimpleSoundInstance.forUI(
                                    entry.sound(),
                                    entry.pitch(),
                                    entry.volume()
                            )
                    );
                } else {

                    SOUND_QUEUE.add(updatedEntry);
                }
            }
        }
    }

    public static void addSequence(SoundEvent sound, int delayTicks, float volume, float pitch) {
        SOUND_QUEUE.add(new SoundEntry(sound, delayTicks, volume, pitch));
    }


    private static final record SoundEntry(
            SoundEvent sound,
            int delayTicks,
            float volume,
            float pitch
    ) {

        public SoundEntry decrementDelay() {
            return new SoundEntry(sound, delayTicks - 1, volume, pitch);
        }
    }
}