package com.mordenkainen.equivalentenergistics.util;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiErrorScreen;

import cpw.mods.fml.client.CustomModLoadingErrorDisplayException;

public class UnmetDependencyException extends CustomModLoadingErrorDisplayException {
	
	private static final long serialVersionUID = 3959069021401895129L;
	private final String[] errorMessage;
	
	public UnmetDependencyException() {
		errorMessage = "Equivalent Energistics has stopped Minecraft Loading.\nA required dependency was not found.\nEither Equivalent Exchange 3 or ProjectE must be installed and enabled!".split("\n");
	}

	@Override
	public void initGui(GuiErrorScreen errorScreen, FontRenderer fontRenderer) {}

	@Override
	public void drawScreen(GuiErrorScreen errorScreen, FontRenderer fontRenderer, int mouseRelX, int mouseRelY, float tickTime) {
		for (int i = 0; i < errorMessage.length; i++)
        {
            errorScreen.drawCenteredString(fontRenderer, errorMessage[i], errorScreen.width / 2, (errorScreen.height - fontRenderer.FONT_HEIGHT * errorMessage.length) / 2
                    + fontRenderer.FONT_HEIGHT + 10 * i, 0xffaabbcc);
        }
	}

}
