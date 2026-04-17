-- KEYS[1]: user:view:history:{userId}
-- ARGV[1]: 帖子ID (member)
-- ARGV[2]: 当前时间戳 (score)
-- ARGV[3]: 最大保留条数 (limit)
-- ARGV[4]: 过期时间（秒）

redis.call('ZADD', KEYS[1], ARGV[2], ARGV[1])
redis.call('ZREMRANGEBYRANK', KEYS[1], 0, - (tonumber(ARGV[3]) + 1))
redis.call('EXPIRE', KEYS[1], tonumber(ARGV[4]))