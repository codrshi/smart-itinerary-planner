local key = KEYS[1]
local tokens_per_period = tonumber(ARGV[1])      -- size of bucket or max tokens in a bucket
local period = tonumber(ARGV[2])                 -- time period in ms
local now = tonumber(ARGV[3])

local redisData = redis.call("HMGET", key, "tokens", "timestamp")
local tokens = tonumber(redisData[1])
local timestamp = tonumber(redisData[2])

if not tokens then                  -- initialize the bucket for given user if it doesn't exist
    tokens = tokens_per_period
    timestamp = now
end

local elapsed_time = now - timestamp
local tokens_to_add = elapsed_time * (tokens_per_period / period)  -- (tokens_per_period / period) means tokens to add per 1ms
tokens = math.min(tokens + tokens_to_add, tokens_per_period)

if tokens < 1 then          -- all tokens consumed, too many requests
    return -1
else
    tokens = tokens - 1
    redis.call("HMSET", key, "tokens", tokens, "timestamp", now)
    redis.call("EXPIRE", key, period)
    return tokens
end
