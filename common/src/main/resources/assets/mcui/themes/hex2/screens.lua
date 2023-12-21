
local button = theme.readWidget("mcui:themes/hex2/widgets/button.xml")

local function static(value)
    if (type(value) == "string") then
        value = "\"" .. value .. "\""
    else
        value = tostring(value)
    end
    return {
        expression = value,
        cache = "STATIC"
    }
end

local function tstatic(value, jtype)
    if (not jtype or type(jtype) ~= "string") then
        local vtype = type(value)
        if (vtype == "string") then
            jtype = "STRING"
        elseif (vtype == "boolean") then
            jtype = "BOOLEAN"
        elseif (vtype == "number") then
            jtype = "DOUBLE"
        end
    end
    if (type(value) ~= "string") then
        value = tostring(value)
    end
    if (jtype == "STRING") then
        value = "\"" .. value .. "\""
    end
    return {
        type = jtype,
        expression = value,
        cache = "STATIC"
    }
end

local function tframe(value, jtype)
    local basic = tstatic(value, jtype)
    basic.cache = "PER_FRAME"
    return basic
end

local function loadCenteredButton(root, text, onClick, y)
    theme.loadWidget(root, button, {
        text = tstatic(text, "STRING"),
        x = tframe("scaledwidth / 2 - 75", "DOUBLE"),
        y = tframe(y, "DOUBLE"),
    })
end

local function testGui(root)
    loadCenteredButton(root, "First button", function() print("Hit first") end, "100")
    loadCenteredButton(root, "Second button", function() print("Hit second") end, "125")
    loadCenteredButton(root, "Third button", function() print("Hit third") end, "150")
end

theme.registerScreen("mcui:testgui", testGui)
