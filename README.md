AdventOfKotlin2017
===

Solutions to the [2017 Advent Of Code](http://adventofcode.com/2017) problems, written in Kotlin.

My solutions for previous years can be found here:
- [AdventOfCode2016](https://github.com/damianw/AdventOfKotlin2016)
- [AdventOfCode2015](https://github.com/damianw/AdventOfKotlin)

Try not to take anything too seriously. It's all for `fun`, after all. :)

### Building

```
$ ./gradlew clean assemble
```

A `AdventOfKotlin2017` binary will be output to the `build` directory.

### Running

Running the `AdventOfKotlin2017` binary will run all the current solutions. You may optionally specify the days to run (comma separated, e.g. `1,2,3`).

```
$ build/AdventOfKotlin2017 --help
Usage: AdventOfKotlin2016 [options]
  Options:
    --days, -d
       Days of the advent calendar to solve
    --help, -h
       Prints usage information
       Default: false

$ build/AdventOfKotlin2017
========
Day 1
========
-> Time elapsed: 0 seconds
-> Part 1: 1158
-> Part 2: 1132

========
Day 2
========
-> Time elapsed: 0 seconds
-> Part 1: 45972
-> Part 2: 326

========
Day 3
========
-> Time elapsed: 0 seconds
-> Part 1: 326
-> Part 2: 363010

========
Day 4
========
-> Time elapsed: 0 seconds
-> Part 1: 466
-> Part 2: 251

========
Day 5
========
-> Time elapsed: 0 seconds
-> Part 1: 358131
-> Part 2: 25558839
```

Enjoy?
