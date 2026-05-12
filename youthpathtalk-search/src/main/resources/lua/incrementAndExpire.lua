-- KEYS[1]: 计数器 key
-- ARGV[1]: 过期时间（秒）
local current = redis.call('INCR', KEYS[1])
-- 可能出现key 永不过期
if current == 1 then
    redis.call('EXPIRE', KEYS[1], ARGV[1])
end
return current