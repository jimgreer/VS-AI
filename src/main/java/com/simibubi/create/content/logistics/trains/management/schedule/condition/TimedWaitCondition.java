package com.simibubi.create.content.logistics.trains.management.schedule.condition;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.Lang;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class TimedWaitCondition extends ScheduleWaitCondition {

	public static enum TimeUnit {
		TICKS(1, "t", "generic.unit.ticks"),
		SECONDS(20, "s", "generic.unit.seconds"),
		MINUTES(20 * 60, "min", "generic.unit.minutes");

		public int ticksPer;
		public String suffix;
		public String key;

		private TimeUnit(int ticksPer, String suffix, String key) {
			this.ticksPer = ticksPer;
			this.suffix = suffix;
			this.key = key;
		}

		public static List<Component> translatedOptions() {
			return Lang.translatedOptions(null, TICKS.key, SECONDS.key, MINUTES.key);
		}
	}

	public int totalWaitTicks() {
		return getValue() * getUnit().ticksPer;
	}
	
	public TimedWaitCondition() {
		data.putInt("Value", 5);
		data.putInt("TimeUnit", TimeUnit.SECONDS.ordinal());
	}

	protected Component formatTime(boolean compact) {
		if (compact)
			return new TextComponent(getValue() + getUnit().suffix);
		return new TextComponent(getValue() + " ").append(Lang.translate(getUnit().key));
	}

	@Override
	public List<Component> getTitleAs(String type) {
		return ImmutableList.of(
			new TranslatableComponent(getId().getNamespace() + ".schedule." + type + "." + getId().getPath()),
			Lang.translate("schedule.condition.for_x_time", formatTime(false))
				.withStyle(ChatFormatting.DARK_AQUA));
	}

	@Override
	public ItemStack getSecondLineIcon() {
		return new ItemStack(Items.REPEATER);
	}

	@Override
	public List<Component> getSecondLineTooltip(int slot) {
		return ImmutableList.of(Lang.translate("generic.duration"));
	}

	public int getValue() {
		return intData("Value");
	}

	public TimeUnit getUnit() {
		return enumData("TimeUnit", TimeUnit.class);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void initConfigurationWidgets(ModularGuiLineBuilder builder) {
		builder.addScrollInput(0, 31, (i, l) -> {
			i.titled(Lang.translate("generic.duration"))
				.withShiftStep(15)
				.withRange(0, 121);
			i.lockedTooltipX = -15;
			i.lockedTooltipY = 35;
		}, "Value");

		builder.addSelectionScrollInput(36, 85, (i, l) -> {
			i.forOptions(TimeUnit.translatedOptions())
				.titled(Lang.translate("generic.timeUnit"));
		}, "TimeUnit");
	}

}