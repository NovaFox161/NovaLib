package org.dreamexposure.novalib.nms.v1_9_R2;

import net.minecraft.server.v1_9_R2.Entity;
import net.minecraft.server.v1_9_R2.EntityArmorStand;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftEntity;
import org.dreamexposure.novalib.api.bukkit.packets.IArmorStand;

/**
 * @author NovaFox161
 * Date Created: 10/29/2018
 * For Project: NovaLib
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
public class ArmorStand implements IArmorStand {
    @Override
    public void setNoClip(org.bukkit.entity.ArmorStand armorStand, boolean clip) {
        Entity armorEntity = ((CraftEntity) armorStand).getHandle();
        
        EntityArmorStand entityArmorStand = (EntityArmorStand) armorEntity;
        
        entityArmorStand.noclip = clip;
    }
}
