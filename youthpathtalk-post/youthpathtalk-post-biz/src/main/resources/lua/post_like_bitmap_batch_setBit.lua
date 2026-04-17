-- KEYS[1]: 位图 key
-- ARGV: 用户 ID 列表（可变参数）

local bitKey = KEYS[1]
for _, userId in ipairs(ARGV) do
    redis.call('setbit', bitKey, userId, 1)
end