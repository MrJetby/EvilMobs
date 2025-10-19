### Requirements
- Paper 1.16.5+
- [Treex](https://github.com/MrJetby/Treex/releases "Treex") 0.1.3+
- Java 17

------------

### Example mob
https://www.youtube.com/watch?v=4qVqgTsaKAg

### API
###### Repository
```xml
	<repository>
	    <id>jitpack.io</id>
	    <url>https://jitpack.io</url>
	</repository>
```
###### Dependency
```xml
<dependency>
	<groupId>com.github.MrJetby</groupId>
	<artifactId>EvilMobs</artifactId>
	<version>VERSION</version>
</dependency>
```
##### Example
```java
 @EventHandler
    public void onDamage(MobDamageEvent e) {
        if (e.getDamager() instanceof Player player) {
            player.sendMessage("You damaged: "+e.getId());
        }
    }
```


------------

