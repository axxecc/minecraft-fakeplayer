package io.github.hello09x.fakeplayer.core;

import com.google.inject.Guice;
import com.google.inject.Injector;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIPaperConfig;
import io.github.hello09x.devtools.command.CommandModule;
import io.github.hello09x.devtools.core.TranslationModule;
import io.github.hello09x.devtools.core.translation.TranslationConfig;
import io.github.hello09x.devtools.core.translation.TranslatorUtils;
import io.github.hello09x.devtools.core.utils.Exceptions;
import io.github.hello09x.devtools.database.DatabaseModule;
import io.github.hello09x.fakeplayer.core.command.CommandRegistry;
import io.github.hello09x.fakeplayer.core.listener.FakeplayerLifecycleListener;
import io.github.hello09x.fakeplayer.core.listener.FakeplayerListener;
import io.github.hello09x.fakeplayer.core.listener.PlayerListener;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerAutofishManager;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerReplenishManager;
import io.github.hello09x.fakeplayer.core.manager.WildFakeplayerManager;
import io.github.hello09x.fakeplayer.core.manager.invsee.InvseeManager;
import io.github.hello09x.fakeplayer.core.placeholder.FakeplayerPlaceholderExpansion;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class Main extends JavaPlugin {

    @Getter
    private static Main instance;

    private Injector injector;

    private long loadAt;

    @Override
    public void onLoad() {
        loadAt = System.currentTimeMillis();
        instance = this;

        CommandAPI.onLoad(new CommandAPIPaperConfig(this)
                .verboseOutput(false) // 使用详细输出加载
                .silentLogs(true) // 禁用所有日志记录（错误除外）
                .fallbackToLatestNMS(true) // 如果找不到当前版本的实现，CommandAPI 是否应该退回到最新的 NMS 版本
                .missingExecutorImplementationMessage("This command has no implementations for %s") // 设置在缺少执行器实现时显示的消息
                .instance()
        );
    }

    @Override
    public void onEnable() {
        CommandAPI.onEnable();

        injector = Guice.createInjector(
                new FakeplayerModule(),
                new CommandModule(),
                new DatabaseModule(),
                new TranslationModule(new TranslationConfig(
                        "message/message",
                        TranslatorUtils.getDefaultLocale(Main.getInstance())))
        );

        injector.getInstance(CommandRegistry.class).register();
        {
            var messenger = getServer().getMessenger();
            messenger.registerIncomingPluginChannel(this, "BungeeCord", injector.getInstance(WildFakeplayerManager.class));
            messenger.registerOutgoingPluginChannel(this, "BungeeCord");
        }


        {
            var manager = getServer().getPluginManager();
            manager.registerEvents(injector.getInstance(PlayerListener.class), this);
            manager.registerEvents(injector.getInstance(FakeplayerLifecycleListener.class), this);
            manager.registerEvents(injector.getInstance(FakeplayerListener.class), this);
            manager.registerEvents(injector.getInstance(FakeplayerAutofishManager.class), this);
            manager.registerEvents(injector.getInstance(FakeplayerReplenishManager.class), this);
            manager.registerEvents(injector.getInstance(InvseeManager.class), this);
        }


        {
            var placeholderExpansion = injector.getInstance(FakeplayerPlaceholderExpansion.class);
            if (placeholderExpansion != null) {
                if (placeholderExpansion.register()) {
                    getServer().getPluginManager().registerEvents(placeholderExpansion, this);
                    getLogger().info("Successfully registered PlaceholderExpansion");
                }
            }
        }

        getLogger().info("Enabled in %d ms".formatted(System.currentTimeMillis() - loadAt));
    }

    @Override
    public void onDisable() {
        {
            Exceptions.suppress(this, () -> {
                var messenger = getServer().getMessenger();
                messenger.unregisterIncomingPluginChannel(this);
                messenger.unregisterOutgoingPluginChannel(this);
            });
        }
        CommandAPI.onDisable();
    }

    public static @NotNull Injector getInjector() {
        return instance.injector;
    }

}
