package pl.amillert.retry.policy.domain

import cats.effect.IO

import scala.annotation.tailrec
import scala.concurrent.duration.*
import scala.util.Random

import pl.amillert.retry.policy.utils.syntax.debug

sealed trait Retry[F[_], A]:
  def minBackoff: Long

  def retry(
    maxRetry: Int = 3,
    giveUpAfter: FiniteDuration = 5.seconds,
    onFail: A => F[A]
  )(fa: F[A]): F[A]

class ExponentialBackoffRetry[A]() extends Retry[IO, A]:
  private def exponentialMultiplier(cnt: Int) = scala.math.pow(2, cnt)

  private def exponentialBackoffWithJitter(cnt: Int) =
    (exponentialMultiplier(cnt) * (1 + Random.nextFloat()) * minBackoff).round.millis

  override val minBackoff = 200

  def retry(
    maxRetry: Int,
    giveUpAfter: FiniteDuration,
    onFail: A => IO[A] = IO.apply
  )(fa: IO[A]): IO[A] =
    import pl.amillert.retry.policy.domain.RetryException.*

    def retryRec(cnt: Int = 1)(fa: IO[A]): IO[A] = fa.handleErrorWith(errorHandler(cnt))

    def errorHandler(cnt: Int) = (_: Throwable) =>
      if cnt > maxRetry then IO.raiseError(RetriesExceededException)
      else
        IO.sleep(exponentialBackoffWithJitter(cnt)) >>
          IO(s"retrying ${cnt} time").debug >>
          retryRec(cnt + 1)(fa.flatMap(onFail))

    retryRec()(fa)
      .timeoutTo(giveUpAfter, IO.raiseError(TimedOutException))
      .onError {
        case e: RetriesExceededException.type => e.reRaise
        case e: TimedOutException.type        => e.reRaise
        case _                                => UnknownRetryException.reRaise
      }
