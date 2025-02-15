package com.is.mtc.pack;

import com.is.mtc.MineTradingCards;
import com.is.mtc.card.CardItem;
import com.is.mtc.root.Logs;
import com.is.mtc.util.Functions;
import com.is.mtc.util.Reference;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Random;

public class PackItemStandard extends PackItemBase {

	public static final String[] STANDARD_PACK_CONTENT_DEFAULT = new String[]{
			"7x1:0:0:0:0",
			"2x0:1:0:0:0",
			"1x0:0:25:4:1",
	};
	public static String[] STANDARD_PACK_CONTENT = STANDARD_PACK_CONTENT_DEFAULT;

	public PackItemStandard() {
		setUnlocalizedName("item_pack_standard");
		setRegistryName(Reference.MODID, "item_pack_standard");
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if (world.isRemote) {
			return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
		} // Don't do this on the client side

		ArrayList<String> created;
		Random random = world.rand;

		// Figure out how many of each card rarity to create

		int[] card_set_to_create = new int[]{0, 0, 0, 0, 0}; // Set of cards that will come out of the pack

		for (String entry : STANDARD_PACK_CONTENT) {
			try {
				double[] card_weighted_dist = new double[]{0, 0, 0, 0, 0}; // Distribution used when a card is randomized

				// Split entry
				String[] split_entry = entry.toLowerCase().trim().split("x");

				float count = MathHelper.clamp(Float.parseFloat(split_entry[0]), 0F, 64F);
				int drop_count_characteristic = (int) count;
				float drop_count_mantissa = count % 1;

				String[] distribution_split = split_entry[1].split(":");

				for (int i = 0; i < distribution_split.length; i++) {
					card_weighted_dist[i] = Integer.parseInt(distribution_split[i].trim());
				}

				// Repeat for the number of cards prescribed
				for (int i = 0; i < drop_count_characteristic + (random.nextFloat() < drop_count_mantissa ? 1 : 0); i++) {
					Object chosen_rarity = Functions.weightedRandom(CardItem.CARD_RARITY_ARRAY, card_weighted_dist, random);

					if (chosen_rarity != null) {
						card_set_to_create[(Integer) chosen_rarity]++;
					}
				}
			} catch (Exception e) {
				Logs.errLog("Something went wrong parsing standard_pack_contents line: " + entry);
			}
		}

		// Actually create the cards

		created = new ArrayList<String>();

		for (int rarity : CardItem.CARD_RARITY_ARRAY) {
			createCards(rarity, card_set_to_create[rarity], created, world.rand);
		}

		if (created.size() > 0) {
			for (String cdwd : created) {
				spawnCard(player, world, cdwd);
			}
			player.getHeldItem(hand).setCount(player.getHeldItem(hand).getCount() - 1);
		} else {
			Logs.chatMessage(player, "Zero cards were registered, thus zero cards were generated");
			Logs.errLog("Zero cards were registered, thus zero cards can be generated");
		}

		return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}

	// === ICON LAYERING AND COLORIZATION === //

	/**
	 * From https://github.com/matshou/Generic-Mod
	 */
	public static class ColorableIcon implements IItemColor {
		@Override
		@SideOnly(Side.CLIENT)
		public int colorMultiplier(ItemStack stack, int layer) {
			if (layer == 0) {
				return MineTradingCards.PACK_COLOR_STANDARD;
			}

			return -1;
		}
	}
}
