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

import uk.gov.hmrc.play.events.{Alertable, AlertCode}
import uk.gov.hmrc.play.events.AlertLevel._

case class ExampleAlertEvent(source: String,
                             name: String,
                             level: AlertLevel,
                             alertCode: AlertCode,
                             data: Map[String, String]) extends Alertable {

}

object ExampleAlertEvent {

  def apply(exception: Exception, alertCode: AlertCode) = new ExampleAlertEvent(
    source = "TestApp",
    name = "External API Alert",
    level = CRITICAL,
    alertCode = alertCode,
    data = Map (
      "error" -> exception.getMessage,
      "trace" -> exception.getStackTrace.toString
    )
  )

}
