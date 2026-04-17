-- KEYS[1]: 帖子点赞记录的 BitMap 的 key
-- KEYS[2]: 帖子点赞数计数器的 key
-- ARGV[1]: 用户ID

local bitKey = KEYS[1]
local countKey = KEYS[2]
local userId = ARGV[1]

if redis.call('exists',bitKey)==0 or redis.call('exists',countKey)==0 then
    return {-2,-1} --特殊状态码：缓存不存在或已失效
end

local oldBit = redis.call('getbit', bitKey, userId)
local newBit = oldBit == 0 and 1 or 0

-- 切换状态
redis.call('setbit', bitKey, userId, newBit)
local delta = newBit == 1 and 1 or -1
local newCount = redis.call('incrby', countKey, delta)

return {newBit == 1 and 1 or -1,newCount}