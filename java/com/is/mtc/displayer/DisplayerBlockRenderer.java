package com.is.mtc.displayer;

import com.is.mtc.Reference;
import com.is.mtc.card.CardItem;
import com.is.mtc.data_manager.CardStructure;
import com.is.mtc.data_manager.Databank;
import com.is.mtc.root.Rarity;
import com.is.mtc.root.Tools;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import static net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher.instance;

public class DisplayerBlockRenderer extends TileEntitySpecialRenderer<DisplayerBlockTileEntity> {
	public void render(DisplayerBlockTileEntity tileEntity, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		DisplayerBlockTileEntity displayerBlockTileEntity = tileEntity;

		RenderHelper.disableStandardItemLighting();
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);

		// Facing north face
		//tessellator.startDrawingQuads();
		bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bindTextureForSlot(tessellator, displayerBlockTileEntity, 0);
		bufferBuilder.pos(x, y, z - 0.01).tex(0, 0).endVertex();
		bufferBuilder.pos(x, y + 1, z - 0.01).tex(0, 1).endVertex();
		bufferBuilder.pos(x + 1, y + 1, z - 0.01).tex(1, 1).endVertex();
		bufferBuilder.pos(x + 1, y, z - 0.01).tex(1, 0).endVertex();
		/*tessellator.addVertexWithUV(0, 0, 0 - 0.01D, 1, 1);
		tessellator.addVertexWithUV(0, 1D, 0 - 0.01D, 1, 0);
		tessellator.addVertexWithUV(1D, 1D, 0 - 0.01D, 0, 0);
		tessellator.addVertexWithUV(1D, 0, 0 - 0.01D, 0, 1);*/
		tessellator.draw();

		// Facing south face
		//tessellator.startDrawingQuads();
		bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bindTextureForSlot(tessellator, displayerBlockTileEntity, 1);
		bufferBuilder.pos(0, 0, 1.01).tex(0, 0).endVertex();
		bufferBuilder.pos(1, 1, 1.01).tex(1, 1).endVertex();
		bufferBuilder.pos(0, 1, 1.01).tex(0, 1).endVertex();
		bufferBuilder.pos(1, 0, 1.01).tex(1, 0).endVertex();
		/*tessellator.addVertexWithUV(1D, 0, 1.01D, 1, 1);
		tessellator.addVertexWithUV(1D, 1D, 1.01D, 1, 0);
		tessellator.addVertexWithUV(0, 1D, 1.01D, 0, 0);
		tessellator.addVertexWithUV(0, 0, 1.01D, 0, 1);*/
		tessellator.draw();

		// Facing east face
		//tessellator.startDrawingQuads();
		bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bindTextureForSlot(tessellator, displayerBlockTileEntity, 2);
		bufferBuilder.pos(1.01, 0, 0).tex(0, 0).endVertex();
		bufferBuilder.pos(1.01, 1, 0).tex(1, 0).endVertex();
		bufferBuilder.pos(1.01, 1, 1).tex(1, 1).endVertex();
		bufferBuilder.pos(1.01, 0, 1).tex(0, 1).endVertex();
		/*tessellator.addVertexWithUV(1.01D, 0, 0, 1, 1);
		tessellator.addVertexWithUV(1.01D, 1D, 0, 1, 0);
		tessellator.addVertexWithUV(1.01D, 1D, 1D, 0, 0);
		tessellator.addVertexWithUV(1.01D, 0, 1D, 0, 1);*/
		tessellator.draw();

		// Facing west face
		//tessellator.startDrawingQuads();
		bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bindTextureForSlot(tessellator, displayerBlockTileEntity, 3);
		bufferBuilder.pos(0 - 0.01, 0, 0).tex(0, 0).endVertex();
		bufferBuilder.pos(0 - 0.01, 1, 0).tex(1, 0).endVertex();
		bufferBuilder.pos(0 - 0.01, 1, 1).tex(1, 1).endVertex();
		bufferBuilder.pos(0 - 0.01, 0, 1).tex(0, 1).endVertex();
		/*tessellator.addVertexWithUV(0 - 0.01D, 0, 1D, 1, 1);
		tessellator.addVertexWithUV(0 - 0.01D, 1D, 1D, 1, 0);
		tessellator.addVertexWithUV(0 - 0.01D, 1D, 0, 0, 0);
		tessellator.addVertexWithUV(0 - 0.01D, 0, 0, 0, 1);*/
		tessellator.draw();

		GL11.glPopMatrix();
		RenderHelper.enableStandardItemLighting();
	}

	private void bindTextureForSlot(Tessellator tessellator, DisplayerBlockTileEntity dbte, int slot) {
		ItemStack stack;

		if ((stack = dbte.getItemStackInSlot(slot)) != null) {

			if (!Tools.isValidCard(stack))
				tessellator.getBuffer().color(1, 1, 1, 0);
			else {
				CardItem ci = (CardItem) stack.getItem();
				CardStructure cStruct = Databank.getCardByCDWD(stack.getTagCompound().getString("cdwd"));

				if (cStruct == null || cStruct.getDynamicTexture() == null) // Card not registered or unregistered illustration, use item image instead
					bindTexture(new ResourceLocation(Reference.MODID, "textures/items/item_card_" + Rarity.toString(ci.getCardRarity()).toLowerCase() + ".png"));
				else {
					cStruct.preloadResource(instance.renderEngine);
					bindTexture(cStruct.getResourceLocation());
				}

				tessellator.getBuffer().color(1, 1, 1, 1);
			}
		} else
			tessellator.getBuffer().color(1, 1, 1, 0);
	}
}
