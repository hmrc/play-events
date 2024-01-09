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

package uk.gov.hmrc.play.events.examples

import uk.gov.hmrc.play.events._
import uk.gov.hmrc.play.events.handlers.{DefaultAlertEventHandler, DefaultMetricsEventHandler, EventHandler}
import uk.gov.hmrc.play.events.monitoring._
import uk.gov.hmrc.http.{HttpException, UpstreamErrorResponse}

import scala.concurrent.duration.Duration

trait ExampleEventRecorder extends DefaultEventRecorder {

  override def eventHandlers: Set[EventHandler] = Set(DefaultMetricsEventHandler, DefaultAlertEventHandler)
}

trait ExampleHttpErrorMonitor extends HttpErrorMonitor with ExampleEventRecorder {

  override val source = "TestApp"
}

trait ExampleHttpErrorCountMonitor extends HttpErrorCountMonitor with ExampleEventRecorder {

  override val source = "TestApp"


  override def createHttpErrorCountEvent(source: String, response: UpstreamErrorResponse, alertCode: AlertCode): Measurable = ExampleHttpErrorCountEvent(source, response, alertCode)

  override def createHttpErrorCountEvent(source: String, response: HttpException, alertCode: AlertCode): Measurable = ExampleHttpErrorCountEvent(source, response, alertCode)
}

case class ExampleHttpErrorCountEvent(source: String,
                                      name: String,
                                      data: Map[String, String]) extends Measurable

object ExampleHttpErrorCountEvent {

  private val Source = "TestApp"

  def apply(source: String, response: UpstreamErrorResponse, alertCode: AlertCode): DefaultHttpErrorCountEvent = {
    response match {
      case UpstreamErrorResponse.Upstream4xxResponse(_) =>
        new DefaultHttpErrorCountEvent(
          source = Source,
          name = s"Http4xxErrorCount-$alertCode",
          data = Map (
            "Count" -> "1"
          )
        )
      case UpstreamErrorResponse.Upstream5xxResponse(_) =>
        new DefaultHttpErrorCountEvent(
          source = Source,
          name = s"Http5xxErrorCount-$alertCode",
          data = Map (
            "Count" -> "1"
          )
        )
    }
  }

  def apply(source: String, exception: HttpException, alertCode: AlertCode) = new DefaultHttpErrorCountEvent(
    source = Source,
    name = if (exception.responseCode >= 500) s"Http5xxErrorCount-$alertCode" else s"Http4xxErrorCount-$alertCode",
    data = Map (
      "Count" -> "1"
    )
  )
}

trait ExampleTimer extends Timer with ExampleEventRecorder {

  override val source = "TestApp"

  override def createTimerEvent(alertCode: AlertCode, duration: Duration): Measurable = ExampleTimerEvent(alertCode, duration)
}

case class ExampleTimerEvent(alertCode: String, duration: Duration) extends Measurable {

  override val source = "TestApp"

  override def data: Map[String, String] = Map (
    "Time" -> s"${duration.length}"
  )

  override def name: String = s"Timer-$alertCode"
}
