package com.tencao.saoui.util

/*
object CraftingUtil {

    val player: ClientPlayerEntity?
        get() = Client.minecraft.player
    val world: ClientWorld?
        get() = Client.minecraft.world
    val recipeList = RecipeList()
    val stackedContents = RecipeItemHelper()
    private var timesInventoryChanged = 0
    var craftReady = false
    var craftLimit = 0
    var currentCount = 0
    var ticks = 0
    var currentRecipe: IRecipe? = null

    /**
     * @param force will force an update
     * @return true if recipes have changed
     */
    fun updateItemHelper(force: Boolean = false): Boolean =
        if (player.inventory.timesChanged != timesInventoryChanged || force) {
            timesInventoryChanged = player.inventory.timesChanged
            stackedContents.clear()
            player.inventory.fillStackedContents(stackedContents, false)
            recipeList.updateKnownRecipes(player.recipeBook)
            updateCollections()
            true
        } else false


    fun getCategories(): List<CreativeTabs>{
        return RecipeBookClient.RECIPES_BY_TAB.keys.filter { it != CreativeTabs.SEARCH }
    }

    fun anyValidRecipes(tab: CreativeTabs): Boolean{
        return RecipeBookClient.RECIPES_BY_TAB[tab]?.any { it.containsCraftableRecipes() && it.recipes.any { recipe -> canCraft(recipe) } }?: false
    }

    fun getRecipes(tab: CreativeTabs): MutableSet<IRecipe>{
        val recipes: MutableSet<IRecipe> = mutableSetOf()
        RecipeBookClient.RECIPES_BY_TAB[tab]?.forEach { recipeList -> recipes.addAll(recipeList.recipes.filter { recipe -> recipe.canFit(2, 2) && canCraft(recipe) }) }
        return recipes
    }

    fun canCraft(recipe: IRecipe): Boolean{
        return stackedContents.canCraft(recipe, null)
    }

    fun setupCraft(){
        if (currentRecipe != null)
            Client.minecraft.playerController.func_194338_a(player.openContainer.windowId, currentRecipe!!, false, player)
        else {
            resetCraft()
        }
    }

    fun craft(recipe: IRecipe): Boolean{
        return craft(recipe, recipe.recipeOutput.count)
    }

    fun craft(recipe: IRecipe, count: Int): Boolean{
        return if (canCraft(recipe)) {

            Client.minecraft.playerController.func_194338_a(player.openContainer.windowId, recipe, true, player)
            this.currentRecipe = recipe
            this.craftLimit = count
            this.craftReady = true
            CraftingAlert.new(recipe.recipeOutput)
            ticks = 0
            true
        } else false
    }

    fun resetCraft(){
        currentRecipe = null
        this.craftReady = false
        this.craftLimit = 0
        this.currentCount = 0
        for (i in 1..4) {
            Client.minecraft.playerController.windowClick(player.openContainer.windowId, i, 0, ClickType.QUICK_MOVE, player)
        }
    }

    fun getCraft() {
        if (ticks++ < 5) return
        ticks = 0
        if (this.currentCount >= this.craftLimit){
            resetCraft()
            return
        }

        var stack = Client.minecraft.playerController.windowClick(player.openContainer.windowId, 0, 0, ClickType.PICKUP, player)
        if (stack.isEmpty) {
            resetCraft()
            return
        }
        player.openContainer.inventorySlots
                .filter { it.slotNumber !in IntRange(0, 4) && it.isItemValid(stack) && it.hasStack && it.stack.count != it.stack.maxStackSize && ContainerPlayer.canAddItemToSlot(it, stack, true) }
                .any slotCheck@{
                    stack = Client.minecraft.playerController.windowClick(player.openContainer.windowId, it.slotNumber, 0, ClickType.PICKUP, player)
                    stack.isEmpty
                }
        if (!stack.isEmpty) {
            player.openContainer.inventorySlots
                    .filter { it.slotNumber !in IntRange(0, 4) && !it.hasStack && it.isItemValid(stack) }
                    .any slotCheck@{
                        stack = Client.minecraft.playerController.windowClick(player.openContainer.windowId, it.slotNumber, 0, ClickType.PICKUP, player)
                        stack.isEmpty
                    }
        }
        this.currentCount++
        setupCraft()
        player.openContainer.detectAndSendChanges()
    }

    fun getMaxStack(recipe: IRecipe): Int{

        return stackedContents.getBiggestCraftableStack(recipe, null) * recipe.recipeOutput.count
    }

    private fun updateCollections() {
        val list: List<RecipeList> = RecipeBookClient.ALL_RECIPES
        list.forEach(Consumer { recipeList: RecipeList -> recipeList.canCraft(stackedContents, 2, 2, player.recipeBook) })
        val validRecipes: MutableList<RecipeList> = Lists.newArrayList(list)
        validRecipes.removeIf { recipeList: RecipeList -> !recipeList.isNotEmpty }
        validRecipes.removeIf { recipeList: RecipeList -> !recipeList.containsValidRecipes() }
        if (player.recipeBook.isFilteringCraftable) {
            validRecipes.removeIf { recipeList: RecipeList -> !recipeList.containsCraftableRecipes() }
        }
    }
}*/
