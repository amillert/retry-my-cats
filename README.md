# retry-my-cats

## Overview
Minimal purely functional `IO` retry `Scala 3` library with `cats-core` and `cats-effect` dependency. It is based on exponential backoff policy. It provides a flexible and comfy syntax that can be utilized on any `IO` effect type.

## Usage
Bring the `retry` syntax into scope:
```scala
import pl.amillert.retry.policy.utils.syntax.RetrySyntaxOps.*
```

Within `cats.IOApp` run some effectful operation and suffix it with the extension `retry` method with desired settings. In its simplest form, it will use overloaded method with default parameters' values:
```scala
def run = io.retry.debug.void
```
`debug` syntax is optional but useful for debugging/visualization purposes; `void` allows discarding operation's result type and consider it as a value of `Unit` type.

To have better control over retrying policy, one can specify the following parameters respectively:
- `maxRetry` - parameter of `Int` type controlling the amount of retries to perform before giving up;
- `giveUpAfter` - max `Duration` to wait and keep retrying before giving up;
- `onFail` - effectful operation to convert value wrapped in a monad to a desired type, defaulting to lazily converting a value to an `IO`.

## Example toy app (simplified)
```scala
import pl.amillert.retry.policy.utils.syntax.*

object Main extends IOApp.Simple:

  import RetrySyntaxOps.*

  val fails    = IO.raiseError[Int](new RuntimeException("random error"))
  val succeeds = IO.pure(42)

  val simulateError = List(fails, succeeds, succeeds, succeeds, fails, fails, succeeds)

  def randIdx = for {
    idx <- IO.pure(Random.nextInt(simulateError.size))
    _   <- IO(s"index extracted: $idx").debug
    res <- simulateError(idx)
  } yield res

  def simulate() =
    IO.sleep(400.millis) >>
      randIdx

  def run = simulate().retry(10, 5.seconds).debug.void
```

1. Import `retry-my-cats` library
2. Create `cats.IOApp`
3. Randomize failure in the chain of operations
4. Run simulation with retry policy - maximum of 10 retries that take no longer than 5 seconds total to succeed
5. Debug result and discard its return type

[![Build, Style & Tests](https://github.com/amillert/retry-my-cats/actions/workflows/check-pr.yml/badge.svg)](https://github.com/amillert/retry-my-cats/actions/workflows/check-pr.yml)