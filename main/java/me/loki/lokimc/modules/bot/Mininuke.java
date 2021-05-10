package me.loki.lokimc.modules.bot;


import me.loki.lokimc.events.Event;
import me.loki.lokimc.events.listeners.EventUpdate;
import me.loki.lokimc.modules.Module;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import org.lwjgl.input.Keyboard;

public class Mininuke extends Module {
    int state;
    boolean hooked;

    public Mininuke() {
        super("Mininuke", Keyboard.KEY_NONE, Category.MOVEMENT);
    }

    public void onEnable() {
        System.out.println("Enabled");
        state = 0;
        hooked = false;
        mc.gameSettings.keyBindAttack.setPressed(true);
    }

    public void onDisable() {
        mc.gameSettings.keyBindAttack.setPressed(false);
    }

    public void onEvent(Event event) {
        int r1 = 0, r2 = 0, r3 = 0, r4 = 0;
        if (event instanceof EventUpdate) {
            if (event.isPre()) {
                int x = mc.objectMouseOver.getBlockPos().getX();
                int y = mc.objectMouseOver.getBlockPos().getY();
                int z = mc.objectMouseOver.getBlockPos().getZ();
                BlockPos bp = mc.objectMouseOver.getBlockPos();

                if (mc.theWorld.getBlockState(bp).getBlock() != null && Block.getIdFromBlock(mc.theWorld.getBlockState(bp).getBlock()) != 0) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, bp, EnumFacing.UP));
                    mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, bp, EnumFacing.UP));
                }
            }
        }
    }
}
