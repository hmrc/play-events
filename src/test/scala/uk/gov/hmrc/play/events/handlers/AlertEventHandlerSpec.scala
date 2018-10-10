/*
 * Copyright 2018 HM Revenue & Customs
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

package uk.gov.hmrc.play.events.handlers

import org.scalatest.{Matchers, WordSpec}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.events.examples.ExampleAlertEvent
import uk.gov.hmrc.play.events.{Alertable, Loggable}
import scala.concurrent.ExecutionContext.Implicits.global

class AlertEventHandlerSpec extends WordSpec with Matchers {

  implicit val hc = HeaderCarrier()

  "AlertEventHandler" should {

    "handle Alert events" in new AlertEventHandler {

      val alertEvent = ExampleAlertEvent(new IllegalStateException("There ain't no state!"), "Unknown")

      handle(alertEvent)

      override def handleAlertable(alertable: Alertable): Unit = {
        alertable shouldBe alertEvent
      }
    }

    "not handle Logger events" in new AlertEventHandler {
      val loggerEvent = new Loggable {
        override def log: String = "test"
      }

      handle(loggerEvent)

      override def handleAlertable(alertable: Alertable): Unit = {
        fail("Should never call handle for Logger event")
      }
    }

  }

}
