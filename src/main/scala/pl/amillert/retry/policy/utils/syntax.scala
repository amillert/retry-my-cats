package pl.amillert.retry.policy.utils.syntax

import cats.FlatMap
import cats.syntax.functor._

extension [F[_]: FlatMap, A](fa: F[A])

  def debug = for {
    a <- fa
    t = Thread.currentThread().getName()
    _ = println(s"[$t] $a")
  } yield a
