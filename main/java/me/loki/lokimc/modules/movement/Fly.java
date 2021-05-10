package me.loki.lokimc.modules.movement;

import me.loki.lokimc.events.Event;
import me.loki.lokimc.events.listeners.EventUpdate;
import me.loki.lokimc.modules.Module;
import org.lwjgl.input.Keyboard;

public class Fly extends Module {
    public Fly() {
        super("Fly", Keyboard.KEY_J, Category.MOVEMENT);
    }


    public void onEnable() {
        mc.thePlayer.capabilities.isFlying = true;
        mc.thePlayer.capabilities.allowFlying = true;
    }

    public void onDisable() {
        mc.thePlayer.capabilities.isFlying = false;
        mc.thePlayer.capabilities.allowFlying = false;
    }

    public void onEvent(Event event) {
        if (event instanceof EventUpdate) {
            if (event.isPre()) {
            }
        }
    }
}
