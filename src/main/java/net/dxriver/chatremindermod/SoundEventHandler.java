package net.dxriver.chatremindermod;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.sound.SoundEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = ChatReminderMod.MODID, value = Dist.CLIENT)
public class SoundEventHandler {
    @SubscribeEvent
    public static void onSound(SoundEvent soundEventClient){

    }
}
