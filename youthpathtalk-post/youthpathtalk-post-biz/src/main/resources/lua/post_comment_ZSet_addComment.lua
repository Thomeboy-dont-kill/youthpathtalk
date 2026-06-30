-- KEYS[1] = hot rank key
-- ARGV[1] = member (commentId)
-- ARGV[2] = score (hotScore)
-- ARGV[3] = limit (20)

redis.call('ZADD', KEYS[1], ARGV[2], ARGV[1])

-- 只保留 TOP N
redis.call('ZREMRANGEBYRANK', KEYS[1], 0, -(tonumber(ARGV[3]) + 1))

return 1