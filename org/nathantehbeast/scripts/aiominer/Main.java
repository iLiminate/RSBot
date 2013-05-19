package org.nathantehbeast.scripts.aiominer;


import org.nathantehbeast.api.framework.Condition;
import org.nathantehbeast.api.tools.Utilities;
import org.powerbot.core.Bot;
import org.powerbot.core.event.events.MessageEvent;
import org.powerbot.core.event.listeners.MessageListener;
import org.powerbot.core.event.listeners.PaintListener;
import org.powerbot.core.script.ActiveScript;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.input.Keyboard;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.methods.tab.Skills;
import org.powerbot.game.api.methods.widget.WidgetCache;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.wrappers.Locatable;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.node.SceneObject;
import org.powerbot.game.client.Client;
import sk.action.ActionBar;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Nathan
 * Date: 5/11/13
 * Time: 3:52 AM
 * To change this template use File | Settings | File Templates.
 */

@Manifest(
        authors         = "NathanTehBeast",
        name            = "Nathan's AIO Miner",
        description     = "AIO Miner, Alpha testing phase. Powermining only for now; banking support to be added later.",
        hidden          = true,
        vip             = false,
        singleinstance  = false,
        version         = 1.2
)

public final class Main extends ActiveScript implements MessageListener, PaintListener {

    private static boolean powermine = true;
    private static Constants.Ore ore = Constants.Ore.COPPER;
    private final double version = getClass().getAnnotation(Manifest.class).version();
    private final String user = Bot.context().getDisplayName();
    private Client client;
    private Node currentNode = null;
    public static ArrayList<Node> nodes = new ArrayList<>();
    public static Tile startTile = null;
    public static int radius = 1;
    public static long startTime, runTime;
    public static boolean start = false;
    public static Filter<SceneObject> FILTER = new Filter<SceneObject>() {
        @Override
        public boolean accept(SceneObject sceneObject) {
            return sceneObject != null && isInArea(sceneObject) && Utilities.contains(ore.rocks, sceneObject.getId());
        }
    };
    private static int startExp, expHour, expGained;

    @Override
    public void onStart() {
        System.out.println("Welcome " + user);
        System.out.println("You are using version " + version);
        new GUI();
    }

    @Override
    public int loop() {
        if (Game.getClientState() != Game.INDEX_MAP_LOADED) {
            return 1000;
        }
        if (client != Bot.client()) {
            WidgetCache.purge();
            Bot.context().getEventManager().addListener(this);
            client = Bot.client();
        }
        for (Node node : nodes) {
            if (node.activate()) {
                currentNode = node;
                node.execute();
            }
        }
        if (!start && Game.isLoggedIn()) {
            startTile = Players.getLocal().getLocation();
        }
        return 600;
    }

    @Override
    public void onStop() {
        System.out.println("Thanks for using Nathan's AIO Miner!");
    }

    public static void setOre(Constants.Ore orex) {
        ore = orex;
    }

    public static Constants.Ore getOre() {
        return ore;
    }

    public static void setPowermine(boolean b) {
        powermine = b;
    }

    public static boolean getPowermine() {
        return powermine;
    }

    public static boolean setStartXP() {
        startExp = Skills.getExperience(Skills.MINING);
        return startExp != 0;
    }

    @Override
    public void messageReceived(MessageEvent me) {
        if (me.getMessage().toLowerCase().contains("cya nerds")) {
            ActionBar.expand(false);
            Utilities.waitFor(new Condition() {
                @Override
                public boolean validate() {
                    return !ActionBar.isExpanded();
                }
            }, 3000);
            Keyboard.sendText("Bye!", true, 100, 200);
            if (Players.getLocal().isInCombat()) {
                Utilities.waitFor(new Condition() {
                    @Override
                    public boolean validate() {
                        return !Players.getLocal().isInCombat();
                    }
                }, 30000);
            }
            Game.logout(false);
            stop();
        }
    }

    @Override
    public void onRepaint(Graphics g) {
        runTime = System.currentTimeMillis() - startTime;
        expGained = Skills.getExperience(Skills.MINING) - startExp;
        expHour = (int) ((3600000.0 / runTime) * expGained);
        final SceneObject[] ROCKS = SceneEntities.getLoaded(new Filter<SceneObject>() {
            @Override
            public boolean accept(SceneObject sceneObject) {
                return sceneObject != null && Utilities.contains(ore.rocks, sceneObject.getId()) && isInArea(sceneObject);
            }
        });


        Graphics2D g2d = (Graphics2D) g;

        g2d.drawString("Run Time: " + Time.format(runTime), 5, 85);
        if (currentNode != null) {
            g2d.drawString("Current node: "+currentNode, 5, 100);
        }
        g2d.drawString("XP per Hour: "+expHour, 5, 115);

        if (startTile != null) {
            g2d.setColor(goldT);
            Point p = Calculations.worldToMap(startTile.getX(), startTile.getY());
            g2d.fillOval(p.x - (radius * 5), p.y - (radius * 5), 5 * (radius * 2), 5 * (radius * 2));
            g2d.setColor(gold);
            g2d.drawOval(p.x - (radius * 5), p.y - (radius * 5), 5 * (radius * 2), 5 * (radius * 2));
        }

        if (ROCKS != null) {
            for (final SceneObject so : ROCKS) {
                for (final Polygon p : so.getBounds()) {
                    g2d.setColor(green);
                    g2d.fill(p);
                }
            }
        }
    }

    private static final Color gold = new Color(255,215,0);
    private static final Color goldT = new Color(255, 215, 0, 150);
    private static final Color green = Color.GREEN;

    public static boolean isInArea(Locatable l) {
        return Calculations.distance(startTile, l) <= radius;
    }
}
