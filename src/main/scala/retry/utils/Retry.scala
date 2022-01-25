package retry.utils

import cats.effect.IO

import scala.annotation.tailrec
import scala.concurrent.duration.*
import scala.util.Random

import retry.utils._

trait Retry[A]:
  case object RetriesExceededException extends RuntimeException
  case object TimedOutException        extends RuntimeException

  def minBackoff: Long

  def retry(
    maxRetry: Int = 3,
    giveUpAfter: FiniteDuration = 5.seconds,
    onFail: A => IO[A] = IO.apply
  )(io: IO[A]): IO[A]

class ExponentialBackOffRetry[A](override val minBackoff: Long = 200) extends Retry[A]:
  private def exponentialMultiplier(cnt: Int) = scala.math.pow(2, cnt)

  private def exponentialBackoffWithJitter(cnt: Int) =
    (exponentialMultiplier(cnt) * (1 + Random.nextFloat()) * minBackoff).round.millis

  override def retry(maxRetry: Int, giveUpAfter: FiniteDuration, onFail: A => IO[A])(
    io: IO[A]
  ): IO[A] =

    def errorHandler(cnt: Int) = (_: Throwable) =>
      if cnt > maxRetry then IO.raiseError(RetriesExceededException)
      else
        IO.sleep(exponentialBackoffWithJitter(cnt)) >>
          IO(s"let's retry ${cnt} time").debug >>
          retryRec(cnt + 1, io.flatMap(onFail))

    def retryRec(cnt: Int, io: IO[A]): IO[A] =
      (IO("try io before retrying") >> io)
        .handleErrorWith(errorHandler(cnt))

    retryRec(1, io)
