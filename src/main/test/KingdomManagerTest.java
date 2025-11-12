import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.MockCommandAPIPaper;
import dev.jorel.commandapi.MockCommandAPIPlugin;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import se.fusion1013.cobaltCore.CobaltCore;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;
import se.fusion1013.cobaltKingdoms.Response;
import se.fusion1013.cobaltKingdoms.ResponseType;
import se.fusion1013.cobaltKingdoms.kingdom.KingdomManager;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class KingdomManagerTest {

    private static ServerMock server;
    private static Plugin plugin;
    private static KingdomManager manager;
    private static Plugin cobaltCore;

    @BeforeAll
    static void setUpServer() {
        // Start MockBukkit and load your plugin (optional)
        server = MockBukkit.mock();
//        plugin = MockBukkit.load(CobaltKingdoms.class);
        MockCommandAPIPlugin.load(config -> config.missingExecutorImplementationMessage("This command cannot be run by %S"));
        cobaltCore = MockBukkit.load(CobaltCore.class);

        // Get your manager from the plugin, or instantiate directly
        manager = KingdomManager.getInstance();
    }

    @AfterAll
    static void tearDownServer() {
        MockBukkit.unmock();
    }


    @AfterEach
    void cleanUp() {
        // Clean up the database to ensure test isolation
        try {
            manager.deleteKingdom("Avalon");
            manager.deleteKingdom("Camelot");
        } catch (Exception ignored) {}
    }

    @Test
    public void shouldCreateKingdomSuccessfully() {
        // given
        Player player = server.addPlayer("Arthur");
        UUID ownerId = player.getUniqueId();

        // when
        Response response = manager.createKingdom("Avalon", ownerId);

        // then
        assertEquals(ResponseType.OK, response.type());
    }

    @Test
    public void shouldFailWhenKingdomNameAlreadyExists() {
        // given
        Player player1 = server.addPlayer("Arthur");
        UUID owner1 = player1.getUniqueId();
        manager.createKingdom("Avalon", owner1);

        Player player2 = server.addPlayer("Lancelot");
        UUID owner2 = player2.getUniqueId();

        // when
        Response response = manager.createKingdom("Avalon", owner2);

        // then
        assertEquals(ResponseType.FAIL, response.type());
    }

    @Test
    public void shouldFailWhenOwnerAlreadyOwnsAKingdom() {
        // given
        Player player = server.addPlayer("Arthur");
        UUID ownerId = player.getUniqueId();

        manager.createKingdom("Avalon", ownerId);

        // when
        Response response = manager.createKingdom("Camelot", ownerId);

        // then
        assertEquals(ResponseType.FAIL, response.type());
    }

    @Test
    public void shouldFailWhenPlayerNotFoundOnServer() {
        // given
        UUID missingPlayer = UUID.randomUUID();

        // when
        Response response = manager.createKingdom("Avalon", missingPlayer);

        // then
        assertEquals(ResponseType.FAIL, response.type());
    }

}
