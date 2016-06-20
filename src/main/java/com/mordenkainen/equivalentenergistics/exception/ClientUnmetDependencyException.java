package com.mordenkainen.equivalentenergistics.exception;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiErrorScreen;

import cpw.mods.fml.client.CustomModLoadingErrorDisplayException;

public class ClientUnmetDependencyException extends CustomModLoadingErrorDisplayException {
	
	private static final long serialVersionUID = 3959069021401895129L;
	private final String[] errorMessage;
	
	public ClientUnmetDependencyException() {
		super();
		errorMessage = "Equivalent Energistics has stopped Minecraft Loading.\nA required dependency was not found.\nEither Equivalent Exchange 3 or ProjectE must be installed and enabled!".split("\n");
	}

	@Override
	public void initGui(final GuiErrorScreen errorScreen, final FontRenderer fontRenderer) {}

	@Override
	public void drawScreen(final GuiErrorScreen errorScreen, final FontRenderer fontRenderer, final int mouseRelX, final int mouseRelY, final float tickTime) {
		for (int i = 0; i < errorMessage.length; i++) {
            errorScreen.drawCenteredString(fontRenderer, errorMessage[i], errorScreen.width / 2, (errorScreen.height - fontRenderer.FONT_HEIGHT * errorMessage.length) / 2
                    + fontRenderer.FONT_HEIGHT + 10 * i, 0xffaabbcc);
        }
	}

}
