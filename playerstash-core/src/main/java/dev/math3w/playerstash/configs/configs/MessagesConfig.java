package dev.math3w.playerstash.configs.configs;

import dev.math3w.playerstash.configs.Config;
import dev.math3w.playerstash.configs.annotations.ConfigField;
import dev.math3w.playerstash.configs.annotations.ConfigInfo;
import dev.math3w.playerstash.utils.StringUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

@ConfigInfo(fileName = "messages.yml")
public class MessagesConfig implements Config {
    @ConfigField(path = "notification.enabled")
    private boolean notificationEnabled = true;

    @ConfigField(path = "notification.content")
    private String notification = "&7You have &3%amount% &7items stashed away!\n&3>>> CLICK HERE &bto pick them up! &3<<<";

    @ConfigField(path = "notification.period")
    private int notificationPeriod = 600;

    @ConfigField(path = "claimed")
    private String claimed = "&7You have claimed %amount% items!";

    @ConfigField(path = "full-inventory")
    private String fullInventory = "&cYou cannot claim the stash because your inventory is full!";

    @ConfigField(path = "empty-stash")
    private String emptyStash = "&ccYou don't have any items in your stash!";

    public boolean isNotificationEnabled() {
        return notificationEnabled;
    }

    public TextComponent getNotification(int stashedAmount) {
        String rawNotification = notification.replaceAll("%amount%", String.valueOf(stashedAmount));
        TextComponent notification = new TextComponent(StringUtils.colorizeComponent(rawNotification));
        notification.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pickupstash"));
        return notification;
    }

    public int getNotificationPeriod() {
        return notificationPeriod;
    }

    public String getClaimed(int claimedAmount) {
        return StringUtils.colorize(claimed).replaceAll("%amount%", String.valueOf(claimedAmount));
    }

    public String getFullInventory() {
        return StringUtils.colorize(fullInventory);
    }

    public String getEmptyStash() {
        return StringUtils.colorize(emptyStash);
    }
}