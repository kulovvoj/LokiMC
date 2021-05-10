package me.loki.lokimc.modules.bot;

import me.loki.lokimc.LokiMC;
import me.loki.lokimc.events.Event;
import me.loki.lokimc.modules.Module;
import me.loki.lokimc.utils.camera.CameraAngle;
import me.loki.lokimc.utils.camera.CameraRotation;
import me.loki.lokimc.utils.other.ProximitySensor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Math.*;

public class AutoMine extends Module {
    BlockPos minedBlock;
    CameraAngle nextAngle;
    CameraRotation rotation;
    boolean wait;
    long waitTime, waitTotal;
    ProximitySensor proximitySensor;

    public AutoMine() {
        super("AutoMine", Keyboard.KEY_L, Category.BOT);
    }

    public void onEnable() {
        minedBlock = null;
        nextAngle = null;
        rotation = null;
        wait = false;
        waitTime = 0;
        waitTotal = 0;
        proximitySensor = new ProximitySensor(8, 100);
        //System.out.println("Player angles... Yaw: " + mc.thePlayer.rotationYaw + " Pitch: " + mc.thePlayer.rotationPitch);
    }

    public void onDisable() {
        if (minedBlock != null) {
            mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, minedBlock, EnumFacing.DOWN));
        }
        if (mc.thePlayer.isSneaking()) {
            mc.thePlayer.setSneaking(false);
            mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
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

    private MovingObjectPosition raytraceBlock (CameraAngle target) {
        double playerReach = (double)mc.playerController.getBlockReachDistance();
        Vec3 playerVec = mc.thePlayer.getPositionEyes(1.0F);
        Vec3 viewVec = mc.thePlayer.getVectorForRotation(target.pitch, target.yaw);
        Vec3 reachVec = playerVec.addVector(viewVec.xCoord * playerReach, viewVec.yCoord * playerReach, viewVec.zCoord * playerReach);
        MovingObjectPosition tracedObject = mc.theWorld.rayTraceBlocks(playerVec, reachVec, false, false, true);
        //System.out.println("Traced object: " + tracedObject.getBlockPos());
        return tracedObject;
    }

    private CameraAngle targetReachableBlock() {
        // get all corners in 4.45 reach
        // get all blocks with those corners
        // get the ones that their face borders air
        // get blocks that are mineable
        // get the faces that could be seen from your position
        // place points on their faces bordering air
        // raytrace to those points

        // get all points differing by multiples of 3 degrees from yaw and pitch
        // raytrace those points to blocks
        // get closest good raytrace, return that yaw and pitch
        CameraAngle playerCameraAngle = new CameraAngle(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
        CameraAngle targetCameraAngle = new CameraAngle(0, 0);
        float degreeDif = 9;
        for (float i = 0; i < 10; i++) {
            for (float j = -1; j <= 1; j += 2) {
                for (float k = -1; k <= 1; k += 2) {
                    List<Integer> range = IntStream.rangeClosed(0, (int)i)
                        .boxed().collect(Collectors.toList());
                    Random rand = new Random();
                    for (float l = (float)range.remove(rand.nextInt(range.size())); !range.isEmpty(); l = (float)range.remove(rand.nextInt(range.size()))
                    ) {
                        targetCameraAngle.yaw = playerCameraAngle.yaw + l * degreeDif * j;
                        targetCameraAngle.pitch = playerCameraAngle.pitch + (i - l) * degreeDif * k;
                        targetCameraAngle.normalizeRotation();
                        if (abs(targetCameraAngle.pitch) > 60) continue;
                        MovingObjectPosition tracedObject = raytraceBlock(targetCameraAngle);
                        if (tracedObject != null && (isMythril(tracedObject.getBlockPos()) || isDiorite(tracedObject.getBlockPos()))) {
                            CameraAngle res = targetCameraAngle;
                            return res;
                        }
                    }
                }
            }
        }
/*
            for (float j = degreeDif * i; j >= -degreeDif * i; j = j - degreeDif) {
                if (abs(j) == degreeDif * i) {
                        for (float k = degreeDif * i; k >= -degreeDif * i; k = k - degreeDif) {
                            targetYaw = playerYaw + j;
                            targetPitch = playerPitch + k;
                            System.out.println("Trying yaw: " + targetYaw + " and pitch: " + targetPitch);
                            MovingObjectPosition tracedObject = raytraceBlock(targetYaw, targetPitch);
                            if (tracedObject != null && (isMythril(tracedObject.getBlockPos()) || isDiorite(tracedObject.getBlockPos()))) {
                                float[] res = {targetYaw, targetPitch};
                                return res;
                            }
                    }
                } else {
                    for (float k = degreeDif * i; k >= -degreeDif * i; k = k - 2 * degreeDif * i) {
                        targetYaw = playerYaw + j;
                        targetPitch = playerPitch + k;
                        System.out.println("Trying yaw: " + targetYaw + " and pitch: " + targetPitch);
                        MovingObjectPosition tracedObject = raytraceBlock(targetPitch, targetYaw);
                        if (tracedObject != null && (isMythril(tracedObject.getBlockPos()) || isDiorite(tracedObject.getBlockPos()))) {
                            float[] res = {targetYaw, targetPitch};
                            return res;
                        }
                    }
                }
            }
        }
 */
        return null;
    }

    private void randomSneak() {
        Random rand = new Random();
        if (rand.nextDouble() < 0.5) {
            if (!mc.thePlayer.isSneaking()) {
                mc.thePlayer.setSneaking(true);
            } else {
                mc.thePlayer.setSneaking(false);
            }
        }
    }

    public void onEvent(Event event) {
        if (proximitySensor.onTick()) {
            LokiMC.modules.get(3).toggle();
        }
        if (wait == true) {
            if (waitTime < waitTotal) {
                waitTime++;
                return;
            } else {
                wait = false;
            }
        }

        BlockPos targetBlockPos = mc.objectMouseOver.getBlockPos();
        EnumFacing targetBlockFace = mc.objectMouseOver.sideHit;
        if (targetBlockPos != null) handleMining(targetBlockPos, targetBlockFace);
        if (!(isMythril(targetBlockPos) || isDiorite((targetBlockPos))) || nextAngle != null) {
            if (nextAngle == null) {
                Random rand = new Random();
                waitTotal = 2 + 5 - (int)sqrt(rand.nextInt(25));
                waitTime = 0;
                wait = true;
                randomSneak();
                nextAngle = targetReachableBlock();
                if (nextAngle != null) {
                    rotation = new CameraRotation(new CameraAngle(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch), nextAngle);
                }
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
