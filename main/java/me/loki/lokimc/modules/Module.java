package me.loki.lokimc.modules;

import me.loki.lokimc.events.Event;
import net.minecraft.client.Minecraft;


public class Module {
    public String name;
    public boolean toggled;
    public int keyCode;
    public Category category;
    public Minecraft mc = Minecraft.getMinecraft();

    public Module(String name, int keyCode, Category category) {
        this.name = name;
        this.keyCode = keyCode;
        this.category = category;
    }

    public boolean isEnabled() {
        return toggled;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public void onEvent(Event event) {

    }

    public void toggle() {
        toggled = !toggled;
        if (toggled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    public void onEnable() {

    }

    public void onDisable() {

    }

    public enum Category {
        BOT,
        SWITCH_LOBBIES,
        MOVEMENT
    }
}
