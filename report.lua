done = function(summary, latency, requests)
  io.write("------------------------------\n")
  io.write("HTTP errors:\n")
  
  -- Add nil check before accessing status_codes
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