package me.jetby.evilmobs;

import lombok.experimental.UtilityClass;
import me.jetby.evilmobs.records.Mob;
import me.jetby.evilmobs.tools.MiniTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@UtilityClass
public class Maps {

    public final Map<UUID, Map<String, MiniTask>> tasks = new HashMap<>();
    public final Map<String, MobCreator> mobCreators = new HashMap<>();
    public final Map<String, Mob> mobs = new HashMap<>();


}
