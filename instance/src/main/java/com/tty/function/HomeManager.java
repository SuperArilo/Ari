package com.tty.function;

import com.tty.Ari;
import com.tty.entity.sql.ServerHome;
import com.tty.enumType.FilePath;
import com.tty.lib.Lib;
import com.tty.lib.dto.Page;
import com.tty.lib.tool.Log;
import com.tty.tool.PermissionUtils;
import com.tty.tool.SQLInstance;
import com.tty.tool.TextTool;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.sql2o.Connection;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class HomeManager extends BaseFunction implements BaseManager<ServerHome> {

    private final Player player;
    private final Location location;

    private HomeManager(Player player) {
        this.player = player;
        this.location = player.getLocation();
    }

    @Override
    public CompletableFuture<List<ServerHome>> asyncGetList(int pageNum, int pageSize) {
        long start = System.currentTimeMillis();
        CompletableFuture<List<ServerHome>> future = new CompletableFuture<>();
        Lib.Scheduler.runAsync(Ari.instance, i -> {
            try (Connection connection = SQLInstance.SESSION_FACTORY.open()) {
                Page page = Page.create(pageNum, pageSize);
                List<ServerHome> serverHomes = connection.createQuery("""
                                    SELECT * FROM %splayer_home AS ph
                                    WHERE ph.player_uuid = :player_uuid
                                    ORDER BY ph.top_slot DESC, ph.id
                                    LIMIT :limit OFFSET :offset
                                """.formatted(SQLInstance.getTablePrefix()))
                        .addParameter("player_uuid", this.player.getUniqueId())
                        .addParameter("limit", page.getLimit())
                        .addParameter("offset", page.getOffset()).executeAndFetch(ServerHome.class);
                future.complete(serverHomes);
            } catch (Exception e) {
                Log.error("query home error!", e);
                future.complete(List.of());
            } finally {
                Log.debug(Level.INFO, "get home list time: " + (System.currentTimeMillis() - start) + "ms");
            }
        });
        return future;
    }

    @Override
    public CompletableFuture<List<String>> asyncGetIdList() {
        return null;
    }

    @Override
    public CompletableFuture<ServerHome> asyncGetInstance(String id) {
        CompletableFuture<ServerHome> future = new CompletableFuture<>();
        Lib.Scheduler.runAsync(Ari.instance, i -> {
            try (Connection connection = SQLInstance.SESSION_FACTORY.open()) {
                ServerHome serverHome = connection.createQuery("""
                                    select * from %splayer_home
                                    where home_id = :home_id and player_uuid = :player_uuid
                                """.formatted(SQLInstance.getTablePrefix()))
                        .addParameter("home_id", id)
                        .addParameter("player_uuid", this.player.getUniqueId())
                        .executeAndFetchFirst(ServerHome.class);
                future.complete(serverHome);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }


    @Override
    public void createInstance(String homeId) {
        Material material = this.checkIsItem(location.getBlock().getRelative(BlockFace.DOWN).getType());
        Lib.Scheduler.runAsync(Ari.instance, i -> {
            long start = System.currentTimeMillis();

            try (Connection connection = SQLInstance.SESSION_FACTORY.open()) {

                List<String> homeIdList = connection.createQuery("""
                                    select ph.home_id from %splayer_home as ph
                                    where ph.player_uuid = :player_uuid
                                """.formatted(SQLInstance.getTablePrefix()))
                        .addParameter("player_uuid", this.player.getUniqueId())
                        .executeAndFetch(String.class);

                if (homeIdList.size() + 1 > PermissionUtils.getMaxCountInPermission(player, "home") && !player.isOp()) {
                    Log.debug("Exceeds the specified quantity");
                    player.sendMessage(TextTool.setHEXColorText("function.home.exceeds", FilePath.Lang));
                    i.cancel();
                    return;
                }
                if (homeIdList.contains(homeId)) {
                    player.sendMessage(TextTool.setHEXColorText("function.home.exist", FilePath.Lang, player));
                    i.cancel();
                    return;
                }

                connection.createQuery("""
                    insert into %splayer_home
                    (home_id, home_name, player_uuid, location, show_material)
                    values
                    (:home_id, :home_name, :player_uuid, :location, :show_material)
                """.formatted(SQLInstance.getTablePrefix()))
                        .addParameter("home_id", homeId)
                        .addParameter("home_name", homeId)
                        .addParameter("player_uuid", this.player.getUniqueId())
                        .addParameter("location", this.location.toString())
                        .addParameter("show_material", material.name())
                        .executeUpdate();

                player.sendMessage(TextTool.setHEXColorText("function.home.create-success", FilePath.Lang, player));
            } catch (Exception e) {
                Log.error(e.getMessage());
                i.cancel();
            } finally {
                Log.debug(Level.INFO, "save home done. time: " + (System.currentTimeMillis() - start) + "ms");
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> deleteInstance(String homeId) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        long start = System.currentTimeMillis();
        Lib.Scheduler.runAsync(Ari.instance, i -> {
            try (Connection connection = SQLInstance.SESSION_FACTORY.open()) {
                int delete = connection.createQuery("""
                                    delete from %splayer_home
                                    where home_id = :home_id and player_uuid = :player_uuid
                                """.formatted(SQLInstance.getTablePrefix()))
                        .addParameter("home_id", homeId)
                        .addParameter("player_uuid", player.getUniqueId())
                        .executeUpdate().getResult();
                future.complete(delete == 1);
            } catch (Exception e) {
                future.completeExceptionally(e);
                Log.error("remove home fail, id: " + homeId, e);
            } finally {
                Log.debug(Level.INFO, "remove home time: " + (System.currentTimeMillis() - start) + "ms");
            }
        });
        return future;
    }

    @Override
    public CompletableFuture<Boolean> modify(ServerHome instance) {
        long start = System.currentTimeMillis();
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        Lib.Scheduler.runAsync(Ari.instance, i -> {

            try (Connection connection = SQLInstance.SESSION_FACTORY.open()) {
                int result = connection.createQuery("""
                                    update %splayer_home set
                                    home_name = :home_name,
                                    location = :location,
                                    show_material = :show_material,
                                    top_slot = :top_slot
                                    where home_id = :home_id and player_uuid = :player_uuid
                                """.formatted(SQLInstance.getTablePrefix()))
                        .addParameter("home_name", instance.getHomeName())
                        .addParameter("location", instance.getLocation())
                        .addParameter("show_material", instance.getShowMaterial())
                        .addParameter("top_slot", instance.isTopSlot())
                        .addParameter("home_id", instance.getHomeId())
                        .addParameter("player_uuid", this.player.getUniqueId())
                        .executeUpdate().getResult();
                if (result == 1) {
                    future.complete(true);
                } else {
                    future.complete(false);
                    Log.error("save homeId: [" + instance.getHomeId() + "] error");
                }
            } catch (Exception e) {
                Log.error("save home error", e);
                future.complete(false);
            } finally {
                Log.debug("save time: " + (System.currentTimeMillis() - start) + "ms");
            }
        });
        return future;
    }

    public static HomeManager create(Player player) {
        return new HomeManager(player);
    }
}
