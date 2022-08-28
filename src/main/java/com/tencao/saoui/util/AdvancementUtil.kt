package com.tencao.saoui.util

/*
object AdvancementUtil {

    private var hasErrored = false
    private val ADVANCEMENT_LIST = AdvancementList()

    fun getAdvancements(category: Advancement, complete: Boolean): Sequence<Advancement> {
        return getAdvancements().asSequence().filter {
            it.parent == category &&
                    it.getProgress()?.isDone == complete }
    }

    fun getAdvancements(): Set<Advancement> {
        return ADVANCEMENT_LIST.all.toSet().also { Client.minecraft.connection?.advancementManager?.advancementList?.all }
    }

    fun getCategories(): Set<Advancement> = ADVANCEMENT_LIST.roots.filter { !it.id.path.contains("recipe") && !it.children.none() }.toSet()

    fun getRecipes(): List<Advancement> {
        return getAdvancements().asSequence().filter {
            it.id.path.contains("recipe") && it.parent != null }.toList()
    }

    fun generateList() {
        this.hasErrored = false
        ADVANCEMENT_LIST.clear()
        val map = mutableMapOf<ResourceLocation, Advancement.Builder>()
        this.loadBuiltInAdvancements(map)
        ADVANCEMENT_LIST.loadAdvancements(map)
        for (advancement in ADVANCEMENT_LIST.roots) {
            if (advancement.display != null) {
                AdvancementTreeNode.layout(advancement)
            }
        }
    }

    private fun loadBuiltInAdvancements(map: MutableMap<ResourceLocation, Advancement.Builder>) {

        var filesystem: FileSystem? = null
        try {
            val url = AdvancementManager::class.java.getResource("/assets/.mcassetsroot")
            if (url != null) {
                val uri = url.toURI()
                val path: Path
                if ("file" == uri.scheme) {
                    path = Paths.get(CraftingManager::class.java.getResource("/assets/minecraft/advancements").toURI())
                } else {
                    if ("jar" != uri.scheme) {
                        SAOCore.LOGGER.error("Unsupported scheme $uri trying to list all built-in advancements (NYI?)")
                        this.hasErrored = true
                        return
                    }
                    filesystem = FileSystems.newFileSystem(uri, emptyMap<String, Any>())
                    path = filesystem.getPath("/assets/minecraft/advancements")
                }
                val iterator = Files.walk(path).iterator()
                while (iterator.hasNext()) {
                    val path1 = iterator.next()
                    if ("json" == FilenameUtils.getExtension(path1.toString())) {
                        val path2 = path.relativize(path1)
                        val s = FilenameUtils.removeExtension(path2.toString()).replace("\\\\".toRegex(), "/")
                        val resourcelocation = ResourceLocation("minecraft", s)
                        if (!map.containsKey(resourcelocation)) {
                            var bufferedreader: BufferedReader? = null
                            try {
                                bufferedreader = Files.newBufferedReader(path1)
                                val advancement =  JsonUtils.fromJson(AdvancementManager.GSON, bufferedreader, Advancement.Builder::class.java)
                                if (advancement != null)
                                map[resourcelocation] = advancement
                            } catch (jsonparseexception: JsonParseException) {
                                SAOCore.LOGGER.error("Parsing error loading built-in advancement $resourcelocation", jsonparseexception as Throwable)
                                this.hasErrored = true
                            } catch (ioexception: IOException) {
                                SAOCore.LOGGER.error("Couldn't read advancement $resourcelocation from $path1", ioexception as Throwable)
                                this.hasErrored = true
                            } finally {
                                IOUtils.closeQuietly(bufferedreader as Reader?)
                            }
                        }
                    }
                }
                return
            }
            SAOCore.LOGGER.error("Couldn't find .mcassetsroot")
            this.hasErrored = true
        } catch (urisyntaxexception: IOException) {
            SAOCore.LOGGER.error("Couldn't get a list of all built-in advancement files", urisyntaxexception as Throwable)
            this.hasErrored = true
            return
        } catch (urisyntaxexception: URISyntaxException) {
            SAOCore.LOGGER.error("Couldn't get a list of all built-in advancement files", urisyntaxexception as Throwable)
            this.hasErrored = true
            return
        } finally {
            IOUtils.closeQuietly(filesystem as Closeable?)
        }
    }
}*/

// fun Advancement.getProgress() = Client.minecraft.connection?.advancementManager?.getAdvancementToProgress()?.get(this)
