package me.loki.lokimc.modules.bot;

import me.loki.lokimc.events.Event;
import me.loki.lokimc.events.listeners.EventUpdate;
import me.loki.lokimc.modules.Module;
import org.lwjgl.input.Keyboard;

import static java.lang.Math.floorDiv;
import static java.lang.Math.random;

public class FishingBot extends Module {
    int ticksTotal;
    int ticksAction;
    float rotationYaw;
    float rotationPitch;

    public FishingBot() {
        super("FishingBot", Keyboard.KEY_J, Category.BOT);
    }

    public void onEnable() {
        System.out.println("Enabled");
        ticksTotal = ticksAction = 0;
        rotationYaw = mc.thePlayer.rotationYaw;
        rotationPitch = mc.thePlayer.rotationPitch;
    }

    public void onDisable() {
        mc.gameSettings.keyBindAttack.setPressed(false);
    }

    public void onUpdate() {
    }

    public void cast() {
        System.out.println("swinging");
        mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
    }

    public void killEntities() {

    }

    public void onEvent(Event event) {
        //ticksTotal++;
        ticksAction++;
        if (event instanceof EventUpdate) {
            if (event.isPre()) {
                if (mc.thePlayer.fishEntity == null && ticksTotal >= 6000) {
                    ticksTotal = 0;
                } else if (mc.thePlayer.fishEntity != null && ((mc.thePlayer.fishEntity.ticksExisted > 40 && (mc.thePlayer.fishEntity.motionY < -0.02 || mc.thePlayer.fishEntity.motionY > 0.02) && ticksAction > 50) || (ticksAction > 400))) {
                    System.out.println("Y speed: " + mc.thePlayer.fishEntity.motionY);
                    ticksAction = 0;
                    mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
                } else if (mc.thePlayer.fishEntity == null && mc.thePlayer.getHeldItem().getItem().getUnlocalizedName().contains("fishingRod") && ticksAction > 20) {
                    ticksAction = 0;
                    cast();
                }
            }
        }
        mc.thePlayer.rotationYaw = getRandomRotation(0.1F)[0];
        mc.thePlayer.rotationPitch = getRandomRotation(0.1F)[1];
    }

    public float[] getRandomRotation(float deviation) {
        float tempYaw = (mc.thePlayer.rotationYaw + ((float)random() - 0.5F) * deviation + rotationYaw) / 2;
        float tempPitch = (mc.thePlayer.rotationPitch + ((float)random() - 0.5F) * deviation + rotationPitch) / 2;
        return new float[] {tempYaw, tempPitch};
    }
}

