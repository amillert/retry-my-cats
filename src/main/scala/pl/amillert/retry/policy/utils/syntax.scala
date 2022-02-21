package pl.amillert.retry.policy.utils.syntax

import cats.FlatMap
import cats.syntax.functor._

import cats.effect.IO

import scala.concurrent.duration.FiniteDuration

extension [F[_]: FlatMap, A](fa: F[A])

  def debug = for {
    a <- fa
    t = Thread.currentThread().getName()
    _ = println(s"[$t] $a")
  } yield a

object RetrySyntaxOps:
  import pl.amillert.retry.policy.domain.ExponentialBackoffRetry

  extension [A](io: IO[A])

    def retry(maxRetry: Int, giveUpAfter: FiniteDuration, onFail: A => IO[A]): IO[A] =
      new ExponentialBackoffRetry().retry(maxRetry, giveUpAfter, onFail)(io)

    def retry(maxRetry: Int, giveUpAfter: FiniteDuration): IO[A] =
      new ExponentialBackoffRetry().retry(maxRetry, giveUpAfter)(io)

    def retry(maxRetry: Int): IO[A] = new ExponentialBackoffRetry().retry(maxRetry)(io)

    def retry: IO[A] = new ExponentialBackoffRetry().retry()(io)
