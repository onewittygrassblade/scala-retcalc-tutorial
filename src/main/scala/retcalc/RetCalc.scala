package retcalc

import scala.annotation.tailrec

object RetCalc {
  def futureCapital(
                     monthlyInterestRate: Double,
                     nbOfMonths: Int,
                     netIncome: Int,
                     monthlyExpenses: Int,
                     initialCapital: Double): Double = {

    val monthlySavings = netIncome - monthlyExpenses

    (0 until nbOfMonths).foldLeft(initialCapital)(
      (accumulated, _) => accumulated * (1 + monthlyInterestRate) + monthlySavings
    )
  }

  def simulatePlan(
                    monthlyInterestRate: Double,
                    nbOfMonthsSaving: Int,
                    nbOfMonthsInRetirement: Int,
                    netIncome: Int,
                    monthlyExpenses: Int,
                    initialCapital: Double): (Double, Double) = {

    val capitalAtRetirement = futureCapital(
      monthlyInterestRate = monthlyInterestRate,
      nbOfMonths = nbOfMonthsSaving,
      netIncome = netIncome,
      monthlyExpenses = monthlyExpenses,
      initialCapital = initialCapital)

    val capitalAfterDeath = futureCapital(
      monthlyInterestRate = monthlyInterestRate,
      nbOfMonths = nbOfMonthsInRetirement,
      netIncome = 0,
      monthlyExpenses = monthlyExpenses,
      initialCapital = capitalAtRetirement)

    (capitalAtRetirement, capitalAfterDeath)
  }

  def nbOfMonthsSaving(
                        monthlyInterestRate: Double,
                        nbOfMonthsInRetirement: Int,
                        netIncome: Int,
                        monthlyExpenses: Int,
                        initialCapital: Double): Int = {
    @tailrec
    def loop(months: Int): Int = {
      val (_, capitalAfterDeath) = simulatePlan(
        monthlyInterestRate = monthlyInterestRate,
        nbOfMonthsSaving = months,
        nbOfMonthsInRetirement = nbOfMonthsInRetirement,
        netIncome = netIncome,
        monthlyExpenses = monthlyExpenses,
        initialCapital = initialCapital)

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
