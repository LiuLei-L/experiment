package liulei.experiment;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLevelEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import java.net.URI;

/** Experiment: Auto Experiment (Experimentation Table solver) mod. */
public final class ExperimentMod implements ClientModInitializer {
    public static final String MOD_ID = "experiment";
    public static final String GITHUB_URL = "https://github.com/LiuLei-L/experiment";

    @Override
    public void onInitializeClient() {
        ExperimentConfig.load();
        ClientTickEvents.END_CLIENT_TICK.register(client -> AutoExperimentFeature.tick());
        ClientLevelEvents.AFTER_CLIENT_LEVEL_CHANGE.register((client, level) -> AutoExperimentFeature.onWorldChanged());
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> registerCommands(dispatcher));
    }

    private static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommands.literal("experiment")
            .executes(context -> {
                context.getSource().sendFeedback(Component.literal(
                    "[Experiment] 状态: " + (ExperimentConfig.enabled() ? "开启" : "关闭")
                        + " | 点击延迟: " + ExperimentConfig.delayMs() + " ms"
                        + " | Superpairs: " + (ExperimentConfig.superpairs() ? "开启" : "关闭")
                        + " | Serums: " + (ExperimentConfig.serums() ? "开启" : "关闭")
                ).withStyle(ChatFormatting.AQUA));
                return 1;
            })
            .then(ClientCommands.literal("help")
                .executes(context -> showHelp(context.getSource())))
            .then(ClientCommands.literal("on")
                .executes(context -> setEnabled(context.getSource(), true)))
            .then(ClientCommands.literal("off")
                .executes(context -> setEnabled(context.getSource(), false)))
            .then(ClientCommands.literal("superpairs")
                .then(ClientCommands.literal("on")
                    .executes(context -> setSuperpairsEnabled(context.getSource(), true)))
                .then(ClientCommands.literal("off")
                    .executes(context -> setSuperpairsEnabled(context.getSource(), false))))
            .then(ClientCommands.literal("serums")
                .then(ClientCommands.literal("on")
                    .executes(context -> setSerumsEnabled(context.getSource(), true)))
                .then(ClientCommands.literal("off")
                    .executes(context -> setSerumsEnabled(context.getSource(), false))))
            .then(ClientCommands.literal("delay")
                .then(ClientCommands.argument("ms",
                        IntegerArgumentType.integer(ExperimentConfig.MIN_DELAY_MS, ExperimentConfig.MAX_DELAY_MS))
                    .executes(context -> {
                        int ms = IntegerArgumentType.getInteger(context, "ms");
                        ExperimentConfig.setDelayMs(ms);
                        context.getSource().sendFeedback(Component.literal(
                            "[Experiment] 点击延迟已设置为 " + ms + " ms"
                        ).withStyle(ChatFormatting.GREEN));
                        return 1;
                    }))));
    }

    private static int showHelp(FabricClientCommandSource source) {
        source.sendFeedback(Component.literal(
            "========== Experiment 帮助 =========="
        ).withStyle(ChatFormatting.AQUA));
        source.sendFeedback(Component.literal(
            "/experiment         查看当前状态"
        ).withStyle(ChatFormatting.GRAY));
        source.sendFeedback(Component.literal(
            "/experiment on/off  开启/关闭模组"
        ).withStyle(ChatFormatting.GRAY));
        source.sendFeedback(Component.literal(
            "/experiment delay <ms>  设置点击延迟"
        ).withStyle(ChatFormatting.GRAY));
        source.sendFeedback(Component.literal(
            "/experiment superpairs on/off  Superpairs开关"
        ).withStyle(ChatFormatting.GRAY));
        source.sendFeedback(Component.literal(
            "/experiment serums on/off  Serums模式开关"
        ).withStyle(ChatFormatting.GRAY));
        source.sendFeedback(Component.literal(
            "GitHub: " + GITHUB_URL
        ).setStyle(Style.EMPTY
            .withColor(ChatFormatting.YELLOW)
            .withClickEvent(new ClickEvent.OpenUrl(URI.create(GITHUB_URL)))
            .withUnderlined(true)));
        return 1;
    }

    private static int setEnabled(FabricClientCommandSource source, boolean value) {
        ExperimentConfig.setEnabled(value);
        source.sendFeedback(Component.literal(
            "[Experiment] Auto Experiment 已" + (value ? "开启" : "关闭")
        ).withStyle(ChatFormatting.GREEN));
        return 1;
    }

    private static int setSuperpairsEnabled(FabricClientCommandSource source, boolean value) {
        ExperimentConfig.setSuperpairs(value);
        source.sendFeedback(Component.literal(
            "[Experiment] Superpairs 自动配对已" + (value ? "开启" : "关闭")
        ).withStyle(ChatFormatting.GREEN));
        return 1;
    }

    private static int setSerumsEnabled(FabricClientCommandSource source, boolean value) {
        ExperimentConfig.setSerums(value);
        source.sendFeedback(Component.literal(
            "[Experiment] Serums 模式已" + (value ? "开启" : "关闭")
                + " (Chronomatron 9轮 / Ultrasequencer 6轮)"
        ).withStyle(ChatFormatting.GREEN));
        return 1;
    }
}
