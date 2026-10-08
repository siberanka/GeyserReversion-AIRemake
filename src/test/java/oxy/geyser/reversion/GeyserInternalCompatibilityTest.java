package oxy.geyser.reversion;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import org.geysermc.event.subscribe.Subscribe;
import org.geysermc.geyser.GeyserImpl;
import org.geysermc.geyser.api.event.lifecycle.GeyserPostInitializeEvent;
import org.geysermc.geyser.api.event.lifecycle.GeyserPreInitializeEvent;
import org.geysermc.geyser.configuration.GeyserConfig;
import org.geysermc.geyser.network.GeyserServerInitializer;
import org.geysermc.geyser.network.RaknetServer;
import org.geysermc.geyser.network.bedrock.GameProtocol;
import org.junit.jupiter.api.Test;
import oxy.geyser.reversion.config.Config;
import oxy.geyser.reversion.handler.init.TranslatorServerInitializer;

import java.io.InputStream;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

class GeyserInternalCompatibilityTest {
    @Test
    void lifecycleHandlersRemainSubscribed() throws Exception {
        var pre = GeyserReversion.class.getMethod("onGeyserPreInitializeEvent", GeyserPreInitializeEvent.class);
        var post = GeyserReversion.class.getMethod("onGeyserPostInitializeEvent", GeyserPostInitializeEvent.class);
        assertNotNull(pre.getAnnotation(Subscribe.class));
        assertNotNull(post.getAnnotation(Subscribe.class));
    }

    @Test
    void listenerInjectionMatchesGeyser2113Internals() throws Exception {
        assertEquals(RaknetServer.class, GeyserImpl.class.getMethod("getGeyserServer").getReturnType());
        assertTrue(GeyserServerInitializer.class.isAssignableFrom(TranslatorServerInitializer.class));
        assertField("group", EventLoopGroup.class);
        assertField("childGroup", EventLoopGroup.class);
        assertField("playerGroup", EventLoopGroup.class);
        assertField("listenCount", int.class);
        assertField("bootstrapFutures", ChannelFuture[].class);
        assertEquals(int.class, GeyserConfig.BedrockConfig.class.getMethod("raknetPort").getReturnType());

        assertLoadable("org.geysermc.geyser.network.BedrockPingHandler");
        assertLoadable("org.geysermc.geyser.network.bedrock.raknet.Bootstraps");
        assertLoadable("org.geysermc.geyser.network.bedrock.raknet.RakConnectionRequestHandler");
        assertLoadable("org.geysermc.geyser.network.bedrock.raknet.RakPingHandler");
        assertLoadable("org.geysermc.geyser.network.bedrock.InvalidPacketHandler");
        assertThrows(ClassNotFoundException.class,
                () -> Class.forName("org.geysermc.geyser.network.GameProtocol"));
    }

    @Test
    void currentGeyserCatalogProvidesTheSelectedBridgeAndLatestCodec() {
        assertEquals(2193, GameProtocol.DEFAULT_BEDROCK_PROTOCOL);
        assertNotNull(GameProtocol.getBedrockCodec(1001));
        assertNotNull(GameProtocol.getBedrockCodec(2193));
        assertNull(GameProtocol.getBedrockCodec(944));
        assertTrue(GameProtocol.getAllSupportedBedrockVersions().contains("26.52"));
    }

    @Test
    void existingConfigurationSchemaAndDefaultsArePreserved() throws Exception {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.yml")) {
            assertNotNull(input);
            Config config = new ObjectMapper(new YAMLFactory())
                    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(input, Config.class);
            assertFalse(config.debugMode());
            assertEquals(-1, config.minProtocolId());
            assertNotNull(config.blockProtocols());
            assertTrue(config.blockProtocols().isEmpty());
            assertFalse(config.minProtocolKick().isBlank());
            assertFalse(config.versionNotSupportedKick().isBlank());
            assertFalse(config.blockedProtocolKick().isBlank());
        }
    }

    private static void assertField(String name, Class<?> type) throws Exception {
        var field = RaknetServer.class.getDeclaredField(name);
        assertEquals(type, field.getType());
        assertFalse(Modifier.isFinal(field.getModifiers()), name + " must remain replaceable");
    }

    private static void assertLoadable(String className) {
        assertDoesNotThrow(() -> Class.forName(className, false,
                GeyserInternalCompatibilityTest.class.getClassLoader()));
    }
}
