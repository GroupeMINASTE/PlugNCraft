/*
 *  Copyright (C) 2018 FALLET Nathan
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 */

package fr.zabricraft.plugnreplica.events;

import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import fr.zabricraft.plugnapi.PlugNAPI;
import fr.zabricraft.plugnapi.utils.ServerStatus;
import fr.zabricraft.plugnreplica.PlugNReplica;
import fr.zabricraft.plugnreplica.utils.ZabriPlayer;

public class BlockBreak implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent e) {
		if (e.getBlock().getWorld().getName().equals("game")) {
			ZabriPlayer zp = PlugNReplica.getInstance().getPlayer(e.getPlayer().getUniqueId());
			if (e.getBlock().getLocation().getBlockY() != 64) {
				e.setCancelled(true);
				return;
			}
			if (e.getBlock().getLocation().getBlockZ() < 0 || e.getBlock().getLocation().getBlockZ() > 320) {
				e.setCancelled(true);
				return;
			}
			if (!e.getBlock().getType().equals(Material.STAINED_CLAY)) {
				e.setCancelled(true);
				return;
			}
			int z = e.getBlock().getChunk().getZ(), col = 0;
			while (z >= 2) {
				z -= 2;
				col++;
			}
			col++;
			for (UUID c : PlugNReplica.getInstance().getPlayers()) {
				if (e.getPlayer().getUniqueId().equals(c)) {
					if (PlugNAPI.getInstance().getStatus().equals(ServerStatus.PLAYING)) {
						if (zp.getPlot() == col) {
							e.setCancelled(false);
							if (PlugNReplica.getInstance().isCompletingPlot(col)) {
								PlugNReplica.getInstance().breakPlot(col);
								e.getPlayer().getInventory().clear();
								e.getPlayer().updateInventory();
								e.getPlayer().setGameMode(GameMode.SPECTATOR);
								zp.setFinish(true);
								PlugNReplica.getInstance().verifNext();
							}
						} else {
							e.setCancelled(true);
						}
					} else {
						e.setCancelled(true);
					}
					return;
				}
			}
			e.setCancelled(true);
		}
	}

}
