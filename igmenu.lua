print("Hello from " .. _VERSION .. " !")

local itemFilters = java.require("com.tencao.saoui.api.items.ItemFilterRegister").INSTANCE.tlFilters
local elementRegistry = java.require("com.tencao.saoui.api.elements.registry.ElementRegistry").INSTANCE
local tlOptions = java.require("com.tencao.saoui.config.OptionCore").Companion.tlOptions

local GuiIngameMenu = java.require("net.minecraft.client.gui.GuiIngameMenu")
local GuiOptions = java.require("net.minecraft.client.gui.GuiOptions")
local Extensions = java.require("com.tencao.saoui.api.elements.ExtensionsKt")

tlCats = {
    tlCategory("PROFILE", 0, function(profile)
        for _, v in java.ipairs(itemFilters) do
            elementRegistry:addItemCategories(profile, v)
        end
        profile:category("SKILLS", i18n.format("sao.element.skills"), {}, function(skills)
            skills:category("SKILLS", "Test 1")
            skills:category("SKILLS", "解散")
        end)
        profile:crafting()
        -- TODO : actual profile (requires making the Player lazy or it will crash during boot)
    end),
    tlCategory("SOCIAL", 1, function(social)
        social:category("GUILD", i18n.format("sao.element.guild"), {}, function(guild)
            elementRegistry:setWip(guild)
        end)
        social:partyMenu()
        social:friendMenu()
    end),
    tlCategory("MESSAGE", 2, function(message)
        elementRegistry:setWip(message)
    end),
    tlCategory("NAVIGATION", 3, function(nav)
        elementRegistry:setWip(nav)
    end),
    tlCategory("SETTINGS", 4, function(settings)
        settings:category("OPTION", i18n.format("sao.element.options"), {}, function(options)
            options:category("OPTION", i18n.format("guiOptions"), {}, function(vanillaOptions)
                vanillaOptions:onClick(function()
                    openGui(GuiOptions:new(vanillaOptions.controllingGUI, mc().gameSettings))
                    return true
                end)
            end)
            for _, v in java.ipairs(tlOptions) do
                options:unaryPlus(Extensions:optionCategory(options, v))
            end
        end)
        settings:category("HELP", i18n.format("sao.element.menu"), {}, function(menuButton)
            menuButton:onClick(function()
                openGui(GuiIngameMenu:new())
                return true
            end)
        end)
        settings:category("LOGOUT", i18n.format("sao.element.logout"), {}, function(logout)
            logout.disabled = true
        end)
    end)
}
