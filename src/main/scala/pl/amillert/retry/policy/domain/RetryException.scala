package pl.amillert.retry.policy.domain

import cats.effect.IO

import pl.amillert.retry.policy.utils.syntax.debug

sealed trait RetryException extends RuntimeException

/** `object` around unnecessary */
object RetryException:
  case object RetriesExceededException extends RetryException
  case object TimedOutException        extends RetryException
  case object UnknownRetryException    extends RetryException

/** `enum` looses underlying types */
// enum RetryException extends RuntimeException:
//   case RetriesExceededException, TimedOutException, UnknownRetryException

extension [A, E <: RetryException](e: E) def reRaise = IO.raiseError[A](e).debug
