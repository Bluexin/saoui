package com.tencao.saoui.config

// import net.minecraftforge.common.config.Configuration

object FriendData { /*

    var config: Configuration = Configuration(File(SAOCore.saoConfDir, "friend_list.cfg"), "1")
    private const val CATEGORY_FRIENDS = "Friends"
    var friendDataList = linkedSetOf<PlayerInfo>()

    fun preInit(){
        config.load()

        if (config.hasCategory(CATEGORY_FRIENDS)){
            config.getCategory(CATEGORY_FRIENDS).forEach {
                friendDataList.add(PlayerInfo(UUID.fromString(it.value.string), it.key.toString()))
            }
        }
        save()
    }

    fun save() {
        friendDataList.forEach { player -> config[CATEGORY_FRIENDS, player.username, player.uuidString] }
        config.save()
    }*/
}
