/*
 *  Copyright (c) 2017, Kronos <https://github.com/KronosDesign>
 *  Copyright (c) 2017, Adam <Adam@sigterm.info>
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice, this
 *     list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.kgummy.osrs.orbs;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class OrbPathOverlay extends Overlay
{
	private final Client client;
	private OrbPlugin plugin;

	@Inject
	public OrbPathOverlay(OrbPlugin plugin, Client client) {
		setPosition(OverlayPosition.DYNAMIC);
		this.client = client;
		this.plugin = plugin;
		setPriority(OverlayPriority.HIGH);
	}


	@Override
	public Dimension render(Graphics2D graphics) {

		for (NPC stupidOrb : plugin.getSpawnedOrbs()) {
			renderDangerousTiles(graphics, stupidOrb);
		}

		return null;
	}

	public void renderDangerousTiles(Graphics2D graphics, NPC stupidOrb){

		// each entity gets 4 polygons rendered
		// curr tile - red
		// next tile up - red
		// next tile up - orange
		// next tile up - yellow

		List<OrbDetails> polygons = new ArrayList<>();
		LocalPoint lp = LocalPoint.fromWorld(client, stupidOrb.getWorldLocation()); // centered on sw tile

		final LocalPoint centerLp = new LocalPoint(
				lp.getX() + Perspective.LOCAL_TILE_SIZE * (1 - 1) / 2,
				lp.getY() + Perspective.LOCAL_TILE_SIZE * (1 - 1) / 2);
		Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client, centerLp, 1);
		polygons.add(new OrbDetails(tilePoly,new Color(255, 0, 0, 84)));

		getNextTile(polygons, stupidOrb);

		for (OrbDetails orbDetails : polygons) {
			if (orbDetails.getPolygon() != null) {
				graphics.setColor(Color.BLACK);
				graphics.setStroke(new BasicStroke(1));
				graphics.draw(orbDetails.getPolygon());
				graphics.setColor(orbDetails.getFillColor());
				graphics.fill(orbDetails.getPolygon());
			}
		}
	}

	public void getNextTile(List<OrbDetails> polygons, NPC stupidOrb){
		/*
		 * 	   value between 0-2047
		 *     0 is true South
		 *     512 is true West
		 *     1024 is true North
		 *     1536 is true East
		 */
		int cardinality = stupidOrb.getCurrentOrientation();

		LocalPoint lp = LocalPoint.fromWorld(client, stupidOrb.getWorldLocation());
		LocalPoint nextLp = null;
		LocalPoint nextNextLp = null;
		LocalPoint nextNextNextLp = null;
		int tileSize = Perspective.LOCAL_TILE_SIZE;
		
		if (cardinality == 1024){ //north
			nextLp = new LocalPoint(lp.getX(), lp.getY() + tileSize);
			nextNextLp = new LocalPoint(nextLp.getX(), nextLp.getY() + tileSize);
			nextNextNextLp = new LocalPoint(nextNextLp.getX(), nextNextLp.getY() + tileSize);
		} else if (cardinality == 1280){ //northeast
			nextLp = new LocalPoint(lp.getX() + tileSize, lp.getY() + tileSize);
			nextNextLp = new LocalPoint(nextLp.getX() + tileSize, nextLp.getY() + tileSize);
			nextNextNextLp = new LocalPoint(nextNextLp.getX() + tileSize, nextNextLp.getY() + tileSize);
		} else if (cardinality == 1536){ // east
			nextLp = new LocalPoint(lp.getX() + tileSize, lp.getY() );
			nextNextLp = new LocalPoint(nextLp.getX() + tileSize, nextLp.getY() );
			nextNextNextLp = new LocalPoint(nextNextLp.getX() + tileSize, nextNextLp.getY());
		} else if (cardinality == 1792){ // southeast
			nextLp = new LocalPoint(lp.getX() + tileSize, lp.getY()  - tileSize);
			nextNextLp = new LocalPoint(nextLp.getX() + tileSize, nextLp.getY()  - tileSize);
			nextNextNextLp = new LocalPoint(nextNextLp.getX() + tileSize, nextNextLp.getY() - tileSize);
		} else if (cardinality == 0){ // south
			nextLp = new LocalPoint(lp.getX(), lp.getY()  - tileSize);
			nextNextLp = new LocalPoint(nextLp.getX(), nextLp.getY()  - tileSize);
			nextNextNextLp = new LocalPoint(nextNextLp.getX(), nextNextLp.getY() - tileSize);
		} else if (cardinality == 256){ // southwest
			nextLp = new LocalPoint(lp.getX() - tileSize, lp.getY()  - tileSize);
			nextNextLp = new LocalPoint(nextLp.getX() - tileSize, nextLp.getY()  - tileSize);
			nextNextNextLp = new LocalPoint(nextNextLp.getX() - tileSize, nextNextLp.getY() - tileSize);
		} else if (cardinality == 512){ // west
			nextLp = new LocalPoint(lp.getX() - tileSize, lp.getY());
			nextNextLp = new LocalPoint(nextLp.getX() - tileSize, nextLp.getY());
			nextNextNextLp = new LocalPoint(nextNextLp.getX() - tileSize, nextNextLp.getY());
//		} else if (cardinality == 768){ // northwest
		} else { // northwest
			nextLp = new LocalPoint(lp.getX() - tileSize, lp.getY() + tileSize);
			nextNextLp = new LocalPoint(nextLp.getX() - tileSize, nextLp.getY() + tileSize);
			nextNextNextLp = new LocalPoint(nextNextLp.getX() - tileSize, nextNextLp.getY() + tileSize);
		}
		polygons.add(new OrbDetails(Perspective.getCanvasTileAreaPoly(client, nextLp, 1), new Color(244, 57, 0, 84)));
		polygons.add(new OrbDetails(Perspective.getCanvasTileAreaPoly(client, nextNextLp, 1), new Color(224, 126, 28, 105)));
		polygons.add(new OrbDetails(Perspective.getCanvasTileAreaPoly(client, nextNextNextLp, 1), new Color(245, 163, 71, 150)));
	}

}
