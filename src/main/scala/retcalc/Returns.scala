package retcalc

import scala.annotation.tailrec

sealed trait Returns

case class FixedReturns(annualRate: Double) extends Returns

case class VariableReturns(returns: Vector[VariableReturn]) extends Returns {
  def fromUntil(monthIdFrom: String, monthIdUntil: String): VariableReturns =
    VariableReturns(
      returns
        .dropWhile(_.monthId != monthIdFrom)
        .takeWhile(_.monthId != monthIdUntil)
    )

}

case class VariableReturn(monthId: String, monthlyRate: Double) extends Returns

case class OffsetReturns(origin: Returns, offset: Int) extends Returns

object Returns {
  @tailrec
  def monthlyRate(returns: Returns, month: Int): Double =
    returns match {
      case FixedReturns(annualRate)      => annualRate / 12
      case VariableReturns(returns)      => returns(month % returns.length).monthlyRate
      case OffsetReturns(origin, offset) => monthlyRate(origin, month + offset)
    }
}
