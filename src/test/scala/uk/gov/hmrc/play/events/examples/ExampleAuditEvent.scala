/*
 * Copyright 2020 HM Revenue & Customs
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

import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.events.Auditable
import uk.gov.hmrc.play.audit.AuditExtensions._

case class ExampleAuditEvent(source: String,
                             name: String,
                             tags: Map[String, String],
                             privateData: Map[String, String]) extends Auditable

object ExampleAuditEvent {

  def apply(testCount: Int, testName: String)(implicit hc: HeaderCarrier): ExampleAuditEvent =
    ExampleAuditEvent(
      source = "example-source",
      name = "test-conducted",
      tags = Map(hc.toAuditTags("testConducted", "/your-web-app/example-path/").toSeq: _*),
      privateData = hc.toAuditDetails() ++ buildAuditData(testCount, testName)
    )

  private def buildAuditData(count: Int, name: String) = {
      Map(
        "Test Name" -> name.toString,
        "Tests Run" -> count.toString
      )
  }
}
