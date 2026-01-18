package se.fusion1013.cobaltKingdoms.chair;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import se.fusion1013.cobaltCore.manager.Manager;
import se.fusion1013.cobaltKingdoms.CobaltKingdoms;

import java.util.Set;

public class ChairManager extends Manager<CobaltKingdoms> implements Listener {

    public ChairManager(CobaltKingdoms plugin) {
        super(plugin);
        INSTANCE = this;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onSit(PlayerInteractEvent event) {
        if (event.getHand() == null || !event.getHand().equals(EquipmentSlot.HAND)) return;

        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

        Block block = event.getClickedBlock();
//        Location below = block.getLocation().add(new Vector(0, -1, 0));
        Block belowBlock = block.getRelative(BlockFace.DOWN);
        BlockData blockData = block.getBlockData();

        if (blockData instanceof Stairs bisected) {
            if (!bisected.getHalf().equals(Bisected.Half.BOTTOM)) return;
        } else if (blockData instanceof Slab) {
            if (!((Slab) blockData).getType().equals(Slab.Type.BOTTOM)) return;
        }

        Player p = event.getPlayer();

        if (p.isSneaking()) return;

        if (!p.getInventory().getItemInMainHand().getType().equals(Material.AIR)) return;

        if (p.isInsideVehicle()) return;

        CobaltKingdoms instance = CobaltKingdoms.getInstance();
        FileConfiguration config = instance.getConfig();
        String selectedLayout = null;

        Set<String> nodes = config.getConfigurationSection("sitables").getKeys(false);
        for (String node : nodes) {

            if (config.getBoolean("sitables." + node + ".permission.require")) {
                String permission = config.getString("sitables." + node + ".permission.name");
                if (permission != null && !permission.equals("")) {
                    permission = String.format(permission, node);
                    if (!p.hasPermission(permission)) break;
                }
            }

            if (belowBlock.getType() != Material.RED_WOOL) break;

            String mode = config.getString("sitables." + node + ".check");
            switch (mode) {
                case "BLOCKDATA":
                    Class claz;
                    for (String clasz : config.getStringList("sitables." + node + ".list")) {
                        try {
                            claz = Class.forName(clasz);
                        } catch (ClassNotFoundException ex) {
                            throw new RuntimeException("class " + clasz + " doesn't exists.");
                        }
                        if (claz.isInstance(blockData)) {
                            selectedLayout = node;
                            break;
                        }
                    }
                    break;
                case "BLOCKS":
                    for (String material : config.getStringList("sitables." + node + ".list")) {
                        if (block.getType().toString().equalsIgnoreCase(material)) {
                            selectedLayout = node;
                            break;
                        }
                    }
                    break;
            }
            if (selectedLayout != null) break;
        }
        if (selectedLayout == null) return;

        event.setCancelled(true);

        Location loc = block.getLocation();

        double adderX = config.getDouble("sitables." + selectedLayout + ".offsets.x");
        double adderY = config.getDouble("sitables." + selectedLayout + ".offsets.y");
        double adderZ = config.getDouble("sitables." + selectedLayout + ".offsets.z");

        loc.setX(loc.getX() + adderX);
        loc.setY(loc.getY() + adderY);
        loc.setZ(loc.getZ() + adderZ);

        if (blockData instanceof Directional) {
            BlockFace facing = ((Directional) blockData).getFacing();
            switch (facing) {
                case SOUTH:
                    loc.setYaw(180);
                    break;
                case WEST:
                    loc.setYaw(270);
                    break;
                case EAST:
                    loc.setYaw(90);
                    break;
                case NORTH:
                    loc.setYaw(0);
                    break;
            }
        } else {
            loc.setYaw(p.getLocation().getYaw() + 180);
        }

        if (blockData instanceof Stairs) {
            Stairs.Shape shape = ((Stairs) blockData).getShape();
            if (shape == Stairs.Shape.INNER_RIGHT || shape == Stairs.Shape.OUTER_RIGHT) {
                loc.setYaw(loc.getYaw() + 45);
            } else if (shape == Stairs.Shape.INNER_LEFT || shape == Stairs.Shape.OUTER_LEFT) {
                loc.setYaw(loc.getYaw() - 45);
            }
        }

        String entityType = config.getString("sitables." + selectedLayout + ".entity.type");
        // create final value to use in lambda
        final String layout = selectedLayout;
        Entity entity = p.getWorld().spawn(loc, EntityType.valueOf(entityType).getEntityClass(), (stair -> {
            stair.setPersistent(false);
            if (stair instanceof Attributable attributable) {
                // set movement speed to 0 to entity to not move when steering item(carrot on a stick) held
                attributable.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0);

                if (stair instanceof Pig && config.getBoolean("sitables." + layout + ".entity.saddle")) {
                    ((Pig) stair).setSaddle(true);
                }
            }

            stair.setInvulnerable(true);
            stair.setSilent(true);
            stair.setGravity(false);
            stair.setMetadata("stair", new FixedMetadataValue(instance, true));
            stair.setInvisible(true);

            if (stair instanceof LivingEntity livingEntity) {
                livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 99999, 1, false, false));
                //livingEntity.setInvisible(true);
                livingEntity.setAI(false);
            }
        }));

        entity.addPassenger(p);
    }

    @EventHandler
    public void onDismount(EntityDismountEvent e) {
        if (e.getDismounted().hasMetadata("stair")) {
            Bukkit.getScheduler().runTaskLater(CobaltKingdoms.getInstance(), () -> e.getDismounted().remove(), 1L);
        }
    }

    @Override
    public void reload() {
        Bukkit.getPluginManager().registerEvents(this, CobaltKingdoms.getInstance());
    }

    @Override
    public void disable() {

    }

    private ChairManager INSTANCE;

    public ChairManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ChairManager(CobaltKingdoms.getInstance());
        }
        return INSTANCE;
    }
}
