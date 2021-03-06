package org.nathantehbeast.scripts.braceletCrafter.Nodes;

import org.nathantehbeast.api.framework.XNode;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.wrappers.Area;
import org.powerbot.game.api.wrappers.Tile;

/**
 * Created with IntelliJ IDEA.
 * User: Nathan
 * Date: 5/26/13
 * Time: 4:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class WalkBank implements XNode {

    private int goldId;
    private Area bankArea;

    public WalkBank(int goldId, Area bankArea) {
        this.goldId = goldId;
        this.bankArea = bankArea;
    }

    @Override
    public boolean activate() {
        return !Inventory.contains(goldId) && !bankArea.contains(Players.getLocal().getLocation());
    }

    @Override
    public void execute() {
        Walking.walk(new Tile(3097, 3496, 0));
    }
}
