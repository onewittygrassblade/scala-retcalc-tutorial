package retcalc

import scala.annotation.tailrec

case class RetCalcParams(
    nbOfMonthsInRetirement: Int,
    netIncome: Int,
    monthlyExpenses: Int,
    initialCapital: Double
)

object RetCalc {
  def futureCapital(
      returns: Returns,
      nbOfMonths: Int,
      netIncome: Int,
      monthlyExpenses: Int,
      initialCapital: Double
  ): Either[RetCalcError, Double] = {

    val monthlySavings = netIncome - monthlyExpenses

    (0 until nbOfMonths).foldLeft[Either[RetCalcError, Double]](Right(initialCapital))((accumulated, month) =>
      for {
        acc         <- accumulated
        monthlyRate <- Returns.monthlyRate(returns, month)
      } yield acc * (1 + monthlyRate) + monthlySavings
    )
  }

  def simulatePlan(
      returns: Returns,
      params: RetCalcParams,
      nbOfMonthsSaving: Int
  ): Either[RetCalcError, (Double, Double)] = {
    import params._

    for {
      capitalAtRetirement <- futureCapital(
        returns = returns,
        nbOfMonths = nbOfMonthsSaving,
        netIncome = netIncome,
        monthlyExpenses = monthlyExpenses,
        initialCapital = initialCapital
      )
      capitalAfterDeath <- futureCapital(
        returns = OffsetReturns(returns, nbOfMonthsSaving),
        nbOfMonths = nbOfMonthsInRetirement,
        netIncome = 0,
        monthlyExpenses = monthlyExpenses,
        initialCapital = capitalAtRetirement
      )
    } yield (capitalAtRetirement, capitalAfterDeath)
  }

  def nbOfMonthsSaving(returns: Returns, params: RetCalcParams): Either[RetCalcError, Int] = {
    import params._

    @tailrec
    def loop(months: Int): Either[RetCalcError, Int] = {
      simulatePlan(returns, params, months) match {
        case Right((_, capitalAfterDeath)) =>
          if (capitalAfterDeath > 0.0)
            Right(months)
          else
            loop(months + 1)
        case Left(err) => Left(err)
      }
    }

    if (netIncome > monthlyExpenses)
      loop(0)
    else
      Left(RetCalcError.MoreExpensesThanIncome(netIncome, monthlyExpenses))
  }
}
