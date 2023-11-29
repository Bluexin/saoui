local modVersion, resourceLocation = ...

print("Hello from " .. _VERSION .. " !")
print("Running on " .. modVersion .. " from " .. resourceLocation)

for _, fragName in pairs({ "label.xml" }) do
    local frag = fragment("saoui:themes/hex2/fragments/" .. fragName)
    print("Loaded " .. fragName .. " : ")
    print("\t" .. tostring(frag))
    print("\t name: " .. frag.name)
    --print("\t" .. )
end

--local frag = loadFragment("saoui:themes/hex2/fragments/" .. fragName)
--print("Loaded " .. fragName " : " .. frag:tostring())

