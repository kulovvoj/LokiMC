package me.loki.lokimc.modules.bot;

import me.loki.lokimc.events.Event;
import me.loki.lokimc.modules.Module;
import me.loki.lokimc.utils.camera.CameraAngle;
import me.loki.lokimc.utils.camera.CameraRotation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Keyboard;

import static java.lang.Math.abs;

public class AutoMine2 extends Module {
    BlockPos minedBlock;
    CameraAngle nextAngle;
    CameraRotation rotation;

    public AutoMine2() {
        super("AutoMine2", Keyboard.KEY_M, Module.Category.BOT);
    }

    public void onEnable() {
        minedBlock = null;
        nextAngle = null;
        System.out.println("Player angles... Yaw: " + mc.thePlayer.rotationYaw + " Pitch: " + mc.thePlayer.rotationPitch);
    }

    public void onDisable() {
        if (minedBlock != null) {
            mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, minedBlock, EnumFacing.DOWN));
        }
    }

    private boolean isMythril(BlockPos position) {
        String[] mineableBlocks = {"minecraft:stained_hardened_clay[color=cyan]",
                "minecraft:wool[color=gray]",
                "minecraft:prismarine[variant=dark_prismarine]",
                "minecraft:prismarine[variant=prismarine_bricks",
                "minecraft:prismarine[variant=prismarine]",
                "minecraft:wool[color=light_blue]"};
        IBlockState mouseoverBlockState = mc.theWorld.getBlockState(position);

        for (String i: mineableBlocks) {
            if (mouseoverBlockState.toString().contains(i)) {
                return true;
            }
        }
        return false;
    }

    private boolean isBedrock(BlockPos position) {
        String bedrock = "minecraft:bedrock";

        IBlockState mouseoverBlockState = mc.theWorld.getBlockState(position);
        return mouseoverBlockState.toString().contains(bedrock);
    }

    private boolean isDiorite(BlockPos position) {
        String diorite = "minecraft:stone[variant=smooth_diorite]";

        IBlockState mouseoverBlockState = mc.theWorld.getBlockState(position);
        return mouseoverBlockState.toString().contains(diorite);
    }

    private MovingObjectPosition raytraceBlock (CameraAngle target) {
        double playerReach = (double)mc.playerController.getBlockReachDistance();
        Vec3 playerVec = mc.thePlayer.getPositionEyes(1.0F);
        Vec3 viewVec = mc.thePlayer.getVectorForRotation(target.pitch, target.yaw);
        Vec3 reachVec = playerVec.addVector(viewVec.xCoord * playerReach, viewVec.yCoord * playerReach, viewVec.zCoord * playerReach);
        MovingObjectPosition tracedObject = mc.theWorld.rayTraceBlocks(playerVec, reachVec, false, false, true);
        System.out.println("Traced object: " + tracedObject.getBlockPos());
        return tracedObject;
    }

    private void handleMining(BlockPos targetBlockPos, EnumFacing targetBlockFace) {
        // If a block is not being mined
        if (minedBlock == null) {
            if (isMythril(targetBlockPos) || isDiorite(targetBlockPos) || isBedrock(targetBlockPos)) {
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

    private CameraAngle targetReachableBlock() {
        CameraAngle playerCameraAngle = new CameraAngle(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
        CameraAngle targetCameraAngle = new CameraAngle(0, 0);
        return targetCameraAngle;
    }

    public void onEvent(Event event) {
        BlockPos targetBlockPos = mc.objectMouseOver.getBlockPos();
        EnumFacing targetBlockFace = mc.objectMouseOver.sideHit;
        handleMining(targetBlockPos, targetBlockFace);
        if (!(isMythril(targetBlockPos) || isDiorite((targetBlockPos))) || nextAngle != null) {
            if (nextAngle == null) {
                nextAngle = targetReachableBlock();
                if (nextAngle != null) {
                    rotation = new CameraRotation(new CameraAngle(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch), nextAngle);
                    System.out.println("Next block: " + nextAngle.yaw + "; " + nextAngle.pitch);
                }
                else
                    System.out.println("Next block: null");
            } else {
                rotation.onTick();
                if (0.1F > abs(nextAngle.yaw - mc.thePlayer.rotationYaw) && 0.1F > abs(nextAngle.pitch - mc.thePlayer.rotationPitch)) {
                    nextAngle = null;
                    rotation = null;
                }
            }
        }
        if (!(isMythril(targetBlockPos) || isDiorite((targetBlockPos))) && nextAngle != null) {
            if (targetBlockPos.isAtSamePositionAs(raytraceBlock(nextAngle).getBlockPos())) {
                nextAngle = null;
            }
        }
    }
}
