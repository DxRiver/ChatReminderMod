package net.dxriver.chatremindermod;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = ChatReminderMod.MODID, value = Dist.CLIENT)
class ChatEventHandler {


    @SubscribeEvent
    public static void onChat(ClientChatReceivedEvent event) {

        Reminder.msgCheck(event);

    }

}


