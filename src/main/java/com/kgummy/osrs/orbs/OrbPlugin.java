/*
 * Copyright (c) 2019 Hydrox6 <ikada@protonmail.ch>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.kgummy.osrs.orbs;

import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.npcoverlay.NpcOverlayService;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@PluginDescriptor(
	name = "AkkahOrbs",
	description = "Shows dangerous Akkah orb tiles",
	tags = {"toa", "orbs"}
)
public class OrbPlugin extends Plugin
{
	@Inject
	private Client client;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private NpcOverlayService npcOverlayService;
	@Inject
	private ClientThread clientThread;
	@Inject
	private OrbPathOverlay orbPathOverlay;
	@Inject
	private EventBus eventBus;

	private final List<NPC> spawnedOrbs = new ArrayList<>();

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(orbPathOverlay);
		clientThread.invoke(() ->
		{
			rebuild();
		});
	}

	@Override
	protected void shutDown() throws Exception
	{
		this.spawnedOrbs.clear();
		overlayManager.remove(orbPathOverlay);
	}

	void rebuild()
	{
		if (client.getGameState() != GameState.LOGGED_IN &&
				client.getGameState() != GameState.LOADING)
		{
			// NPCs are still in the client after logging out,
			// but we don't want to highlight those.
			return;
		}

		for (NPC npc : client.getNpcs())
		{
			final String npcName = npc.getName();
			System.out.println(npcName);
		}
		npcOverlayService.rebuild();
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned npcSpawned) {
		final NPC npc = npcSpawned.getNpc();
		// first - black orb, second is white orb
		if (npc.getId() == 11708 || npc.getId() == 11804) {
			spawnedOrbs.add(npcSpawned.getNpc());
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned npcDespawned) {
		final NPC npc = npcDespawned.getNpc();

		if (npc.getId() == 11708 || npc.getId() == 11804) {
			spawnedOrbs.remove(npc);
		}
	}

	public List<NPC> getSpawnedOrbs() {
		return spawnedOrbs;
	}

}
