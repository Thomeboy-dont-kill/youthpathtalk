-- verify_code_validate.lua
-- 功能：原子性验证验证码，获取并删除，防止并发重复验证
-- 参数：KEYS[1] - 验证码key，ARGV[1] - 用户输入的验证码
-- 返回：1 - 验证成功并删除，0 - 验证失败或不存在

local key = KEYS[1]
local inputCode = ARGV[1]

-- 获取存储的验证码
local storedCode = redis.call('get', key)

-- 不存在或已过期
if not storedCode then
    return 0
end

-- 不匹配
if storedCode ~= inputCode then
    return 0
end

-- 匹配成功，删除key
redis.call('del', key)
return 1