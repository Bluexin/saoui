local t, t2 = ...

print("Hello from " .. _VERSION .. " !")
print("Received " .. t .. " :) " .. tostring(...) .. "/" .. t2)
--print("Received " .. t .. " :) " .. table.concat(..., ' '))

local zs = table.pack(...)
for k, v in pairs(zs) do
    print("- Received : " .. k .. "=" .. v)
end

print("test_global : " .. tostring(test_global))
print("test_local : " .. tostring(test_local))

-- setting a global
test_global = test_global or 1
test_global = test_global + 1

-- setting a local
local test_local = test_local or 1
test_local = test_local + 1

print("test_global: " .. test_global)
print("test_local: " .. test_local)
