package me.loki.lokimc.modules.bot;


import me.loki.lokimc.events.Event;
import me.loki.lokimc.events.listeners.EventUpdate;
import me.loki.lokimc.modules.Module;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.lwjgl.input.Keyboard;

import static net.optifine.CustomColors.random;

public class ForagingBot extends Module {
    int state;
    int digState;
    boolean hooked;
    boolean nuke;
    BlockPos minedBlock;

    public ForagingBot() {
        super("ForagingBot", Keyboard.KEY_M, Category.BOT);
    }

    public void onEnable() {
        System.out.println("Enabled");
        state = 0;
        hooked = false;
        nuke = true;
    }

    public void onDisable() {
        if (minedBlock != null) {
        }
    }

    public void onUpdate() {
    }

    private boolean isWood(BlockPos targetBlockPos) {

        String blockName = mc.theWorld.getBlockState(targetBlockPos).getBlock().getLocalizedName();
        return blockName.contains("Wood");

    }

    private void handleMining(BlockPos targetBlockPos, EnumFacing targetBlockFace) {
        // If a block is not being mined
        if (minedBlock == null) {
            if (isWood(targetBlockPos)) {
                minedBlock = targetBlockPos;
                mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, targetBlockPos, targetBlockFace));
                mc.thePlayer.swingItem();
            }
            // If a block is being mined
        } else {
            if (minedBlock.getX() != targetBlockPos.getX() || minedBlock.getY() != targetBlockPos.getY() || minedBlock.getZ() != targetBlockPos.getZ()) {
                mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, minedBlock, EnumFacing.DOWN));
                minedBlock = null;
            }
        }
        if (minedBlock != null) {
            mc.thePlayer.swingItem();
        }
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
        if (blockName.contains("Wood")) {
            if (mc.theWorld.getBlockState(blockPos).getBlock().getMaterial() != Material.air && mc.playerController.onPlayerDamageBlock(blockPos, side))
            {
                mc.effectRenderer.addBlockHitEffects(blockPos, side);
                mc.thePlayer.swingItem();
            }
        }
    }
}