package me.aleiv.core.paper.gui;

import org.bukkit.Location;

import lombok.Getter;
import lombok.Setter;
import us.jcedeno.libs.rapidinv.RapidInv;

public class DecoGUIBase extends RapidInv{

    @Getter @Setter Location location;
    @Getter @Setter String name;

    public DecoGUIBase(int slots, String name, Location location) {
        super(slots, name);
        this.name = name;
        this.location = location;
    }
}
