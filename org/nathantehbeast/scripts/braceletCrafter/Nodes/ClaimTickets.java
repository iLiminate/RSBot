package org.nathantehbeast.scripts.braceletCrafter.Nodes;

import org.powerbot.core.randoms.SpinTickets;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.game.api.methods.Settings;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.wrappers.node.Item;

public class ClaimTickets extends Node {

    @Override
    public boolean activate() {
        return Inventory.contains(SpinTickets.ITEM_ID_SPIN_TICKET);
    }

    @Override
    public void execute() {
        if (((Settings.get(1448) & 0xFF00) >>> 8) < 10) {
            final Item ticket = Inventory.getItem(SpinTickets.ITEM_ID_SPIN_TICKET);
            if (ticket != null && ticket.getWidgetChild().interact("Claim spin")) {
                sleep(1000);
            }
        }
    }
}