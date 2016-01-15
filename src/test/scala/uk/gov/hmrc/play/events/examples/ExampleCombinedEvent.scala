/*
 * Copyright 2016 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.play.events.examples

import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.events.AlertLevel.AlertLevel
import uk.gov.hmrc.play.events.AlertCode
import uk.gov.hmrc.play.events._
import uk.gov.hmrc.play.audit.AuditExtensions._

case class ExampleCombinedEvent(source: String,
                                name: String,
                                tags: Map[String, String],
                                privateData: Map[String, String],
                                data: Map[String, String],
                                alertCode: AlertCode,
                                level: AlertLevel) extends Auditable with Measurable with Loggable with Alertable {

 override def log = "Combined Event occurred"

}

object ExampleCombinedEvent {

  def apply(filingID: String, otherFilingInfo: String, userPassword: String, alertCode: AlertCode)(implicit hc: HeaderCarrier) = new ExampleCombinedEvent(
    source = "test-app",
    name = "CombinedEvent",
    tags = Map(hc.toAuditTags("testConducted", "/your-web-app/example-path/").toSeq: _*),
    privateData = Map("Password" -> userPassword) ++ generateData(filingID, otherFilingInfo),
    data = hc.toAuditDetails() ++ generateData(filingID, otherFilingInfo),
    alertCode = alertCode,
    AlertLevel.WARNING
  )

  def generateData(filingID: String, otherFilingInfo: String): Map[String, String] = {
    Map("Filing ID" -> filingID, "Filing Info" -> otherFilingInfo)
  }
}
