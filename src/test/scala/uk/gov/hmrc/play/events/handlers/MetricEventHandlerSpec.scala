/*
 * Copyright 2023 HM Revenue & Customs
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

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.events.examples.ExampleMetricEvent
import uk.gov.hmrc.play.events.{Loggable, Measurable}
import scala.concurrent.ExecutionContext.Implicits.global

class MetricEventHandlerSpec extends AnyWordSpecLike with Matchers {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  "MetricsEventHandler" should {

    "handle Metric events" in new MetricsEventHandler {

      val metricsEvent: ExampleMetricEvent = ExampleMetricEvent("TestFilingID", "TestFilingType")

      handle(metricsEvent)

      override def handleMeasurable(measurable: Measurable): Unit = {
        measurable shouldBe metricsEvent
      }
    }

    "not handle Logger events" in new MetricsEventHandler {
      val loggerEvent: Loggable = new Loggable {
        override def log: String = "test"
      }

      handle(loggerEvent)

      override def handleMeasurable(measurable: Measurable): Unit = {
        fail("Should never call handle for Logger event")
      }
    }

  }

}
