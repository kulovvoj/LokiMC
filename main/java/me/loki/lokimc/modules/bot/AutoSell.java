package me.loki.lokimc.modules.bot;


import me.loki.lokimc.events.Event;
import me.loki.lokimc.events.listeners.EventUpdate;
import me.loki.lokimc.modules.Module;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.lwjgl.input.Keyboard;

import static net.optifine.CustomColors.random;

public class AutoSell extends Module {
    int state;
    int digState;
    boolean hooked;
    boolean nuke;

    public AutoSell() {
        super("AutoSell", Keyboard.KEY_H, Category.BOT);
    }

    public void onEnable() {
        System.out.println("Enabled");
        state = 0;
        hooked = false;
        //mc.gameSettings.keyBindAttack.setPressed(true);
        //mn.toggle();
        nuke = true;
    }

    public void onDisable() {
        mc.gameSettings.keyBindAttack.setPressed(false);
    }

    public void onUpdate() {
    }

    public void onEvent(Event event) {
        int r1 = 0, r2 = 0, r3 = 0, r4 = 0;
        if (event instanceof EventUpdate) {
            if (event.isPre()) {
                if (mc.thePlayer.inventory.getFirstEmptyStack() == -1 || hooked) {
                    state += 1;
                    if (state == 1 && mc.thePlayer.openContainer.windowId == 0) {
                        hooked = true;
                        //mc.gameSettings.keyBindAttack.setPressed(false);
                        //mn.toggle();
                        nuke = false;
                        r1 = random.nextInt(20);
                        r2 = random.nextInt(20);
                        r3 = random.nextInt(20);
                        r4 = random.nextInt(20);
                        mc.thePlayer.sendChatMessage("/bz");

                    }
                    if (state == 61 + r1) {
                        mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, 38, 0 + random.nextInt(2), 0, mc.thePlayer);
                    }
                    if (state == 121 + r2) {
                        mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, 11, 0 + random.nextInt(2), 0, mc.thePlayer);
                    }
                    if (state == 181 + r3) {
                        mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, 15, 0 + random.nextInt(2), 0, mc.thePlayer);
                    }
                    if (state == 241 + r4) {
                        hooked = false;
                        //mc.gameSettings.keyBindAttack.setPressed(true);
                        //mn.toggle();
                        nuke = true;

                        state = 0;
                    }
                } else if (nuke) {
                    try {
                        dig();
                    } catch (Exception e) {
                        System.out.println("Exception!");
                    }
                }
            }
        }
    }

    public void dig() {
        if (mc.objectMouseOver == null) return;
        BlockPos blockPos = mc.objectMouseOver.getBlockPos();
        IBlockState blockState = mc.theWorld.getBlockState(blockPos);
        if (blockPos == null) return;
        String blockName = blockState.getBlock().getLocalizedName();
        EnumFacing side = mc.objectMouseOver.sideHit;
    System.out.println(blockName);
        if (blockName.contains("Cocoa")) {
            System.out.println("Digging.");
            mc.thePlayer.swingItem();
            mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, side));
            mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, side));
            mc.playerController.onPlayerDestroyBlock(blockPos, side);
            mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
        }
    }
}