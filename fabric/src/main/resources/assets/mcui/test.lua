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

local frag = readFragment("saoui:themes/hex2/fragments/label.xml")
print("Loaded " .. frag.name .. " : ")
--print("\t" .. tprint(frag))

for i = 1, 5 do
    print("Loading " .. i .. " to " .. offset * (i - 1))
    frag.name = frag.name .. " edited from Lua"
    --print("\t" .. )
    frag.y = {
        cache = "STATIC",
        expression = tostring(offset * (i - 1))
    }
    frag.x = {
        cache = "STATIC",
        expression = tostring(20)
    }
    print("Loading ...")
    local r = loadFragment("LuaTestScreen.root", frag, {
        text = {
            type = "STRING",
            expression = "\"Label from Lua " .. i .. "\""
        }
    })
    print("Loaded : " .. tostring(r))
end
