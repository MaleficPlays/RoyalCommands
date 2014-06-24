package org.royaldev.royalcommands;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ItemNameManager {

    // <Aliases, <ID, Data>>
    public final Map<String[], Pair<Material, Short>> items = new HashMap<>();

    public ItemNameManager(Iterable<String[]> values) {
        for (String[] s : values) {
            if (s.length < 1) continue;
            String[] aliases;
            try {
                aliases = s[2].split(",");
            } catch (IndexOutOfBoundsException e) {
                Logger l = Logger.getLogger("Minecraft");
                l.warning("[RoyalCommands] Values passed in ItemNameManager invalid: ");
                for (String ss : s) l.warning("[RoyalCommands] - " + ss);
                continue;
            }
            Material m;
            short data;
            try {
                m = Material.valueOf(s[0]);
            } catch (IllegalArgumentException ex) {
                RoyalCommands.instance.getLogger().warning("Material in items.csv is invalid: " + s[0]);
                continue;
            }
            try {
                data = Short.valueOf(s[1]);
            } catch (NumberFormatException e) {
                RoyalCommands.instance.getLogger().warning("Data in items.csv file is invalid: " + s[1]);
                continue;
            }
            synchronized (items) {
                items.put(aliases, new Pair<>(m, data));
            }
        }
    }

    public boolean aliasExists(Material m) {
        return aliasExists(new Pair<>(m, (short) 0));
    }

    public boolean aliasExists(ItemStack is) {
        return aliasExists(new Pair<>(is.getType(), is.getDurability()));
    }

    public boolean aliasExists(Pair<Material, Short> data) {
        return items.values().contains(data);
    }

    public ItemStack getItemStackFromAlias(String alias) {
        boolean found = false;
        String[] aliases = null;
        String data = null;
        if (alias.contains(":")) {
            String[] datas = alias.split(":");
            data = (datas.length > 1) ? datas[1] : "";
            alias = datas[0];
        }
        for (String[] s : items.keySet())
            if (ArrayUtils.contains(s, alias.toLowerCase())) {
                found = true;
                aliases = s;
                break;
            }
        if (!found) return null;
        final Pair<Material, Short> itemstackData = items.get(aliases);
        final ItemStack is = new ItemStack(itemstackData.getFirst(), 1, itemstackData.getSecond());
        if (data != null && !data.isEmpty()) {
            try {
                is.setDurability(Short.parseShort(data));
            } catch (NumberFormatException ignored) {}
        }
        return is;
    }

    private class Pair<T, U> {
        private final T first;
        private final U second;

        private Pair(T first, U second) {
            this.first = first;
            this.second = second;
        }

        public T getFirst() {
            return this.first;
        }

        public U getSecond() {
            return this.second;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (!(o instanceof Pair)) return false;
            final Pair<?, ?> other = (Pair<?, ?>) o;
            return this.getFirst().equals(other.getFirst()) && this.getSecond().equals(other.getSecond());
        }

        @Override
        public String toString() {
            return String.format("Pair<%s, %s>", this.getFirst().toString(), this.getSecond().toString());
        }
    }

}
