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

case class VariableReturn(monthId: String, monthlyRate: Double)

case class OffsetReturns(origin: Returns, offset: Int) extends Returns

object Returns {
  @tailrec
  def monthlyRate(returns: Returns, month: Int): Either[RetCalcError, Double] =
    returns match {
      case FixedReturns(annualRate) => Right(annualRate / 12)
      case VariableReturns(returns) =>
        if (returns.isDefinedAt(month))
          Right(returns(month).monthlyRate)
        else
          Left(RetCalcError.ReturnMonthOutOfBounds(month, returns.size - 1))
      case OffsetReturns(origin, offset) => monthlyRate(origin, month + offset)
    }

  def fromEquityAndInflationData(
      equities: Vector[EquityData],
      inflations: Vector[InflationData]
  ): VariableReturns = {
    VariableReturns(
      equities
        .zip(inflations)
        .sliding(2)
        .collect {
          case (prevEquity, prevInflation) +: (equity, inflation) +: Vector() =>
            val inflationRate   = inflation.value / prevInflation.value
            val totalReturn     = (equity.value + equity.monthlyDividend) / prevEquity.value
            val realTotalReturn = totalReturn - inflationRate
            VariableReturn(equity.monthId, realTotalReturn)
        }
        .toVector
    )
  }
}
