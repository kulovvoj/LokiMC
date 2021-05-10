package me.loki.lokimc;

import com.google.common.base.Throwables;
//import com.google.gson.GsonBuilder;
import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
//import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import me.loki.lokimc.events.Event;
import me.loki.lokimc.modules.Module;
//import daladirn.mahClint.modules.movement.Fly;
import me.loki.lokimc.modules.bot.*;
import net.minecraft.util.Session;
import org.lwjgl.opengl.Display;

import java.net.Proxy;
import java.util.concurrent.CopyOnWriteArrayList;

public class LokiMC {
    public static String name = "LokiMC", version = "1.0.0";
    public static CopyOnWriteArrayList<Module> modules = new CopyOnWriteArrayList<Module>();

    public static void startup() {
        System.out.println("Initiating " + name + " " + version);
        Display.setTitle(name + " v" + version);
        //attemptLogin();

        modules.add(new AutoSell());
        modules.add(new FishingBot());
        modules.add(new CocoaPlant());
        modules.add(new AutoMine());
        modules.add(new ForagingBot());
        modules.add(new WartFarmer());
    }

    public static void onEvent(Event event) {
        for (Module m : modules) {
            if (!m.isEnabled())
                continue;
            m.onEvent(event);
        }
    }

    public static void keyPress(int keyCode) {
        for (Module m : modules) {
            if (m.getKeyCode() == keyCode) {
                m.toggle();
            }
        }

    }

    public static Session attemptLogin()
    {
        YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) new YggdrasilAuthenticationService(Proxy.NO_PROXY, "1").createUserAuthentication(Agent.MINECRAFT);
        auth.setUsername("aliance080@seznam.cz");
        auth.setPassword("-vojtiSek007");

        try {
            auth.logIn();
            return new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), "mojang");
        }
        catch (AuthenticationException e)
        {
            Throwables.propagate(e);
            return null;
        }
    }
}
