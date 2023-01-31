# membase (Memorybase)
Membase is an open-source library that enables developers to create more optimized datasets that are stored in memory.

This is a developer API not meant to be used by end-users.

# Table of contents
- [Usage](#usage)
- [Why Membase?](#why-membase)
- [API](#api)
- [Credits](#credits)
- [License](#license)

## Usage
Gradle:
```groovy
repositories {
    maven { url "https://nexus.tridentgames.net/repository/public/" }
}

dependencies {
    implementation("com.github.tridentgames:memstore:1.0.0")
}
```

## Why Membase?
Memory is a very fast storage medium, although it is an expensive option sometimes it's just required.
This is when Membase comes into play, it is an in-memory database that is optimized for speed and memory usage.

You can query items out by using an index and filter them with equals or contains.

I ran a benchmark on my machine and below are the results:
```
MemoryStoreBenchmarkTest.arrayListBenchmark: [measured 500 out of 505 rounds, threads: 1 (sequential)]
 round: 0.04 [+- 0.00], round.block: 0.00 [+- 0.00], round.gc: 0.00 [+- 0.00], GC.calls: 6, GC.time: 0.01, time.total: 22.59, time.warmup: 0.33, time.bench: 22.27
MemoryStoreBenchmarkTest.memoryStoreBenchmark: [measured 500 out of 505 rounds, threads: 1 (sequential)]
 round: 0.00 [+- 0.00], round.block: 0.00 [+- 0.00], round.gc: 0.00 [+- 0.00], GC.calls: 15, GC.time: 0.02, time.total: 1.22, time.warmup: 0.07, time.bench: 1.15
 ```

## API
Coming soon.

## Credits
This library was initially created by [JParams](https://github.com/jparams).

## License
This project is licensed under the [Apache License Version 2.0](../LICENSE).

## Special Thanks
Special Thanks To:
-------------

[![YourKit-Logo](https://www.yourkit.com/images/yklogo.png)](https://www.yourkit.com/)
[YourKit](https://www.yourkit.com/) support open source projects of all kinds, with their elegant and innotative tools to help you profile your applications to give them an amazing edge with knowing what is performing optimally and what needs work, they also provide YouMonitor which helps you with build management to make a better CI/CD environment, without the sleepless nights. We thank them for granting us with an Open Source Software license so we can make our software the best it can be!

[.NET Profiler](https://www.yourkit.com/.net/profiler)
[Java Profiler](https://www.yourkit.com/java/profiler)
[YouMonitor](https://www.yourkit.com/youmonitor/)
