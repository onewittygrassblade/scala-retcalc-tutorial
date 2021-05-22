package retcalc

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
}
