package retry.util

import cats.FlatMap
import cats.syntax.functor._
import cats.effect.IO

extension [F[_]: FlatMap, A](fa: F[A])

  def debug = for {
    a <- fa
    t = Thread.currentThread().getName()
    _ = println(s"[$t] $a")
  } yield a
