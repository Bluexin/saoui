local modVersion, resourceLocation = ...

print("Hello from " .. _VERSION .. " !")
print("Running on " .. modVersion .. " from " .. resourceLocation)

local offset = 30

local function tprint (tbl, indent)
    if not indent then
        indent = 0
    end
    local toprint = string.rep(" ", indent) .. "{\r\n"
    indent = indent + 2
    for k, v in pairs(tbl) do
        toprint = toprint .. string.rep(" ", indent)
        if (type(k) == "number") then
            toprint = toprint .. "[" .. k .. "] = "
        elseif (type(k) == "string") then
            toprint = toprint .. k .. "= "
        end
        if (type(v) == "number") then
            toprint = toprint .. v .. ",\r\n"
        elseif (type(v) == "string") then
            toprint = toprint .. "\"" .. v .. "\",\r\n"
        elseif (type(v) == "table") then
            toprint = toprint .. tprint(v, indent + 2) .. ",\r\n"
        else
            toprint = toprint .. "\"" .. tostring(v) .. "\",\r\n"
        end
    end
    toprint = toprint .. string.rep(" ", indent - 2) .. "}"
    return toprint
end

local labelFragment = readFragment("saoui:themes/hex2/fragments/label.xml")
print("Loaded " .. labelFragment.name .. " : ")
--print("\t" .. tprint(frag))

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
    if (type(value) == "string") then
        value = "\"" .. value .. "\""
    else
        value = tostring(value)
    end
    return {
        type = jtype,
        expression = value,
        cache = "STATIC"
    }
end

for i = 1, 5 do
    print("Loading " .. i .. " to " .. offset * (i - 1))
    labelFragment.name = labelFragment.name .. " edited from Lua"
    --print("\t" .. )
    labelFragment.y = static(offset * (i - 1))
    labelFragment.x = static(150)
    print("Loading ...")
    local centered = i % 2 == 0
    local r = loadFragment("LuaTestScreen.root", labelFragment, {
        text = tstatic("Label from Lua " .. i .. " centered: " .. tostring(centered)),
        centered = tstatic(centered)
    })
    print("Loaded : " .. tostring(r))
end

labelFragment.y = "scaledheight - 64"
labelFragment.x = static(16)

loadFragment("LuaTestScreen.root", labelFragment, {
    text = {
        expression = "\"Dear \" + username",
        type = "STRING"
    },
})

labelFragment.y = "scaledheight - 32"

for _, child in ipairs(labelFragment.children[2].value.children) do
    if (child.type == "glRectangle") then
        child.value.rgba = static(tonumber("0x076ac0FF"))
    end
end

loadFragment("LuaTestScreen.root", labelFragment, {
    text = {
        expression = "\"Dear \" + username",
        type = "STRING"
    },
})
