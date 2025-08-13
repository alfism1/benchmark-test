
## Server Configurations

All servers are containerized using Docker with the following specifications:

### Go Server
- Image: `golang:1.23-alpine`
- Port: 8081
- Resource Limits:
  - CPU: 1.0
  - Memory: 4GB

### Java Server
- Image: `openjdk:21-jdk-slim`
- Port: 8083
- Resource Limits:
  - CPU: 1.0
  - Memory: 4GB

### PHP Server
- Image: `php:8.3-cli`
- Port: 8082
- Resource Limits:
  - CPU: 1.0
  - Memory: 4GB

## Benchmark Tool

We use `wrk` - a modern HTTP benchmarking tool capable of generating significant load when measuring web server performance.

### Basic Command Structure
```bash
wrk -t<threads> -c<connections> -d<duration> <url>
```

Parameters:
- `-t4`: 4 threads for parallel connections
- `-c<N>`: Number of concurrent connections
- `-d<time>`: Duration of the test
- `-s report.lua`: Custom reporting script

### Custom Reporting Script (report.lua)
```lua
done = function(summary, latency, requests)
   io.write("------------------------------\n")
   io.write("HTTP errors:\n")
   
   if summary.status_codes then
      for code, count in pairs(summary.status_codes) do
         io.write(string.format("  %d: %d\n", code, count))
      end
   else
      io.write("  No HTTP status codes recorded\n")
   end

   io.write("\nDetailed latency:\n")
   io.write(string.format("  50%% under %dms\n", latency:percentile(50)))
   io.write(string.format("  90%% under %dms\n", latency:percentile(90)))
   io.write(string.format("  99%% under %dms\n", latency:percentile(99)))

   io.write("\nSocket errors:\n")
   io.write(string.format("  Connect: %d\n", summary.errors.connect))
   io.write(string.format("  Read: %d\n", summary.errors.read))
   io.write(string.format("  Write: %d\n", summary.errors.write))
   io.write(string.format("  Timeout: %d\n", summary.errors.timeout))
end
```

## Benchmark Results

### Go Server (http://localhost:8081)
Test configuration: 4 threads, 500 connections, 10 seconds duration

Results:
- Requests/sec: ~36,363
- Average Latency: 15.84ms
- Latency Stdev: 12.82ms
- Socket Errors:
  - Read: 699
  - Connect: 0
  - Write: 0
  - Timeout: 0
- Transfer Rate: 4.44MB/sec

### PHP Server (http://localhost:8082)
Test configuration: 4 threads, 500 connections, 30 seconds duration

Results:
- Requests/sec: ~188
- Average Latency: 59.21ms
- Latency Stdev: 56.74ms
- Socket Errors:
  - Read: 52,571
  - Write: 497
  - Connect: 0
  - Timeout: 0
- Transfer Rate: 32.07KB/sec

## Performance Comparison

Based on initial testing, the Go server significantly outperforms the PHP server in all metrics:
- ~193x higher request throughput
- ~3.7x lower average latency
- Significantly fewer socket errors
- Higher transfer rate

*Note: Java server benchmarks pending - run the test commands above to compare all three servers.*

## Running the Tests

1. Start the servers:
```bash
docker-compose up
```

2. Run benchmark tests:
```bash
# Test Go server
wrk -t4 -c500 -d10s -s report.lua http://localhost:8081/

# Test Java server
wrk -t4 -c500 -d10s -s report.lua http://localhost:8083/

# Test PHP server
wrk -t4 -c500 -d30s -s report.lua http://localhost:8082/
```

## Notes

- The Go server handles high concurrency much better than the PHP server
- PHP server shows significant socket errors under high load
- All servers are configured with identical resource limits (1 CPU, 4GB RAM)
- Java server uses OpenJDK 21 with built-in HTTP server and thread pool
- Results may vary based on hardware and network conditions

## Future Improvements

1. Test with different concurrency levels
2. Add more complex endpoints for real-world scenarios
3. Implement connection pooling
4. Test with different payload sizes
5. Add database interaction tests