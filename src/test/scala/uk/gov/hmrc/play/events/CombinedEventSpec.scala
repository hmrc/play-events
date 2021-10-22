/*
 * Copyright 2021 HM Revenue & Customs
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

package uk.gov.hmrc.play.events

import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.events.examples.ExampleCombinedEvent
import uk.gov.hmrc.play.events.handlers._
import scala.concurrent.ExecutionContext.Implicits.global

class CombinedEventSpec extends AnyWordSpecLike with Matchers with MockitoSugar {

  "CombinedEvent" should {

    "be handled by all event handlers" in {
      implicit val hc = new HeaderCarrier()

      val combinedEvent = ExampleCombinedEvent("TestFilingId", "More Info", "cleverPassword", "Unknown")

      val mockAuditConnector = mock[AuditConnector]

      val assertingAuditEventHandler = new AuditEventHandler {
        override val auditConnector: AuditConnector = mockAuditConnector
      }

      val assertingLoggerEventHandler = new LoggerEventHandler {
        override def handleLoggable(loggable: Loggable) = {
          loggable shouldBe combinedEvent
        }
      }

      val assertingMetricEventHandler = new MetricsEventHandler {
        override def handleMeasurable(measurable: Measurable) = {
          measurable shouldBe combinedEvent
        }
      }

      val assertingAlertEventHandler = new AlertEventHandler {
        override def handleAlertable(alertable: Alertable) = {
          alertable shouldBe combinedEvent
        }
      }

      val testRecorder = new EventRecorder {
        override def eventHandlers: Set[EventHandler] =
          Set(
            assertingAuditEventHandler,
            assertingLoggerEventHandler,
            assertingMetricEventHandler,
            assertingAlertEventHandler
          )
      }

      testRecorder.record(combinedEvent)

      //Calling a nested future above, need to wait for it
      Thread.sleep(200)

      verify(mockAuditConnector).sendEvent(combinedEvent.event)
    }
  }

}
