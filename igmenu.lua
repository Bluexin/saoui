print("Hello from " .. _VERSION .. " !")

local GuiIngameMenu = java.require("net.minecraft.client.gui.GuiIngameMenu")
local GuiOptions = java.require("net.minecraft.client.gui.GuiOptions")

tlCats = {
    Category("PROFILE", 0, function(profile)
        for _, v in java.ipairs(ItemFilters) do
            AddItemCategories(profile, v)
        end
        profile:category("SKILLS", i18n.format("sao.element.skills"), {}, function(skills)
            skills:category("SKILLS", "Test 1")
            skills:category("SKILLS", "解散")
        end)
        profile:crafting()
        profile:profile(mc.player, nil)
    end),
    Category("SOCIAL", 1, function(social)
        social:category("GUILD", i18n.format("sao.element.guild"), {}, function(guild)
            SetWip(guild)
        end)
        social:partyMenu()
        social:friendMenu()
    end),
    Category("MESSAGE", 2, function(message)
        SetWip(message)
    end),
    Category("NAVIGATION", 3, function(nav)
        SetWip(nav)
    end),
    Category("SETTINGS", 4, function(settings)
        settings:category("OPTION", i18n.format("sao.element.options"), {}, function(options)
            options:category("OPTION", i18n.format("guiOptions"), {}, function(vanillaOptions)
                vanillaOptions:onClick(function()
                    openGui(GuiOptions:new(vanillaOptions.controllingGUI, mc.gameSettings))
                    return true
                end)
            end)
            for _, v in java.ipairs(OptionCore.tlOptions) do
                options:plusAssign(OptionCategory(options, v))
            end
        end)
        settings:category("HELP", i18n.format("sao.element.menu"), {}, function(menuButton)
            menuButton:onClick(function()
                openGui(GuiIngameMenu:new())
                return true
            end)
        end)
        settings:category("LOGOUT", OptionCore.LOGOUT.enabled and i18n.format("sao.element.logout") or "", {}, function(logout)
            logout.disabled = OptionCore.LOGOUT.enabled
            if (OptionCore.LOGOUT.enabled) then
                logout:onClick(function()
                    logout.controllingGUI.loggingOut = true
                    return true
                end)
            end
        end)
    end)
}
