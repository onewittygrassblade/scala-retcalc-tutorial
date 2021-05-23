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
  ): Double = {

    val monthlySavings = netIncome - monthlyExpenses

    (0 until nbOfMonths).foldLeft(initialCapital)((accumulated, month) =>
      accumulated * (1 + Returns.monthlyRate(returns, month)) + monthlySavings
    )
  }

  def simulatePlan(
      returns: Returns,
      params: RetCalcParams,
      nbOfMonthsSaving: Int
  ): (Double, Double) = {
    import params._

    val capitalAtRetirement = futureCapital(
      returns = returns,
      nbOfMonths = nbOfMonthsSaving,
      netIncome = netIncome,
      monthlyExpenses = monthlyExpenses,
      initialCapital = initialCapital
    )

    val capitalAfterDeath = futureCapital(
      returns = OffsetReturns(returns, nbOfMonthsSaving),
      nbOfMonths = nbOfMonthsInRetirement,
      netIncome = 0,
      monthlyExpenses = monthlyExpenses,
      initialCapital = capitalAtRetirement
    )

    (capitalAtRetirement, capitalAfterDeath)
  }

  def nbOfMonthsSaving(returns: Returns, params: RetCalcParams): Int = {
    import params._

    @tailrec
    def loop(months: Int): Int = {
      val (_, capitalAfterDeath) = simulatePlan(returns, params, months)
      if (capitalAfterDeath > 0.0)
        months
      else
        loop(months + 1)
    }

    if (netIncome > monthlyExpenses)
      loop(0)
    else
      Int.MaxValue
  }
}
