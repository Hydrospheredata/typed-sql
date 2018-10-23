package croodie

import shapeless.Witness

object fields {

  def \(wt: Witness): wt.T = wt.value

}
