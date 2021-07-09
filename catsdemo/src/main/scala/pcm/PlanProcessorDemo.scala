package pcm

case class ExcelServicePlan(
                             vendorId: Int,
                             delegatedServicePlans: Seq[ExcelServicePlan] = Seq.empty)

case class InHouseServicePlan(vendorId: Int)

class ServicePlanManager {
  def delegate(vendorId: Int, inHousePlans: Seq[InHouseServicePlan], resellerId: Int): Unit = {
    println(s"performing delegation: vendorId = $vendorId, inHousePlans = $inHousePlans, resellerId = $resellerId")
  }
}

object Diff {
  def getValue(plan: ExcelServicePlan): ExcelServicePlan = plan
}

object PlanProcessorDemo {
  private val servicePlanManager: ServicePlanManager = new ServicePlanManager()

  val ProviderAccountId: Int = 1

  def main(args: Array[String]): Unit = {
    val excelServicePlans = Seq(
      ExcelServicePlan(2, Seq(ExcelServicePlan(20))),
      ExcelServicePlan(3, Seq(ExcelServicePlan(30))))

    delegatePlans(
      excelServicePlans,
      excelServicePlan => InHouseServicePlan(excelServicePlan.vendorId))
  }

  private def delegatePlans(plans: Seq[ExcelServicePlan], planMapper: ExcelServicePlan => InHouseServicePlan): Unit = {
    plans.view
      .flatMap(plan => plan.delegatedServicePlans.map(Diff.getValue))
      .groupMap(plan => plan.vendorId)(planMapper)
      .foreach {
        case (resellerId, inHousePlans) =>
          servicePlanManager.delegate(ProviderAccountId, inHousePlans.toSeq, resellerId)
      }
  }
}
