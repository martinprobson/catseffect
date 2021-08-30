package net.martinprobson.catseffect.filecopy

sealed trait DomainError

object DomainError {
    case class WrongArgumentCount(message :String) extends DomainError
    case object SourceAndDestinationTheSame extends DomainError
    case object SourceFileDoesNotExist extends DomainError
    case object DestinationFileExists extends DomainError
}
