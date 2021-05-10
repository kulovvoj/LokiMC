package me.loki.lokimc.modules.bot;

import me.loki.lokimc.events.Event;
import me.loki.lokimc.modules.Module;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.lwjgl.input.Keyboard;

public class CocoaPlant extends Module {

    public CocoaPlant() {
        super("CocoaPlant", Keyboard.KEY_K, Category.BOT);
    }

    public void onEnable() {
        System.out.println("Enabled");
    }

    public void onDisable() {

    }

    public void onUpdate() {
    }

    public void onEvent(Event event) {
        if (mc.objectMouseOver == null) return;
        BlockPos blockPos = mc.objectMouseOver.getBlockPos();
        EnumFacing enumFacing = mc.objectMouseOver.sideHit;
        if (blockPos == null) return;
        IBlockState blockState = mc.theWorld.getBlockState(blockPos);
        System.out.println(blockState);
        System.out.println("EnumFacing: " + enumFacing.toString() + "; " + enumFacing.getIndex());
        BlockPos blockPosFront = blockPos.add(enumFacing.getFrontOffsetX(), enumFacing.getFrontOffsetY(), enumFacing.getFrontOffsetZ());

        float facingX = (float)(mc.objectMouseOver.hitVec.xCoord - blockPos.getX());
        float facingY = (float)(mc.objectMouseOver.hitVec.yCoord - blockPos.getY());
        float facingZ = (float)(mc.objectMouseOver.hitVec.zCoord - blockPos.getZ());
        if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getDisplayName().contains("Cocoa Beans") && blockState.toString().contains("jungle") && mc.theWorld.getBlockState(blockPosFront).getBlock().getLocalizedName().contains("air")) {
            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(blockPos, enumFacing.getIndex(), mc.thePlayer.getHeldItem(), facingX, facingY, facingZ));
            mc.thePlayer.swingItem();
            System.out.println("PLACING");
        }
    }
}
