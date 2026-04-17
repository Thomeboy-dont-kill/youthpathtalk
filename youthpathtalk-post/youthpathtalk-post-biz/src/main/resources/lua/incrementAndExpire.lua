local key = KEYS[1]
local ttl = tonumber(ARGV[1])

local current = redis.call('INCR',key)
redis.call('EXPIRE',key,ttl)
return current